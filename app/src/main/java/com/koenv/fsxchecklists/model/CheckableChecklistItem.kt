package com.koenv.fsxchecklists.model

data class CheckableChecklistItem(val item: ChecklistItem, var isChecked: Boolean = false)