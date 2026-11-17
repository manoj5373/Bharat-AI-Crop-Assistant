
import sys
from pathlib import Path

import torch
from executorch.runtime import Runtime

PROJECT_DIR = Path(__file__).resolve().parents[1]
PTE_PATH = PROJECT_DIR / "models" / "mulberry" / "mulberry_classifier.pte"
CHECKPOINT_PATH = PROJECT_DIR / "models" / "mulberry" / "best_mulberry_model.pth"

sys.path.insert(0, str(PROJECT_DIR))
from src.mulberry_model import create_model


def main():
    print("1. Loading original PyTorch model...")

    checkpoint = torch.load(
        CHECKPOINT_PATH,
        map_location="cpu",
        weights_only=True,
    )

    original_model = create_model(num_classes=3, pretrained=False)
    original_model.load_state_dict(checkpoint["model_state_dict"])
    original_model.eval()

    print("2. Loading ExecuTorch program...")

    runtime = Runtime.get()
    program = runtime.load_program(str(PTE_PATH))
    method = program.load_method("forward")

    print("3. Creating test input...")

    torch.manual_seed(42)
    test_input = torch.randn(1, 3, 224, 224)

    print("4. Running original model...")

    with torch.no_grad():
        original_output = original_model(test_input)

    print("5. Running ExecuTorch model...")

    executorch_outputs = method.execute([test_input])
    executorch_output = executorch_outputs[0]

    print("\nOriginal output:")
    print(original_output)

    print("\nExecuTorch output:")
    print(executorch_output)

    difference = torch.max(
        torch.abs(original_output - executorch_output)
    ).item()

    print("\nMaximum absolute difference:", difference)

    original_class = original_output.argmax(dim=1).item()
    executorch_class = executorch_output.argmax(dim=1).item()

    print("Original predicted class:", original_class)
    print("ExecuTorch predicted class:", executorch_class)

    assert original_class == executorch_class, (
        "Prediction mismatch between original and exported model!"
    )

    assert difference < 0.01, (
        "Output difference is larger than expected!"
    )

    print("\nEXECUTORCH RUNTIME TEST PASSED!")


if __name__ == "__main__":
    main()
