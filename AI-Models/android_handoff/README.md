

\# Bharat AI — Mulberry Disease Classifier



\## Model information



\- Architecture: MobileNetV3-Small

\- Runtime: ExecuTorch 1.6.0

\- Backend: XNNPACK

\- Model file: assets/mulberry\_classifier.pte

\- Input shape: \[1, 3, 224, 224]

\- Input data type: float32

\- Output shape: \[1, 3]

\- Output: Raw classification logits



\## Class index mapping



0 = Disease Free leaves

1 = Leaf Rust

2 = Leaf spot



See class\_labels.csv.



\## Required image preprocessing



1\. Decode the captured image.

2\. Convert the image to RGB.

3\. Resize to 224 x 224 pixels.

4\. Convert pixel values to float32 in the range \[0, 1].

5\. Normalize each channel:



&#x20;  Red:   (R - 0.485) / 0.229

&#x20;  Green: (G - 0.456) / 0.224

&#x20;  Blue:  (B - 0.406) / 0.225



6\. Arrange the tensor in NCHW format:

&#x20;  \[1, 3, 224, 224]



Important:

Match the training preprocessing, including resize interpolation

and antialiasing. Validate Android preprocessing against the

Python implementation using the same test image.



\## Inference



1\. Load mulberry\_classifier.pte using the ExecuTorch Android runtime.

2\. Execute the forward method with the input tensor.

3\. Read the three output logits.

4\. The largest logit corresponds to the predicted class index.

5\. Map that index using class\_labels.csv.



Softmax may be applied for display, but its output is not

a calibrated disease probability.



\## Important limitations



\- Supports only the three listed classes.

\- Does not detect pests.

\- Does not estimate disease severity.

\- Does not provide a confirmed diagnosis.

\- Unknown or uncertain images should be handled cautiously.

\- The model requires field validation before agricultural

&#x20; recommendations are relied upon.



\## Validation completed



\- PyTorch to ExecuTorch export: successful

\- XNNPACK lowering: successful

\- Random input runtime comparison: passed

\- Real image comparison: passed

\- Held-out test-set agreement: 163/163

\- Held-out test accuracy: 95.09%



