
import csv
from pathlib import Path
from PIL import Image, ImageDraw

PROJECT_ROOT = Path(__file__).resolve().parent.parent

REPORT = (
    PROJECT_ROOT
    / "models/mulberry/misclassified_test_images.csv"
)

OUTPUT = (
    PROJECT_ROOT
    / "models/mulberry/misclassified_contact_sheet.jpg"
)

with open(REPORT, "r", encoding="utf-8") as file:
    errors = list(csv.DictReader(file))

thumb_width = 360
thumb_height = 280
label_height = 85

columns = 2
rows = (len(errors) + columns - 1) // columns

sheet = Image.new(
    "RGB",
    (columns * thumb_width, rows * (thumb_height + label_height)),
    "white"
)

draw = ImageDraw.Draw(sheet)

for index, item in enumerate(errors):
    image_path = Path(item["image_path"])

    with Image.open(image_path) as original:
        image = original.convert("RGB")
        image.thumbnail((thumb_width - 20, thumb_height - 20))

    x = (index % columns) * thumb_width
    y = (index // columns) * (thumb_height + label_height)

    image_x = x + (thumb_width - image.width) // 2
    image_y = y + (thumb_height - image.height) // 2

    sheet.paste(image, (image_x, image_y))

    text = (
        f"Actual: {item['actual_label']}\n"
        f"Predicted: {item['predicted_label']}\n"
        f"Confidence: {item['predicted_probability']}"
    )

    draw.multiline_text(
        (x + 10, y + thumb_height),
        text,
        fill="black",
        spacing=5
    )

sheet.save(OUTPUT, quality=90)

print("Contact sheet created:")
print(OUTPUT)
