
import sys
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

import torch
from torch.utils.data import DataLoader

from src.mulberry_dataset import MulberryDataset
from src.mulberry_model import create_model


MANIFEST = (
    PROJECT_ROOT / "data" / "processed"
    / "mulberry_manifest" / "manifest.csv"
)

CHECKPOINT = (
    PROJECT_ROOT / "models" / "mulberry"
    / "best_mulberry_model.pth"
)

CLASS_NAMES = [
    "Disease Free leaves",
    "Leaf Rust",
    "Leaf spot"
]

DEVICE = torch.device("cpu")


def main():
    print("Loading test dataset...", flush=True)

    test_dataset = MulberryDataset(MANIFEST, "test")

    test_loader = DataLoader(
        test_dataset,
        batch_size=8,
        shuffle=False,
        num_workers=0
    )

    print("Loading best trained model...", flush=True)

    model = create_model(pretrained=False)

    checkpoint = torch.load(
        CHECKPOINT,
        map_location=DEVICE
    )

    model.load_state_dict(checkpoint["model_state_dict"])
    model.to(DEVICE)
    model.eval()

    print(
        "Best validation accuracy:",
        checkpoint["best_val_accuracy"],
        flush=True
    )

    # Rows = actual classes, columns = predicted classes
    confusion = torch.zeros(3, 3, dtype=torch.int64)

    with torch.no_grad():
        for images, labels in test_loader:
            outputs = model(images.to(DEVICE))
            predictions = outputs.argmax(dim=1)

            for actual, predicted in zip(labels, predictions):
                confusion[actual, predicted] += 1

    total = confusion.sum().item()
    correct = confusion.diag().sum().item()
    accuracy = correct / total

    print("\n=== TEST RESULTS ===")
    print(f"Test images: {total}")
    print(f"Correct predictions: {correct}")
    print(f"Test accuracy: {accuracy:.4f} ({accuracy * 100:.2f}%)")

    print("\nConfusion matrix:")
    print("Rows = actual, columns = predicted")
    print("Class order:", CLASS_NAMES)
    print(confusion.tolist())

    print("\nPer-class metrics:")

    for i, class_name in enumerate(CLASS_NAMES):
        true_positive = confusion[i, i].item()
        false_positive = confusion[:, i].sum().item() - true_positive
        false_negative = confusion[i, :].sum().item() - true_positive

        precision = (
            true_positive / (true_positive + false_positive)
            if true_positive + false_positive else 0.0
        )

        recall = (
            true_positive / (true_positive + false_negative)
            if true_positive + false_negative else 0.0
        )

        f1 = (
            2 * precision * recall / (precision + recall)
            if precision + recall else 0.0
        )

        print(f"\n{class_name}")
        print(f"  Precision: {precision:.4f}")
        print(f"  Recall:    {recall:.4f}")
        print(f"  F1-score:  {f1:.4f}")

    print("\nTEST EVALUATION COMPLETE!")


if __name__ == "__main__":
    main()
