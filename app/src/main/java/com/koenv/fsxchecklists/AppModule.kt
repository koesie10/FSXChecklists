package com.koenv.fsxchecklists

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.koenv.fsxchecklists.data.AndroidAssetsIndexRetriever
import com.koenv.fsxchecklists.data.IndexRetriever
import com.koenv.fsxchecklists.util.SchedulerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App) {
    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideIndexRetriever(context: Context, gson: Gson): IndexRetriever = AndroidAssetsIndexRetriever(context, gson)

    @Provides
    @Singleton
    fun provideSchedulerProvider() = SchedulerProvider.DEFAULT
}