package me.dmdev.rxpm.sample.recyclerview

import com.jakewharton.rxrelay2.*
import io.reactivex.*
import java.util.concurrent.*

class RxMutableSet<V>(
    private val mutableSet: MutableSet<V> = ConcurrentHashMap.newKeySet()
) : MutableSet<V> by mutableSet {

    private val state = BehaviorRelay.createDefault<Set<V>>(mutableSet).toSerialized()
    val observable: Observable<Set<V>> = state.hide()
    val value get() = mutableSet.toSet()

    override fun add(element: V): Boolean {
        return mutableSet.add(element).also {
            if (it) state.accept(mutableSet)
        }
    }

    override fun addAll(elements: Collection<V>): Boolean {
        return mutableSet.addAll(elements).also {
            if (it) state.accept(mutableSet)
        }
    }

    override fun clear() {
        return mutableSet.clear().also {
            state.accept(mutableSet)
        }
    }

    override fun remove(element: V): Boolean {
        return mutableSet.remove(element).also {
            if (it) state.accept(mutableSet)
        }
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        return mutableSet.removeAll(elements).also {
            if (it) state.accept(mutableSet)
        }
    }
}