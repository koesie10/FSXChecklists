package com.koenv.fsxchecklists.model.item

import android.support.v7.widget.RecyclerView
import android.view.View
import com.koenv.fsxchecklists.R
import com.mikepenz.fastadapter.utils.ViewHolderFactory
import com.mikepenz.materialdrawer.model.AbstractDrawerItem

class LoadingDrawerItem : AbstractDrawerItem<LoadingDrawerItem, LoadingDrawerItem.ViewHolder>() {
    override fun getLayoutRes() = R.layout.item_loading

    override fun getType() = R.id.loading_drawer_item

    override fun bindView(holder: ViewHolder) {

    }

    override fun getFactory() = ItemFactory()

    class ItemFactory : ViewHolderFactory<ViewHolder> {
        override fun create(view: View) = ViewHolder(view)
    }

    class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}