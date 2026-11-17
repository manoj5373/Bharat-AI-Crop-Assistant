import sys
from pathlib import Path

import torch
import importlib.metadata as metadata

from executorch.backends.xnnpack.partition.xnnpack_partitioner import (
    XnnpackPartitioner,
)
from executorch.exir import to_edge_transform_and_lower

PROJECT_DIR = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_DIR))

from src.mulberry_model import create_model

CHECKPOINT_PATH = PROJECT_DIR / "models" / "mulberry" / "best_mulberry_model.pth"
OUTPUT_PATH = PROJECT_DIR / "models" / "mulberry" / "mulberry_classifier_151.pte"


def main():
    print("ExecuTorch version:", metadata.version("executorch"))
    print("PyTorch version:", torch.__version__)

    if OUTPUT_PATH.exists():
        raise FileExistsError(
            f"Output already exists; refusing to overwrite: {OUTPUT_PATH}"
        )

    print("\n1. Loading trained checkpoint...")
    checkpoint = torch.load(
        CHECKPOINT_PATH,
        map_location="cpu",
        weights_only=True,
    )

    model = create_model(num_classes=3, pretrained=False)
    model.load_state_dict(checkpoint["model_state_dict"])
    model.eval()

    print("Classes:", checkpoint["class_names"])

    print("\n2. Creating example input...")
    example_inputs = (torch.randn(1, 3, 224, 224),)

    with torch.no_grad():
        reference_output = model(*example_inputs)

    print("Reference output shape:", tuple(reference_output.shape))

    print("\n3. Exporting PyTorch model...")
    exported_program = torch.export.export(model, example_inputs)

    print("\n4. Lowering to ExecuTorch with XNNPACK...")
    executorch_program = to_edge_transform_and_lower(
        exported_program,
        partitioner=[XnnpackPartitioner()],
    ).to_executorch()

    print("\n5. Saving separate 1.5.1 model...")
    with open(OUTPUT_PATH, "wb") as file:
        file.write(executorch_program.buffer)

    print("\nEXPORT COMPLETED!")
    print("Output:", OUTPUT_PATH)
    print(
        "File size:",
        round(OUTPUT_PATH.stat().st_size / (1024 * 1024), 2),
        "MB",
    )


if __name__ == "__main__":
    main()
