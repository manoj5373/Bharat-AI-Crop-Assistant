
# Bharat AI – AI Models

## Overview
This folder contains the artificial intelligence and machine learning components developed for the Bharat AI project.

## Model Architecture
- MobileNetV3-Small
- Framework: PyTorch
- Export format: ExecuTorch (.pte)

## Mulberry Disease Classification

The model classifies mulberry leaves into three categories:

1. Disease Free leaves
2. Leaf Rust
3. Leaf spot

## Model Performance

- Best validation accuracy: 97.50%
- Test accuracy: 95.09%
- Test images: 163

## Preprocessing

- Input image size: 224 × 224
- Color format: RGB
- Data type: Float32
- Normalization: ImageNet mean and standard deviation

## Project Structure

- `src/` – Model architecture and prediction code
- `scripts/` – Training, evaluation and export scripts
- `models/` – Trained model files
- `android_handoff/` – Android integration files
- `docs/` – Technical documentation

## Android Integration

The trained classifier is exported to ExecuTorch format for offline Android inference.

## Future Development

- Severity detection
- Additional crop disease classification
- Offline agricultural advisory using Llama

## Note

The current classifier supports three mulberry leaf categories.
Severity detection and Llama-based advisory are planned features.
