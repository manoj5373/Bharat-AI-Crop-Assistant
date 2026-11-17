
import sys
from pathlib import Path

import torch
from executorch.runtime import Runtime

PROJECT_DIR = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_DIR))

from src.mulberry_model import create_model
from src.mulberry_dataset import MulberryDataset

CHECKPOINT = PROJECT_DIR / "models/mulberry/best_mulberry_model.pth"
PTE_FILE = PROJECT_DIR / "models/mulberry/mulberry_classifier_151.pte"
MANIFEST = PROJECT_DIR / "data/processed/mulberry_manifest/manifest.csv"


def main():
    print("Loading test dataset...")
    dataset = MulberryDataset(MANIFEST, split="test")

    checkpoint = torch.load(
        CHECKPOINT, map_location="cpu", weights_only=True
    )

    model = create_model(num_classes=3, pretrained=False)
    model.load_state_dict(checkpoint["model_state_dict"])
    model.eval()

    print("Loading ExecuTorch model...")
    runtime = Runtime.get()
    program = runtime.load_program(str(PTE_FILE))
    method = program.load_method("forward")

    matching_predictions = 0
    original_correct = 0
    executorch_correct = 0
    max_difference = 0.0

    print(f"\nTesting {len(dataset)} images...\n")

    for index in range(len(dataset)):
        image, true_label = dataset[index]
        image = image.unsqueeze(0)

        with torch.no_grad():
            original_output = model(image)

        executorch_output = method.execute([image])[0]

        original_prediction = original_output.argmax(dim=1).item()
        executorch_prediction = executorch_output.argmax(dim=1).item()

        difference = torch.max(
            torch.abs(original_output - executorch_output)
        ).item()

        max_difference = max(max_difference, difference)

        if original_prediction == executorch_prediction:
            matching_predictions += 1

        if original_prediction == true_label:
            original_correct += 1

        if executorch_prediction == true_label:
            executorch_correct += 1

        if (index + 1) % 25 == 0 or index + 1 == len(dataset):
            print(f"Processed {index + 1}/{len(dataset)} images")

    total = len(dataset)

    print("\n========== VALIDATION RESULTS ==========")
    print(f"Total images: {total}")
    print(f"Matching predictions: {matching_predictions}/{total}")
    print(f"Prediction agreement: {matching_predictions / total * 100:.2f}%")
    print(f"Original model accuracy: {original_correct / total * 100:.2f}%")
    print(f"ExecuTorch model accuracy: {executorch_correct / total * 100:.2f}%")
    print(f"Maximum output difference: {max_difference:.8f}")

    assert matching_predictions == total, (
        "Some predictions differ. Investigate before Android integration."
    )

    print("\nFULL TEST-SET EXPORT VALIDATION PASSED!")


if __name__ == "__main__":
    main()

