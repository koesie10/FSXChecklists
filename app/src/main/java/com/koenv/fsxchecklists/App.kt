package com.koenv.fsxchecklists

import android.app.Application
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        graph = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object {
        @JvmStatic
        lateinit var graph: AppComponent
    }
}