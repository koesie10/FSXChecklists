package com.koenv.fsxchecklists.util

import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

interface SchedulerProvider {
    fun <T> applySchedulers(): Observable.Transformer<in T, out T>
    fun <T> applySingleSchedulers(): Single.Transformer<in T, out T>

    companion object {
        val DEFAULT = object : SchedulerProvider {
            override fun <T> applySchedulers() = Observable.Transformer<T, T> {
                it
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }

            override fun <T> applySingleSchedulers() = Single.Transformer<T, T> {
                it
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}