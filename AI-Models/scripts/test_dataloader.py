import sys
from pathlib import Path

# Add the project root to Python's import path
PROJECT_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

from torch.utils.data import DataLoader
from src.mulberry_dataset import MulberryDataset


MANIFEST = PROJECT_ROOT / "data" / "processed" / "mulberry_manifest" / "manifest.csv"

train_dataset = MulberryDataset(MANIFEST, "train")
val_dataset = MulberryDataset(MANIFEST, "val")
test_dataset = MulberryDataset(MANIFEST, "test")

print("Dataset sizes:")
print("Train:", len(train_dataset))
print("Validation:", len(val_dataset))
print("Test:", len(test_dataset))

train_loader = DataLoader(
    train_dataset,
    batch_size=8,
    shuffle=True,
    num_workers=0
)

images, labels = next(iter(train_loader))

print("\nBatch test:")
print("Image tensor shape:", images.shape)
print("Label tensor shape:", labels.shape)
print("Labels:", labels.tolist())

assert images.shape == (8, 3, 224, 224)
assert labels.shape == (8,)

print("\nDATA LOADER TEST PASSED!")