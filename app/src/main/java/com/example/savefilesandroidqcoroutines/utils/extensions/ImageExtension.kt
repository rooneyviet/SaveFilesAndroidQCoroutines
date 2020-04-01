package com.example.savefilesandroidqcoroutines.utils.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

fun ImageView.loadNormalImage(url: String, mWitdh: Int = 0, mHeight: Int = 0, isGif: Boolean = false, loadOnlyFromCache: Boolean = false, onLoadingFinished: () -> Unit = {}, onLoadingError: () -> Unit = {}) {
    if (url.isEmpty()){
        Glide.with(this).clear(this)
        return
    }

    val listener = object : RequestListener<Drawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingError()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished()
            return false
        }
    }

    val gifListener = object : RequestListener<GifDrawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingError()
            return false
        }

        override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished()
            return false
        }

    }
    if(isGif){
        Glide.with(this)
            .asGif()
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .override(mWitdh,mHeight)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).onlyRetrieveFromCache(loadOnlyFromCache))
            .listener(gifListener)
            .into(this)
    } else {
        Glide.with(this)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .override(mWitdh,mHeight)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).onlyRetrieveFromCache(loadOnlyFromCache))
            .listener(listener)
            .into(this)
    }
}