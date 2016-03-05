package com.koenv.fsxchecklists.data

import android.content.Context
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.koenv.fsxchecklists.model.Index
import com.koenv.fsxchecklists.model.Model
import com.koenv.fsxchecklists.model.ModelIndex
import com.mikepenz.materialdrawer.holder.ImageHolder
import rx.Single
import java.io.InputStreamReader

class AndroidAssetsIndexRetriever constructor(val context: Context, val gson: Gson) : IndexRetriever {
    override fun retrieveIndex(): Single<Index> = Single.defer {
        Single.just(gson.fromJson(InputStreamReader(context.assets.open("index.json")), Index::class.java))
    }

    override fun retrieveModel(modelIndex: ModelIndex) = Single.defer {
        Single.just(gson.fromJson(InputStreamReader(context.assets.open(modelIndex.file)), Model::class.java))
    }

    override fun retrieveHeaderImage(modelIndex: ModelIndex) = Single.defer {
        Single.just(ImageHolder(BitmapFactory.decodeStream(context.assets.open(modelIndex.headerImage))))
    }
}