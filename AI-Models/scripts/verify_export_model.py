
import torch
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from src.mulberry_model import create_model

checkpoint_path = Path("models/mulberry/best_mulberry_model.pth")

print("Loading checkpoint...")

checkpoint = torch.load(
    checkpoint_path,
    map_location="cpu",
    weights_only=True
)

print("Classes:", checkpoint["class_names"])
print("Best validation accuracy:", checkpoint["best_val_accuracy"])

model = create_model(num_classes=3, pretrained=False)

model.load_state_dict(checkpoint["model_state_dict"])
model.eval()

print("Model weights loaded successfully.")

sample = torch.randn(1, 3, 224, 224)

with torch.no_grad():
    output = model(sample)

print("Input shape:", tuple(sample.shape))
print("Output shape:", tuple(output.shape))
print("Output:", output)

assert output.shape == (1, 3)

print("\nMODEL CHECK PASSED!")
