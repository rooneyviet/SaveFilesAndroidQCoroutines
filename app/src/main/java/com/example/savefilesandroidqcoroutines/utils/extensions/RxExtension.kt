package com.example.savefilesandroidqcoroutines.utils.extensions

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.view.RxView
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

const val DURATION_DELAY = 1000L

fun View.click(duration: Long = DURATION_DELAY) =
    RxView.clicks(this).throttleFirst(duration, TimeUnit.MILLISECONDS)

fun View.click(activity: FragmentActivity, duration: Long = DURATION_DELAY, clickSuccess: () -> Unit) =
    RxView.clicks(this)
        .throttleFirst(duration, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .autoDisposable(AndroidLifecycleScopeProvider.from(activity, Lifecycle.Event.ON_DESTROY))
        .subscribe {
            clickSuccess()
        }

fun View.click(duration: Long = DURATION_DELAY, clickSuccess: () -> Unit) =
    RxView.clicks(this)
        .throttleFirst(duration, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            clickSuccess()
        }