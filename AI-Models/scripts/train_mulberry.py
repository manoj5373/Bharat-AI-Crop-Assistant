
import sys
import csv
import random
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

import torch
import torch.nn as nn
from torch.utils.data import DataLoader

from src.mulberry_dataset import MulberryDataset
from src.mulberry_model import create_model


SEED = 42
random.seed(SEED)
torch.manual_seed(SEED)

MANIFEST = (
    PROJECT_ROOT
    / "data"
    / "processed"
    / "mulberry_manifest"
    / "manifest.csv"
)

MODEL_DIR = PROJECT_ROOT / "models" / "mulberry"
MODEL_DIR.mkdir(parents=True, exist_ok=True)

EPOCHS = 10
BATCH_SIZE = 8
LEARNING_RATE = 0.001

DEVICE = torch.device("cpu")


def run_epoch(model, loader, criterion, optimizer=None):
    training = optimizer is not None

    if training:
        model.train()
    else:
        model.eval()

    total_loss = 0.0
    correct = 0
    total = 0

    for images, labels in loader:
        images = images.to(DEVICE)
        labels = labels.to(DEVICE)

        if training:
            optimizer.zero_grad()

        with torch.set_grad_enabled(training):
            outputs = model(images)
            loss = criterion(outputs, labels)

            if training:
                loss.backward()
                optimizer.step()

        total_loss += loss.item() * images.size(0)

        predictions = outputs.argmax(dim=1)
        correct += (predictions == labels).sum().item()
        total += labels.size(0)

    return total_loss / total, correct / total


def main():
    print("Loading datasets...", flush=True)

    train_dataset = MulberryDataset(MANIFEST, "train")
    val_dataset = MulberryDataset(MANIFEST, "val")

    train_loader = DataLoader(
        train_dataset,
        batch_size=BATCH_SIZE,
        shuffle=True,
        num_workers=0
    )

    val_loader = DataLoader(
        val_dataset,
        batch_size=BATCH_SIZE,
        shuffle=False,
        num_workers=0
    )

    print("Creating model...", flush=True)

    model = create_model(pretrained=True).to(DEVICE)

    criterion = nn.CrossEntropyLoss()

    optimizer = torch.optim.Adam(
        filter(
            lambda p: p.requires_grad,
            model.parameters()
        ),
        lr=LEARNING_RATE
    )

    best_val_accuracy = 0.0
    history = []

    for epoch in range(EPOCHS):
        print(
            f"\nEpoch {epoch + 1}/{EPOCHS}",
            flush=True
        )

        train_loss, train_accuracy = run_epoch(
            model,
            train_loader,
            criterion,
            optimizer
        )

        val_loss, val_accuracy = run_epoch(
            model,
            val_loader,
            criterion
        )

        print(
            f"Train loss: {train_loss:.4f} | "
            f"Train accuracy: {train_accuracy:.4f}",
            flush=True
        )

        print(
            f"Val loss: {val_loss:.4f} | "
            f"Val accuracy: {val_accuracy:.4f}",
            flush=True
        )

        history.append({
            "epoch": epoch + 1,
            "train_loss": train_loss,
            "train_accuracy": train_accuracy,
            "val_loss": val_loss,
            "val_accuracy": val_accuracy
        })

        # Save latest completed epoch
        torch.save(
            {
                "epoch": epoch + 1,
                "model_state_dict": model.state_dict(),
                "optimizer_state_dict": optimizer.state_dict(),
                "class_names": [
                    "Disease Free leaves",
                    "Leaf Rust",
                    "Leaf spot"
                ],
                "val_accuracy": val_accuracy
            },
            MODEL_DIR / "latest_checkpoint.pth"
        )

        print("Latest checkpoint saved.", flush=True)

        # Save best model
        if val_accuracy > best_val_accuracy:
            best_val_accuracy = val_accuracy

            torch.save(
                {
                    "model_state_dict": model.state_dict(),
                    "class_names": [
                        "Disease Free leaves",
                        "Leaf Rust",
                        "Leaf spot"
                    ],
                    "best_val_accuracy": best_val_accuracy
                },
                MODEL_DIR / "best_mulberry_model.pth"
            )

            print("Best model saved.", flush=True)

    history_path = MODEL_DIR / "training_history.csv"

    with open(
        history_path,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:
        writer = csv.DictWriter(
            file,
            fieldnames=history[0].keys()
        )
        writer.writeheader()
        writer.writerows(history)

    print("\nTraining complete.", flush=True)
    print(
        f"Best validation accuracy: {best_val_accuracy:.4f}",
        flush=True
    )
    print(f"Model saved in: {MODEL_DIR}", flush=True)


if __name__ == "__main__":
    main()
