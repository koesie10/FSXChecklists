package com.koenv.fsxchecklists.data

import android.graphics.Bitmap
import com.koenv.fsxchecklists.model.Index
import com.koenv.fsxchecklists.model.Model
import com.koenv.fsxchecklists.model.ModelIndex
import com.mikepenz.materialdrawer.holder.ImageHolder
import rx.Single

interface IndexRetriever {
    fun retrieveIndex(): Single<Index>

    fun retrieveModel(modelIndex: ModelIndex): Single<Model>

    fun retrieveHeaderImage(modelIndex: ModelIndex): Single<ImageHolder>
}