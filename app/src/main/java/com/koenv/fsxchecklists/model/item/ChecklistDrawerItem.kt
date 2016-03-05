package com.koenv.fsxchecklists.model.item

import com.koenv.fsxchecklists.model.Checklist
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem

class ChecklistDrawerItem(val checklist: Checklist): PrimaryDrawerItem() {
    init {
        withName(checklist.name)
        withBadge("Incomplete")
    }
}