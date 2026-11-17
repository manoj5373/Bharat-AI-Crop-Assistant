import os
import csv
import hashlib
import random
from collections import defaultdict

SOURCE = r"data/raw/mulberry/Mulberry Data"
DEST = r"data/processed/mulberry_manifest"

SEED = 42
EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp"}

CLASSES = [
    "Disease Free leaves",
    "Leaf Rust",
    "Leaf spot"
]

def main():
    random.seed(SEED)
    records = []
    hash_map = defaultdict(list)

    print("Scanning dataset...", flush=True)

    for label in CLASSES:
        folder = os.path.join(SOURCE, label)

        if not os.path.isdir(folder):
            raise FileNotFoundError(f"Missing folder: {folder}")

        for current, _, files in os.walk(folder):
            for filename in files:
                if os.path.splitext(filename)[1].lower() not in EXTENSIONS:
                    continue

                path = os.path.abspath(os.path.join(current, filename))
                digest = hashlib.sha256()

                with open(path, "rb") as f:
                    for chunk in iter(lambda: f.read(1024 * 1024), b""):
                        digest.update(chunk)

                record = {
                    "path": path,
                    "label": label,
                    "sha256": digest.hexdigest()
                }

                records.append(record)
                hash_map[record["sha256"]].append(record)

    unique_records = []
    duplicates = []
    conflicts = []

    for digest, group in hash_map.items():
        labels = {item["label"] for item in group}

        if len(labels) > 1:
            conflicts.extend(group)
        else:
            unique_records.append(group[0])
            duplicates.extend(group[1:])

    print(f"Images discovered: {len(records)}", flush=True)
    print(f"Unique images: {len(unique_records)}", flush=True)
    print(f"Exact duplicates: {len(duplicates)}", flush=True)
    print(f"Conflicting-label images: {len(conflicts)}", flush=True)

    if conflicts:
        raise RuntimeError("Conflicting labels found. Split not created.")

    splits = {"train": [], "val": [], "test": []}

    for label in CLASSES:
        items = [x for x in unique_records if x["label"] == label]
        random.shuffle(items)

        n = len(items)
        n_train = int(n * 0.70)
        n_val = int(n * 0.15)

        splits["train"].extend(items[:n_train])
        splits["val"].extend(items[n_train:n_train + n_val])
        splits["test"].extend(items[n_train + n_val:])

    os.makedirs(DEST, exist_ok=True)

    manifest = os.path.join(DEST, "manifest.csv")

    with open(manifest, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["split", "label", "path", "sha256"])

        for split_name, items in splits.items():
            for item in items:
                writer.writerow([
                    split_name,
                    item["label"],
                    item["path"],
                    item["sha256"]
                ])

    print("\n=== SPLIT COMPLETE ===", flush=True)

    for split_name, items in splits.items():
        print(f"\n{split_name.upper()}: {len(items)}", flush=True)
        for label in CLASSES:
            count = sum(x["label"] == label for x in items)
            print(f"  {label}: {count}", flush=True)

    print(f"\nManifest saved to: {manifest}", flush=True)
    print("Original images were not moved or copied.", flush=True)

if __name__ == "__main__":
    try:
        main()
    except Exception:
        import traceback
        traceback.print_exc()
        raise