package com.koenv.fsxchecklists.util

import rx.subscriptions.CompositeSubscription
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CompositeSubscriptionDelegate: ReadOnlyProperty<Any?, CompositeSubscription> {
    private var compositeSubscription: CompositeSubscription? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): CompositeSubscription {
        if (compositeSubscription == null || compositeSubscription!!.isUnsubscribed) {
            compositeSubscription = CompositeSubscription()
        }
        return compositeSubscription!!
    }
}

fun validCompositeSubscription() : ReadOnlyProperty<Any?, CompositeSubscription> = CompositeSubscriptionDelegate()