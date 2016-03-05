package com.koenv.fsxchecklists.model.item

import com.koenv.fsxchecklists.model.ManufacturerIndex
import com.koenv.fsxchecklists.model.ModelIndex
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem

class ModelProfileDrawerItem(val manufacturer: ManufacturerIndex, val model: ModelIndex, val imageHeader: ImageHolder) : ProfileDrawerItem() {
    init {
        withName("${manufacturer.name} ${model.name}")
    }
}