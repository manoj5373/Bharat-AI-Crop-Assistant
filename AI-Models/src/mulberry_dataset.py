import csv
from pathlib import Path

from PIL import Image
from torch.utils.data import Dataset
from torchvision import transforms


CLASS_NAMES = [
    "Disease Free leaves",
    "Leaf Rust",
    "Leaf spot"
]

CLASS_TO_INDEX = {
    name: index for index, name in enumerate(CLASS_NAMES)
}


class MulberryDataset(Dataset):

    def __init__(self, manifest_path, split):
        self.manifest_path = Path(manifest_path)
        self.split = split

        self.samples = []

        with open(
            self.manifest_path,
            "r",
            encoding="utf-8",
            newline=""
        ) as file:
            reader = csv.DictReader(file)

            for row in reader:
                if row["split"] == split:
                    self.samples.append({
                        "path": row["path"],
                        "label": CLASS_TO_INDEX[row["label"]]
                    })

        self.transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(
                mean=[0.485, 0.456, 0.406],
                std=[0.229, 0.224, 0.225]
            )
        ])

        if not self.samples:
            raise ValueError(f"No images found for split: {split}")

    def __len__(self):
        return len(self.samples)

    def __getitem__(self, index):
        sample = self.samples[index]

        image = Image.open(sample["path"]).convert("RGB")
        image = self.transform(image)

        label = sample["label"]

        return image, label