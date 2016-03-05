package com.koenv.fsxchecklists.ui

import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import com.google.gson.Gson
import com.koenv.fsxchecklists.App
import com.koenv.fsxchecklists.R
import com.koenv.fsxchecklists.bindView
import com.koenv.fsxchecklists.model.*
import com.koenv.fsxchecklists.model.item.ChecklistDrawerItem
import com.koenv.fsxchecklists.model.item.LoadingDrawerItem
import com.koenv.fsxchecklists.model.item.ModelDrawerItem
import com.koenv.fsxchecklists.util.validCompositeSubscription
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.ImageHolder
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.InputStreamReader
import javax.inject.Inject

class MainActivity : BaseActivity() {
    private val toolbar by bindView<Toolbar>(R.id.toolbar)
    private val progressBar by bindView<MaterialProgressBar>(R.id.progressBar)
    private val floatingActionButton by bindView<FloatingActionButton>(R.id.floatingActionButton)
    private val recyclerView by bindView<RecyclerView>(R.id.recyclerView)

    @Inject
    lateinit var gson: Gson

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
            updateProgressBar()
            updateBadge()

            if (selectedItems == totalItems && drawer.drawerItems.size != drawer.currentSelectedPosition) {
                floatingActionButton.show()
            } else {
                floatingActionButton.hide()
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        floatingActionButton.setOnClickListener {
            drawer.setSelectionAtPosition(drawer.currentSelectedPosition + 1, true)
        }

        progressBar.max = 1000

        compositeSubscription.add(
                Single
                        .defer { Single.just(gson.fromJson(InputStreamReader(assets.open("index.json")), Index::class.java)) }
                        .map { index ->
                            val profiles = index.flatMap { manufacturer ->
                                manufacturer.models.map { model ->
                                    val image = ImageHolder(BitmapFactory.decodeStream(assets.open(model.headerImage)))
                                    ModelDrawerItem(manufacturer, model, image)
                                }
                            }
                            profiles
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            accountHeader.addProfiles(*it.toTypedArray())
                            if (it.isNotEmpty()) {
                                accountHeader.setActiveProfile(it.first(), true)
                            }
                        }
        )
    }

    private fun updateBadge() {
        val item = drawer.getDrawerItem(drawer.currentSelection)
        if (item is ChecklistDrawerItem) {
            item.updateBadge()
            drawer.updateItem(item)
        }
    }

    private fun loadChecklists(modelIndex: ModelIndex) {
        drawer.removeAllItems()
        drawer.addItem(LoadingDrawerItem())

        compositeSubscription.add(
                Single.defer { Single.just(gson.fromJson(InputStreamReader(assets.open(modelIndex.file)), Model::class.java)) }
                        .map {
                            it.checklists.map { checklist ->
                                ChecklistDrawerItem(checklist, checklist.items.map { item -> CheckableChecklistItem(item) })
                            }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
            // TODO: Reset checklist
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
