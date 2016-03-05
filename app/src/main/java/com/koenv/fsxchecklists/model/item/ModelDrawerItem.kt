package com.koenv.fsxchecklists.model.item

import android.view.View
import android.view.ViewGroup
import com.koenv.fsxchecklists.R
import com.koenv.fsxchecklists.model.ManufacturerIndex
import com.koenv.fsxchecklists.model.ModelIndex
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem

class ModelDrawerItem(val manufacturer: ManufacturerIndex, val model: ModelIndex, val imageHeader: ImageHolder) : ProfileDrawerItem() {
    init {
        withName(model.name)
        withEmail(manufacturer.name)
        withNameShown(true)
        withPostOnBindViewListener { iDrawerItem, view ->
            (view.findViewById(R.id.material_drawer_profileIcon).parent as ViewGroup).visibility = View.GONE
        }
    }
}