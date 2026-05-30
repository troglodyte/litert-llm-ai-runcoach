package com.litert.coach.ui.debug

import androidx.lifecycle.ViewModel
import com.litert.coach.ai.PromptHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    val promptHistory: PromptHistory
) : ViewModel()
