package me.dmdev.rxpm.map

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import me.dmdev.rxpm.asConsumer
import me.dmdev.rxpm.asObservable
import me.dmdev.rxpm.bufferWhileIdle

/**
 * @author Dmitriy Gorbunov
 */
interface MapPmExtension {

    val mapReadiness: MapReadiness

    fun <T> Observable<T>.bufferWhileMapUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(mapReadiness.state.map { !it }, bufferSize)
    }

    class MapReadiness {
        private val ready = BehaviorRelay.createDefault(false)
        internal val consumer = ready.asConsumer()
        val state = ready.asObservable()
    }
}