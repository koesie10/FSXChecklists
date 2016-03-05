package com.koenv.fsxchecklists.ui

import android.os.Bundle
import com.koenv.fsxchecklists.App
import com.koenv.fsxchecklists.R

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        App.graph.inject(this)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
