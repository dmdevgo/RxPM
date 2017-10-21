package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.jakewharton.rxrelay2.BehaviorRelay
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asConsumer
import me.dmdev.rxpm.asObservable

/**
 * This interface contains extensions for [PresentationModel] that bind to the [MapPmView].
 * See [MapPresentationModel] for how to use it.
 *
 * @author Dmitriy Gorbunov
 */
interface MapPmExtension {

    /**
     * The state of [map][GoogleMap] readiness.
     */
    val mapReadyState: MapReadyState

    /**
     * Returns a [command][PresentationModel.Command] that will collect values until the [view][MapPmView] binds to the [map][GoogleMap].
     * Use to represent a command to the [MapPmView], e.g. pin moves or zoom changes.
     *
     * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
     */
    fun <T> PresentationModel.mapCommand(bufferSize: Int? = null): PresentationModel.Command<T> {
        return Command(mapReadyState.observable.map { !it }, bufferSize)
    }

    /**
     * This class stores the state of [map][GoogleMap] readiness.
     */
    class MapReadyState {
        private val ready = BehaviorRelay.createDefault(false)
        internal val consumer get() = ready.asConsumer()

        /**
         * Observable of the state of [map][GoogleMap] readiness.
         */
        val observable get() = ready.asObservable()
    }
}