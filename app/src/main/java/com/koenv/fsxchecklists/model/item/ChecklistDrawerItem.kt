package com.koenv.fsxchecklists.model.item

import com.koenv.fsxchecklists.R
import com.koenv.fsxchecklists.model.CheckableChecklistItem
import com.koenv.fsxchecklists.model.Checklist
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem

class ChecklistDrawerItem(val checklist: Checklist, val items: List<CheckableChecklistItem>): PrimaryDrawerItem() {
    init {
        withName(checklist.name)
        withIdentifier(checklist.hashCode().toLong())
        updateBadge()
    }

    fun updateBadge() {
        withBadge(if (items.all { it.isChecked }) R.string.badge_complete else R.string.badge_incomplete)
    }
}