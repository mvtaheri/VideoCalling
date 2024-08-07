package com.vahid.videocalling.di

import com.vahid.videocalling.VideoCallingApp
import com.vahid.videocalling.connect.ConnectViewModel
import com.vahid.videocalling.video.VideoCallViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    factory {
        val app = androidContext().applicationContext as VideoCallingApp
        app.client
    }

    viewModelOf(::ConnectViewModel)
    viewModelOf(::VideoCallViewModel)
}