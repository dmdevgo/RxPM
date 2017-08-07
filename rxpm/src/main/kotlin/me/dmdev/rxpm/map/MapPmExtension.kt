package me.dmdev.rxpm.map

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asConsumer
import me.dmdev.rxpm.asObservable
import me.dmdev.rxpm.bufferWhileIdle

/**
 * @author Dmitriy Gorbunov
 */
interface MapPmExtension {

    val mapReadyState: MapReadyState

    fun <T> Observable<T>.bufferWhileMapUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(mapReadyState.observable.map { !it }, bufferSize)
    }

    fun <T> PresentationModel.MapCommand(bufferSize: Int? = null): PresentationModel.Command<T> {
        return Command(mapReadyState.observable.map { !it }, bufferSize)
    }

    class MapReadyState {
        private val ready = BehaviorRelay.createDefault(false)
        internal val consumer = ready.asConsumer()
        val observable = ready.asObservable()
    }
}