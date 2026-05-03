package com.mapex

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache

class MapexApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Habilitar crossfade para que las imágenes aparezcan suavemente
            .crossfade(true)
            .crossfade(300)
            // Configurar cache agresivo en memoria
            .memoryCache {
                MemoryCache.Builder(this)
                    // Usar hasta el 25% de la memoria disponible en la app para imágenes
                    .maxSizePercent(0.25)
                    .build()
            }
            // Configurar cache agresivo en disco
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    // Hasta 50MB de imágenes en disco
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            // Respetar headers de cache de red
            .respectCacheHeaders(false)
            .build()
    }
}
