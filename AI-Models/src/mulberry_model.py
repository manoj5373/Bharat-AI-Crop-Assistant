import torch
import torch.nn as nn
from torchvision import models


def create_model(num_classes=3, pretrained=True):

    weights = (
        models.MobileNet_V3_Small_Weights.DEFAULT
        if pretrained else None
    )

    model = models.mobilenet_v3_small(weights=weights)

    # Freeze the pretrained feature extractor initially
    for parameter in model.features.parameters():
        parameter.requires_grad = False

    # Replace the final classifier layer
    input_features = model.classifier[-1].in_features

    model.classifier[-1] = nn.Linear(
        input_features,
        num_classes
    )

    return model


if __name__ == "__main__":
    model = create_model()

    sample = torch.randn(2, 3, 224, 224)

    with torch.no_grad():
        output = model(sample)

    print("Model:", "MobileNetV3-Small")
    print("Input shape:", sample.shape)
    print("Output shape:", output.shape)
    print("Total parameters:", sum(p.numel() for p in model.parameters()))
    print(
        "Trainable parameters:",
        sum(p.numel() for p in model.parameters() if p.requires_grad)
    )

    assert output.shape == (2, 3)

    print("\nMODEL TEST PASSED!")