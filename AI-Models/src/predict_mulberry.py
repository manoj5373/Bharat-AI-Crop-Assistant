
import sys
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

import torch
from PIL import Image
from torchvision import transforms

from src.mulberry_model import create_model


MODEL_PATH = (
    PROJECT_ROOT
    / "models"
    / "mulberry"
    / "best_mulberry_model.pth"
)

CLASS_NAMES = [
    "Disease Free leaves",
    "Leaf Rust",
    "Leaf spot"
]

DEVICE = torch.device("cpu")

transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])


def load_model():
    model = create_model(pretrained=False)

    checkpoint = torch.load(
        MODEL_PATH,
        map_location=DEVICE
    )

    model.load_state_dict(checkpoint["model_state_dict"])
    model.to(DEVICE)
    model.eval()

    return model


def predict_image(image_path):
    image_path = Path(image_path)

    if not image_path.is_file():
        raise FileNotFoundError(
            f"Image not found: {image_path}"
        )

    with Image.open(image_path) as original:
        image = original.convert("RGB")
        image_tensor = transform(image).unsqueeze(0)

    model = load_model()

    with torch.no_grad():
        outputs = model(image_tensor)
        probabilities = torch.softmax(outputs, dim=1)[0]

    predicted_index = probabilities.argmax().item()

    print("\n=== BHARAT AI MULBERRY PREDICTION ===")
    print("Image:", image_path.name)
    print("Predicted class:", CLASS_NAMES[predicted_index])

    print("\nClass scores:")

    for index, class_name in enumerate(CLASS_NAMES):
        score = probabilities[index].item() * 100
        print(f"{class_name}: {score:.2f}%")

    print(
        "\nNote: These are model scores, not verified "
        "diagnostic probabilities."
    )
    print(
        "This prototype only recognizes three dataset classes."
    )


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(
            "Usage: python -m src.predict_mulberry "
            "<path_to_leaf_image>"
        )
        sys.exit(1)

    predict_image(sys.argv[1])
