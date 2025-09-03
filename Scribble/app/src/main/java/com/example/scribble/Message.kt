package com.example.scribble

import java.net.URL

data class Message(
    var text: String? = null,
    val isUser: Boolean,
    val imageResId: Int? = null,
    val videoResId: Int? = null,
    val audioResId: Int? = null,
    val isAudioPlaying: Boolean = false
    )
