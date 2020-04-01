package com.example.savefilesandroidqcoroutines.utils.extensions

import android.webkit.MimeTypeMap
import java.util.*

fun getRandomString(length: Int = 20) : String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun String.getImageUrlExtension(): String {
    if ((this.contains(".png") )) {
        return ".png"
    }

    if (this.contains(".gif")) {
        return ".gif"
    }

    if (this.contains(".jpg") ||
        this.contains(".jpeg")) {
        return ".jpg"
    }

    return ".jpg"
}

fun String.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase(Locale.getDefault())) }
        ?: fallback // You might set it to */*
}