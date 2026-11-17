import os
import csv
from collections import Counter
from PIL import Image

ROOT = "data/raw/mulberry/Mulberry Data"
OUTPUT = "docs/mulberry/image_quality_report.csv"

extensions = {".jpg", ".jpeg", ".png"}
valid_count = Counter()
bad_files = []
dimensions = Counter()
total = 0

print("=== MULBERRY IMAGE QUALITY CHECK ===")

for current, dirs, files in os.walk(ROOT):
    for filename in files:
        if os.path.splitext(filename)[1].lower() not in extensions:
            continue

        total += 1
        path = os.path.join(current, filename)
        label = os.path.relpath(current, ROOT).split(os.sep)[0]

        try:
            with Image.open(path) as img:
                img.verify()

            with Image.open(path) as img:
                dimensions[img.size] += 1

            valid_count[label] += 1

        except Exception as error:
            bad_files.append((path, str(error)))

os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)

with open(OUTPUT, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["file_path", "error"])
    writer.writerows(bad_files)

print("\nImages found:", total)
print("Readable images:", sum(valid_count.values()))
print("Damaged images:", len(bad_files))

print("\nImages per class:")
for label, count in sorted(valid_count.items()):
    print(f"{label}: {count}")

print("\nMost common image dimensions:")
for size, count in dimensions.most_common(10):
    print(f"{size}: {count}")

print("\nReport saved to:", OUTPUT)
print("=== QUALITY CHECK COMPLETE ===")