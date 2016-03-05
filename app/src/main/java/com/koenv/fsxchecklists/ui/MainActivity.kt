package com.koenv.fsxchecklists.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.koenv.fsxchecklists.App
import com.koenv.fsxchecklists.R
import com.koenv.fsxchecklists.bindView
import com.mikepenz.materialdrawer.DrawerBuilder
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class MainActivity : BaseActivity() {
    private val toolbar by bindView<Toolbar>(R.id.toolbar)
    private val progressBar by bindView<MaterialProgressBar>(R.id.progressBar)
    private val floatingActionButton by bindView<FloatingActionButton>(R.id.floatingActionButton)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.graph.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        floatingActionButton.show()

        DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.action_reset -> {
            // TODO: Reset checklist
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
