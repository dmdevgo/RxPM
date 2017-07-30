package me.dmdev.rxpm.map

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.bufferWhileIdle

/**
 * @author Dmitriy Gorbunov
 */
interface MapPresentationModel {

    val mapReadyConsumer: Consumer<Boolean>
    val mapReadyState: Observable<Boolean>

    fun <T> Observable<T>.bufferWhileMapUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(mapReadyState.map { !it }, bufferSize)
    }
}