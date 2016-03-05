package com.koenv.fsxchecklists

import com.koenv.fsxchecklists.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(
        modules = arrayOf(AppModule::class)
)
@Singleton
interface AppComponent {
    fun inject(activity: MainActivity)
}