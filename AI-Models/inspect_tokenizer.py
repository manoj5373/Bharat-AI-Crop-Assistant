
from pathlib import Path
import hashlib
import sentencepiece as spm

paths = {
    "Original": Path(
        r"C:\Projects\Bharat-AI-Crop-Assistant\AI-Models\models\llama\original\tokenizer.model"
    ),
    "Android asset": Path(
        r"C:\Projects\Bharat-AI-Crop-Assistant\Android-App\BharatAI\app\src\main\assets\models\tokenizer.model"
    ),
}

for name, path in paths.items():
    print(f"\n{'=' * 50}")
    print(name)
    print("Path:", path)
    print("Exists:", path.exists())

    if not path.exists():
        continue

    data = path.read_bytes()

    print("Size:", len(data), "bytes")
    print("SHA256:", hashlib.sha256(data).hexdigest())
    print("First 32 bytes:", data[:32].hex())

    try:
        tokenizer = spm.SentencePieceProcessor(model_file=str(path))
        print("SentencePiece: SUCCESS")
        print("Vocabulary size:", tokenizer.get_piece_size())
    except Exception as error:
        print("SentencePiece: FAILED")
        print("Error:", error)
