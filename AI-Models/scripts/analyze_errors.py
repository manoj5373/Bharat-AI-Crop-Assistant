
import sys
import csv
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

import torch
from torch.utils.data import DataLoader

from src.mulberry_dataset import MulberryDataset
from src.mulberry_model import create_model


MANIFEST = PROJECT_ROOT / "data/processed/mulberry_manifest/manifest.csv"
CHECKPOINT = PROJECT_ROOT / "models/mulberry/best_mulberry_model.pth"
OUTPUT = PROJECT_ROOT / "models/mulberry/misclassified_test_images.csv"

CLASS_NAMES = [
    "Disease Free leaves",
    "Leaf Rust",
    "Leaf spot"
]


def main():
    dataset = MulberryDataset(MANIFEST, "test")

    loader = DataLoader(
        dataset,
        batch_size=8,
        shuffle=False,
        num_workers=0
    )

    model = create_model(pretrained=False)

    checkpoint = torch.load(CHECKPOINT, map_location="cpu")
    model.load_state_dict(checkpoint["model_state_dict"])
    model.eval()

    errors = []

    with torch.no_grad():
        for images, labels in loader:
            outputs = model(images)
            probabilities = torch.softmax(outputs, dim=1)
            predictions = outputs.argmax(dim=1)

            for i in range(len(labels)):
                actual = labels[i].item()
                predicted = predictions[i].item()

                if actual != predicted:
                    sample_index = len(errors)

                    errors.append({
                        "image_path": "",
                        "actual_label": CLASS_NAMES[actual],
                        "predicted_label": CLASS_NAMES[predicted],
                        "predicted_probability": round(
                            probabilities[i, predicted].item(), 4
                        )
                    })

    # Re-run in dataset order to attach the correct file paths.
    errors = []
    sample_index = 0

    with torch.no_grad():
        for images, labels in loader:
            outputs = model(images)
            probabilities = torch.softmax(outputs, dim=1)
            predictions = outputs.argmax(dim=1)

            for i in range(len(labels)):
                actual = labels[i].item()
                predicted = predictions[i].item()

                if actual != predicted:
                    errors.append({
                        "image_path": dataset.samples[sample_index]["path"],
                        "actual_label": CLASS_NAMES[actual],
                        "predicted_label": CLASS_NAMES[predicted],
                        "predicted_probability": round(
                            probabilities[i, predicted].item(), 4
                        )
                    })

                sample_index += 1

    with open(OUTPUT, "w", newline="", encoding="utf-8") as file:
        writer = csv.DictWriter(
            file,
            fieldnames=[
                "image_path",
                "actual_label",
                "predicted_label",
                "predicted_probability"
            ]
        )
        writer.writeheader()
        writer.writerows(errors)

    print("Misclassified images:", len(errors))
    print("Report saved to:", OUTPUT)

    for error in errors:
        print("\nActual:", error["actual_label"])
        print("Predicted:", error["predicted_label"])
        print("Image:", error["image_path"])


if __name__ == "__main__":
    main()
