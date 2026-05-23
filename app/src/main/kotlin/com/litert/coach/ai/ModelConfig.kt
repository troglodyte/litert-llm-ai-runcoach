package com.litert.coach.ai

/**
 * Download URLs for Gemma 3n LiteRT-LM (.litertlm) files.
 *
 * BEFORE BUILDING: You must agree to the Gemma license on Kaggle or Hugging Face
 * and obtain the model files. Update the URLs below to match the actual hosted paths.
 *
 * Kaggle: https://www.kaggle.com/models/google/gemma-3n
 * HuggingFace: https://huggingface.co/google/gemma-3n-E1B-it-litert-lm
 *
 * The files must be .litertlm format for LiteRT-LM.
 */
object ModelConfig {
    const val MODEL_DIR = "litert_models"

    val configs = mapOf(
        ModelVariant.GEMMA_3N_1B to ModelFileConfig(
            fileName = "gemma-3n-E1B-it-int4.litertlm",
            downloadUrl = "https://huggingface.co/google/gemma-3n-E1B-it-litert-lm/resolve/main/gemma-3n-E1B-it-int4.litertlm"
        ),
        ModelVariant.GEMMA_3N_4B to ModelFileConfig(
            fileName = "gemma-3n-E4B-it-int4.litertlm",
            downloadUrl = "https://huggingface.co/google/gemma-3n-E4B-it-litert-lm/resolve/main/gemma-3n-E4B-it-int4.litertlm"
        ),
        ModelVariant.GEMMA3_1B_IT to ModelFileConfig(
            fileName = "gemma3-1b-it-int4.litertlm",
            downloadUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.litertlm"
        )
    )
}

data class ModelFileConfig(val fileName: String, val downloadUrl: String)
