package com.example.savefilesandroidqcoroutines.utils.extensions

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable

fun FragmentActivity.requestPermissions(permissions: List<String>, onRequestPermissionChecked: (Boolean) -> Unit) {
    val rxPermissions = RxPermissions(this)
    rxPermissions
        .request(permissions.joinToString (separator = ","))
        .autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY))
        .subscribe { granted ->
            onRequestPermissionChecked(granted)
        }
}