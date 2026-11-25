# Bharat AI – Offline Mulberry Crop Disease Detection and Advisory System

## Project Overview

Bharat AI is an offline AI-powered agricultural assistance system designed to help farmers identify mulberry leaf diseases, assess disease severity and access agricultural guidance.

The project combines computer vision, on-device AI inference and a structured disease knowledge base to support agriculture and food security.

## Development Team

- **Prabhath C S** – Team Lead and AI Developer
- **Manoj L** – Android Application Developer

## AI Architecture

The AI system consists of the following components:

### 1. Mulberry Disease Classification

- Architecture: MobileNetV3-Small
- Classification categories:
  - Disease Free leaves
  - Leaf Rust
  - Leaf spot
- Dataset: 1,074 unique images
- Training epochs: 10
- Test accuracy: 95.09%
- Export runtime: ExecuTorch 1.5.1

### 2. Llama-Based Farmer Advisory

- Model: Meta Llama 3.2 1B Instruct
- Quantization: XNNPACK dynamic QC4
- KV cache: Enabled
- Dynamic shapes: Enabled
- Maximum sequence length: 128
- Maximum context length: 256
- Runtime: ExecuTorch 1.5.1

Llama integration is undergoing inference debugging and response-quality testing.

### 3. Disease Severity Classification

A provisional four-level severity framework is being developed:

| Level | Classification |
|---|---|
| 0 | Healthy |
| 1 | Mild |
| 2 | Moderate |
| 3 | Severe |

Expert verification and further dataset annotation are ongoing.

### 4. Disease Knowledge Base

A structured JSON knowledge base stores available disease symptoms and safety guidance for the Android application.

### 5. Mulberry Image Validation

A separate image-validation component is being developed to distinguish valid mulberry leaf images from other crop leaves and non-leaf images.

## Technology Stack

- Python 3.11
- PyTorch 2.15.0
- Torchvision 0.29.2
- MobileNetV3-Small
- Meta Llama 3.2 1B Instruct
- ExecuTorch 1.5.1
- XNNPACK
- Kotlin
- Android Jetpack Compose

## Repository Structure

```text
AI-Models/
├── src/
├── scripts/
│   ├── llama/
│   └── severity/
├── models/
│   ├── llama/
│   └── mulberry/
├── data/
│   └── knowledge_base/
├── docs/
├── requirements.txt
├── requirements-executorch.txt
├── README.md
└── .gitignore
```

## Installation

Create a Python virtual environment:

```powershell
python -m venv .venv
```

Activate it:

```powershell
.\.venv\Scripts\Activate.ps1
```

Install the dependencies:

```powershell
pip install -r requirements.txt
```

For ExecuTorch export and Llama development, use the separate requirements file and the compatible ExecuTorch environment.

## Model Files

The repository may contain trained model checkpoints and exported models where file-size limits permit.

Large original Llama checkpoints and other large model binaries should be obtained separately. Refer to the model documentation for download and setup instructions.

## Current Development Status

- Mulberry disease classifier: trained and evaluated.
- ExecuTorch disease classifier: exported and validated.
- Llama 3.2 1B Instruct: exported and integrated; inference debugging ongoing.
- Disease knowledge base: initial implementation completed.
- Severity classification: annotation and expert review ongoing.
- Mulberry image validation: dataset preparation ongoing.

## Project Objective

To develop an accessible, offline and farmer-friendly AI system that supports mulberry crop disease identification and agricultural decision-making.

**Project:** Bharat AI – Agriculture and Food Security
