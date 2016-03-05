package com.koenv.fsxchecklists

import com.google.gson.GsonBuilder
import com.koenv.fsxchecklists.model.ManufacturerIndex
import com.koenv.fsxchecklists.data.ManufacturerIndexDeserializer
import com.koenv.fsxchecklists.model.ModelIndex
import com.koenv.fsxchecklists.data.ModelIndexDeserializer
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
    fun provideGson() = GsonBuilder()
            .registerTypeAdapter(ManufacturerIndex::class.java, ManufacturerIndexDeserializer())
            .registerTypeAdapter(ModelIndex::class.java, ModelIndexDeserializer())
            .create()
}