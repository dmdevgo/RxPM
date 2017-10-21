package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel

/**
 * Interface that need to be implemented by the [View][PmView] that contains a [MapView][MapView].
 *
 * @author Dmitriy Gorbunov
 */
interface MapPmView<PM> : AndroidPmView<PM> where PM : PresentationModel, PM : MapPmExtension {

    /**
     *  [MapView][MapView] for this view.
     */
    var mapView: MapView?

    /**
     *  [GoogleMap][GoogleMap] for this view.
     */
    var googleMap: GoogleMap?

    /**
     * Bind a [GoogleMap][GoogleMap] to the [Presentation Model][presentationModel] in that method.
     */
    fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)

}