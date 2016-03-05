package com.koenv.fsxchecklists.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.koenv.fsxchecklists.App
import com.koenv.fsxchecklists.R
import com.koenv.fsxchecklists.bindView
import com.koenv.fsxchecklists.data.IndexRetriever
import com.koenv.fsxchecklists.model.CheckableChecklistItem
import com.koenv.fsxchecklists.model.Checklist
import com.koenv.fsxchecklists.model.ModelIndex
import com.koenv.fsxchecklists.model.item.ChecklistDrawerItem
import com.koenv.fsxchecklists.model.item.LoadingDrawerItem
import com.koenv.fsxchecklists.model.item.ModelDrawerItem
import com.koenv.fsxchecklists.util.SchedulerProvider
import com.koenv.fsxchecklists.util.validCompositeSubscription
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.ImageHolder
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import javax.inject.Inject

class MainActivity : BaseActivity() {
    private val toolbar by bindView<Toolbar>(R.id.toolbar)
    private val progressBar by bindView<MaterialProgressBar>(R.id.progressBar)
    private val floatingActionButton by bindView<FloatingActionButton>(R.id.floatingActionButton)
    private val recyclerView by bindView<RecyclerView>(R.id.recyclerView)

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var indexRetriever: IndexRetriever
    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private val compositeSubscription by validCompositeSubscription()

    private lateinit var accountHeader: AccountHeader
    private lateinit var drawer: Drawer
    private lateinit var adapter: ChecklistAdapter

    private var selectedItems = 0
    private var totalItems = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        App.graph.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        accountHeader = AccountHeaderBuilder()
                .withActivity(this)
                .withOnAccountHeaderListener { view, iProfile, b ->
                    val profile = iProfile as ModelDrawerItem
                    accountHeader.setHeaderBackground(profile.imageHeader)
                    floatingActionButton.hide()
                    loadChecklists(profile.model)
                    false
                }
                .withProfileImagesVisible(false)
                .build()

        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withOnDrawerItemClickListener { view, i, iDrawerItem ->
                    if (iDrawerItem is ChecklistDrawerItem) {
                        loadChecklist(iDrawerItem.checklist, iDrawerItem.items)
                    }
                    false
                }
                .build()

        adapter = ChecklistAdapter(this, listOf(), {
            selectedItems += if (it) 1 else -1
            updateChecklist()
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        floatingActionButton.setOnClickListener {
            drawer.setSelectionAtPosition(drawer.currentSelectedPosition + 1, true)
        }

        progressBar.max = 1000

        compositeSubscription.add(
                indexRetriever
                        .retrieveIndex()
                        .map { index ->
                            index.flatMap { manufacturer ->
                                manufacturer.models.map { model ->
                                    val item = ModelDrawerItem(manufacturer, model, ImageHolder(R.drawable.default_header))
                                    compositeSubscription.add(
                                            indexRetriever
                                                    .retrieveHeaderImage(model)
                                                    .compose(schedulerProvider.applySingleSchedulers())
                                                    .subscribe {
                                                        item.imageHeader = it
                                                        accountHeader.updateProfile(item)
                                                    }
                                    )
                                    item
                                }
                            }
                        }
                        .compose(schedulerProvider.applySingleSchedulers())
                        .subscribe {
                            accountHeader.addProfiles(*it.toTypedArray())
                            if (it.isNotEmpty()) {
                                accountHeader.setActiveProfile(it.first(), true)
                            }
                        }
        )
    }

    private fun loadChecklists(modelIndex: ModelIndex) {
        drawer.removeAllItems()
        drawer.addItem(LoadingDrawerItem())

        compositeSubscription.add(
                indexRetriever.retrieveModel(modelIndex)
                        .map {
                            it.checklists.map { checklist ->
                                ChecklistDrawerItem(checklist, checklist.items.map { item -> CheckableChecklistItem(item) })
                            }
                        }
                        .compose(schedulerProvider.applySingleSchedulers())
                        .subscribe {
                            drawer.removeAllItems()
                            drawer.addItems(*it.toTypedArray())
                            drawer.setSelection(it.first())
                        }
        )
    }

    private fun loadChecklist(checklist: Checklist, items: List<CheckableChecklistItem>) {
        floatingActionButton.hide()
        supportActionBar!!.title = checklist.name
        adapter.items = items
        adapter.notifyDataSetChanged()

        selectedItems = items.count { it.isChecked }
        totalItems = items.size
        updateProgressBar(1000)
    }

    private fun updateProgressBar(duration: Long = 300) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", ((selectedItems / totalItems.toFloat()) * 1000).toInt())
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    private fun updateChecklist(progressBarDuration: Long = 300) {
        updateProgressBar(progressBarDuration)
        updateBadge()

        if (selectedItems == totalItems && drawer.drawerItems.size != drawer.currentSelectedPosition) {
            floatingActionButton.show()
        } else {
            floatingActionButton.hide()
        }
    }

    private fun updateBadge() {
        val item = drawer.getDrawerItem(drawer.currentSelection)
        if (item is ChecklistDrawerItem) {
            item.updateBadge()
            drawer.updateItem(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeSubscription.unsubscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_reset -> {
            MaterialDialog.Builder(this)
                    .content(R.string.reset_which)
                    .positiveText(R.string.reset_this_one)
                    .onPositive { materialDialog, dialogAction ->
                        val drawerItem = drawer.getDrawerItem(drawer.currentSelection)
                        if (drawerItem is ChecklistDrawerItem) {
                            drawerItem.items.forEach {
                                it.isChecked = false
                            }
                        }

                        selectedItems = 0
                        adapter.notifyDataSetChanged()
                        updateChecklist(1000)
                    }
                    .negativeText(R.string.reset_all)
                    .onNegative { materialDialog, dialogAction ->
                        for (drawerItem in drawer.drawerItems) {
                            if (drawerItem is ChecklistDrawerItem) {
                                drawerItem.items.forEach {
                                    it.isChecked = false
                                }
                                drawerItem.updateBadge()
                                drawer.updateItem(drawerItem)
                            }
                        }

                        selectedItems = 0
                        adapter.notifyDataSetChanged()
                        updateChecklist(1000)
                    }
                    .neutralText(R.string.cancel)
                    .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
