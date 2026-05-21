package com.dskochilov.easydinner

import android.app.Application
import com.dskochilov.easydinner.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EasyDinnerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyDinnerApplication)
            modules(appModule)
        }
    }
}
