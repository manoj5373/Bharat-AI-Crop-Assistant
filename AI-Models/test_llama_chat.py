
from pytorch_tokenizers.tiktoken import TiktokenTokenizer

tokenizer = TiktokenTokenizer(
    "C:/Projects/Bharat-AI-Crop-Assistant/Android-App/BharatAI/app/src/main/assets/models/tokenizer.model"
)

prompt = """<|start_header_id|>system<|end_header_id|>
You are Bharat AI. Give short, safe answers to mulberry farmers.<|eot_id|><|start_header_id|>user<|end_header_id|>
Disease detected: Disease Free leaves.
Confidence: 99%.
Explain this result in simple English in under 40 words. Do not invent treatments.<|eot_id|><|start_header_id|>assistant<|end_header_id|>
"""

print("Formatted prompt:")
print(prompt)

tokens = tokenizer.encode(prompt, bos=False, eos=False)
print("Token count:", len(tokens))
print("\nToken ID 33 decoding test:")
print("Decoded:", repr(tokenizer.decode([33])))
print("EOS ID:", tokenizer.eos_id)
print("BOS ID:", tokenizer.bos_id) 
