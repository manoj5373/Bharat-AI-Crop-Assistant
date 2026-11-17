import os
import sys
from collections import Counter

root = sys.argv[1] if len(sys.argv) > 1 else "data/raw/mulberry"

extensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp"}

if not os.path.isdir(root):
    print("Dataset folder not found:", root)
    sys.exit(1)

print("\n=== MULBERRY DATASET INSPECTION ===")
print("Dataset location:", os.path.abspath(root))

total = 0
class_counts = Counter()
extension_counts = Counter()

for current, dirs, files in os.walk(root):
    for filename in files:
        ext = os.path.splitext(filename)[1].lower()

        if ext in extensions:
            total += 1
            extension_counts[ext] += 1

            relative = os.path.relpath(current, root)
            label = relative.split(os.sep)[0]

            if label == ".":
                label = "Unlabelled / root images"

            class_counts[label] += 1

print("\nImages per folder:")
for label, count in sorted(class_counts.items()):
    print(f"{label}: {count}")

print("\nImage formats:")
for ext, count in extension_counts.items():
    print(f"{ext}: {count}")

print("\nTOTAL IMAGES:", total)
print("=== INSPECTION COMPLETE ===")