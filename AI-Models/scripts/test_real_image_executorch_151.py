
import sys
from pathlib import Path

import torch
from executorch.runtime import Runtime

PROJECT_DIR = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_DIR))

from src.mulberry_model import create_model
from src.mulberry_dataset import MulberryDataset


CHECKPOINT_PATH = PROJECT_DIR / "models/mulberry/best_mulberry_model.pth"
PTE_PATH = PROJECT_DIR / "models/mulberry/mulberry_classifier_151.pte"
MANIFEST_PATH = PROJECT_DIR / "data/processed/mulberry_manifest/manifest.csv"

CLASS_NAMES = [
    "Disease Free leaves",
    "Leaf Rust",
    "Leaf spot",
]


def main():
    print("1. Loading held-out test dataset...")

    dataset = MulberryDataset(
        manifest_path=MANIFEST_PATH,
        split="test",
    )

    # Select the first held-out test image
    image_tensor, true_label = dataset[0]
    image_batch = image_tensor.unsqueeze(0)

    image_path = dataset.samples[0]["path"]

    print("Image:", image_path)
    print("Actual class:", CLASS_NAMES[true_label])
    print("Input shape:", tuple(image_batch.shape))

    print("\n2. Loading original PyTorch model...")

    checkpoint = torch.load(
        CHECKPOINT_PATH,
        map_location="cpu",
        weights_only=True,
    )

    original_model = create_model(
        num_classes=3,
        pretrained=False,
    )

    original_model.load_state_dict(
        checkpoint["model_state_dict"]
    )
    original_model.eval()

    print("\n3. Loading ExecuTorch model...")

    runtime = Runtime.get()
    program = runtime.load_program(str(PTE_PATH))
    method = program.load_method("forward")

    print("\n4. Running both models...")

    with torch.no_grad():
        original_output = original_model(image_batch)

    executorch_output = method.execute([image_batch])[0]

    original_prediction = original_output.argmax(dim=1).item()
    executorch_prediction = executorch_output.argmax(dim=1).item()

    difference = torch.max(
        torch.abs(original_output - executorch_output)
    ).item()

    print("\nActual class:", CLASS_NAMES[true_label])

    print("\nOriginal PyTorch prediction:")
    print(CLASS_NAMES[original_prediction])
    print(original_output)

    print("\nExecuTorch prediction:")
    print(CLASS_NAMES[executorch_prediction])
    print(executorch_output)

    print("\nMaximum absolute difference:", difference)

    assert original_prediction == executorch_prediction, (
        "The two models predicted different classes!"
    )

    assert difference < 0.01, (
        "The model outputs differ more than expected!"
    )

    print("\nREAL IMAGE EXECUTORCH TEST PASSED!")


if __name__ == "__main__":
    main()

