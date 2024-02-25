package com.example.travelapp_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.musicapp_kmp.MainCommon
import com.example.musicapp_kmp.decompose.MusicRoot
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent

@Composable
fun CommonMainDesktop(rootComponent: MusicRoot) {
    Box(Modifier.background(color = Color(0xFF1A1E1F)).fillMaxSize()) {
        CompositionLocalProvider(
            LocalImageLoader provides ImageLoader {
                interceptor {
                    memoryCacheConfig {
                        maxSizePercent(0.25)
                    }
                }
            },
        ) {
            MainCommon(rootComponent, true)
        }
    }
}