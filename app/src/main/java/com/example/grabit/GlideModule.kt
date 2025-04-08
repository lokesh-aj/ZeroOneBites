package com.example.grabit

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

@GlideModule
class GrabITGlideModule : AppGlideModule() {
    override fun applyOptions(builder: com.bumptech.glide.GlideBuilder) {
        super.applyOptions(builder)
        
        // Set memory cache size (20% of available memory)
        val memoryCacheSizeBytes = 1024 * 1024 * 20 // 20 MB
        builder.setMemoryCache(com.bumptech.glide.load.engine.cache.LruResourceCache(memoryCacheSizeBytes))
        
        // Set disk cache size (100 MB)
        val diskCacheSizeBytes = 1024 * 1024 * 100 // 100 MB
        builder.setDiskCache(com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper(
            com.bumptech.glide.load.engine.cache.DiskCache.Factory.getDiskCacheDirectory(
                com.bumptech.glide.Glide.getPhotoCacheDir(com.bumptech.glide.Glide.getPhotoCacheDir(null)?.context),
                "image_cache"
            ),
            diskCacheSizeBytes
        ))
        
        // Default request options
        val defaultOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .dontAnimate()
        
        builder.setDefaultRequestOptions(defaultOptions)
    }
} 