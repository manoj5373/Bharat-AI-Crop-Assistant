from pathlib import Path
import json

import numpy as np
import torch
import torchvision

from PIL import Image
from torchvision import transforms
from torchvision.transforms import InterpolationMode


# ============================================================
# 1. PROJECT CONFIGURATION
# ============================================================

PROJECT_ROOT = Path(__file__).resolve().parents[1]

DATA_ROOT = PROJECT_ROOT / "data" / "raw" / "mulberry"

OUTPUT_DIR = PROJECT_ROOT / "android_handoff" / "sample_input"

IMAGE_NAME = "IMG_1990.JPG"

IMAGE_SIZE = 224

JPEG_QUALITY = 95


# ============================================================
# 2. CREATE OUTPUT DIRECTORY
# ============================================================

OUTPUT_DIR.mkdir(
    parents=True,
    exist_ok=True
)

print("Project directory:", PROJECT_ROOT)
print("Output directory:", OUTPUT_DIR)


# ============================================================
# 3. FIND ORIGINAL IMAGE
# ============================================================

matches = [
    path
    for path in DATA_ROOT.rglob("*")
    if path.is_file()
    and path.name.lower() == IMAGE_NAME.lower()
]

if not matches:
    raise FileNotFoundError(
        f"Image not found: {IMAGE_NAME}\n"
        f"Search directory: {DATA_ROOT}"
    )

if len(matches) > 1:
    raise RuntimeError(
        "Multiple images with the same name were found:\n"
        + "\n".join(str(path) for path in matches)
    )

image_path = matches[0]

print("\nOriginal image:", image_path)


# ============================================================
# 4. DEFINE MODEL PREPROCESSING
# ============================================================

preprocess = transforms.Compose([

    transforms.Resize(
        (IMAGE_SIZE, IMAGE_SIZE),
        interpolation=InterpolationMode.BILINEAR,
        antialias=True
    ),

    transforms.ToTensor(),

    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )

])


# ============================================================
# 5. SAVE REFERENCE IMAGE AS JPG
# ============================================================

sample_image_path = OUTPUT_DIR / "sample_leaf.jpg"

with Image.open(image_path) as original_image:

    rgb_image = original_image.convert("RGB")

    original_width, original_height = rgb_image.size

    # Save the reference image as JPEG.

    rgb_image.save(
        sample_image_path,
        format="JPEG",
        quality=JPEG_QUALITY
    )

print("\nReference JPG saved:", sample_image_path)


# ============================================================
# 6. REOPEN THE SAVED JPG
# ============================================================

# IMPORTANT:
# Generate the tensor from the exact JPG file that
# Android will use for its preprocessing comparison.

with Image.open(sample_image_path) as reference_image:

    reference_rgb = reference_image.convert("RGB")

    reference_width, reference_height = reference_rgb.size

    # Apply training preprocessing.

    tensor = preprocess(reference_rgb).unsqueeze(0)


# ============================================================
# 7. CONVERT TO FLOAT32
# ============================================================

tensor = tensor.to(
    dtype=torch.float32
).contiguous()


# ============================================================
# 8. CONVERT TO NUMPY
# ============================================================

numpy_tensor = tensor.cpu().numpy()


# ============================================================
# 9. VALIDATE TENSOR
# ============================================================

expected_shape = (1, 3, 224, 224)

assert numpy_tensor.shape == expected_shape, (
    f"Incorrect tensor shape: {numpy_tensor.shape}"
)

assert numpy_tensor.dtype == np.float32, (
    f"Incorrect tensor dtype: {numpy_tensor.dtype}"
)

assert np.isfinite(numpy_tensor).all(), (
    "Tensor contains NaN or infinite values"
)


# ============================================================
# 10. SAVE NUMPY REFERENCE TENSOR
# ============================================================

npy_path = OUTPUT_DIR / "preprocessed_input.npy"

with open(npy_path, "wb") as npy_file:

    np.save(
        npy_file,
        numpy_tensor
    )

print("Reference tensor saved:", npy_path)


# ============================================================
# 11. SAVE METADATA
# ============================================================

metadata = {

    "source_image": image_path.name,

    "reference_image": "sample_leaf.jpg",

    "original_image_width": original_width,

    "original_image_height": original_height,

    "reference_image_width": reference_width,

    "reference_image_height": reference_height,

    "jpeg_quality": JPEG_QUALITY,

    "expected_class_index": 0,

    "expected_class_name": "Disease Free leaves",

    "tensor_shape": list(numpy_tensor.shape),

    "tensor_dtype": str(numpy_tensor.dtype),

    "tensor_layout": "NCHW",

    "color_format": "RGB",

    "image_size": [IMAGE_SIZE, IMAGE_SIZE],

    "interpolation": "BILINEAR",

    "antialias": True,

    "normalization": "ImageNet",

    "mean": [0.485, 0.456, 0.406],

    "std": [0.229, 0.224, 0.225],

    "torch_version": torch.__version__,

    "torchvision_version": torchvision.__version__,

    "exif_orientation_correction": False,

    "reference_purpose": "Android preprocessing comparison",

    "test_set_membership_verified": False

}

metadata_path = OUTPUT_DIR / "metadata.json"

with open(
    metadata_path,
    "w",
    encoding="utf-8"
) as metadata_file:

    json.dump(
        metadata,
        metadata_file,
        indent=4
    )

print("Metadata saved:", metadata_path)


# ============================================================
# 12. VERIFY GENERATED FILES
# ============================================================

# Reload tensor.

loaded_tensor = np.load(npy_path)

assert loaded_tensor.shape == expected_shape

assert loaded_tensor.dtype == np.float32

assert np.array_equal(
    loaded_tensor,
    numpy_tensor
), "Saved tensor verification failed"


# Verify JPG format.

with Image.open(sample_image_path) as check_image:

    assert check_image.format == "JPEG"

    assert check_image.mode == "RGB"

    assert check_image.size == (
        reference_width,
        reference_height
    )


# Verify metadata.

with open(
    metadata_path,
    "r",
    encoding="utf-8"
) as metadata_file:

    loaded_metadata = json.load(metadata_file)

assert loaded_metadata["reference_image"] == "sample_leaf.jpg"


# ============================================================
# 13. DISPLAY RESULTS
# ============================================================

print("\n" + "=" * 55)

print("ANDROID PREPROCESSING EXPORT SUCCESSFUL")

print("=" * 55)

print("\nGenerated files:")

print("1. Reference image:", sample_image_path)

print("2. Reference tensor:", npy_path)

print("3. Metadata:", metadata_path)

print("\nImage information:")

print("Format: JPEG")

print("Color mode: RGB")

print("Dimensions:", reference_width, "x", reference_height)

print("JPEG quality:", JPEG_QUALITY)

print("\nTensor information:")

print("Shape:", loaded_tensor.shape)

print("Data type:", loaded_tensor.dtype)

print("Layout: NCHW")

print("Minimum:", float(loaded_tensor.min()))

print("Maximum:", float(loaded_tensor.max()))

print("Mean:", float(loaded_tensor.mean()))

print("\nSoftware versions:")

print("PyTorch:", torch.__version__)

print("Torchvision:", torchvision.__version__)

print("\nExpected class:")

print("Index: 0")

print("Name: Disease Free leaves")

print("\nAll reference files verified successfully!")

print("=" * 55)