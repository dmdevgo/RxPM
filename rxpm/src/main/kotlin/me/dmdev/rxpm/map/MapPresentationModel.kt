package me.dmdev.rxpm.map

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asConsumer
import me.dmdev.rxpm.bufferWhileIdle

/**
 * @author Dmitriy Gorbunov
 */
abstract class MapPresentationModel : PresentationModel() {

    private val mapReady = BehaviorRelay.createDefault<Boolean>(false)
    val mapReadyConsumer = mapReady.asConsumer()

    protected fun <T> Observable<T>.bufferWhileMapUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(mapReady.map { !it }, bufferSize)
    }
}