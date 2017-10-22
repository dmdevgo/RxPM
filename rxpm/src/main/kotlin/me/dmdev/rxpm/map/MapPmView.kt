package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * Interface that need to be implemented by the View that contains a [MapView].
 *
 * @author Dmitriy Gorbunov
 */
interface MapPmView<PM> : AndroidPmView<PM> where PM : PresentationModel, PM : MapPmExtension {

    /**
     *  [MapView] contained by this view.
     */
    var mapView: MapView?

    /**
     *  [GoogleMap] used by view.
     */
    var googleMap: GoogleMap?

    /**
     * Bind a [GoogleMap] to the [Presentation Model][presentationModel] in that method.
     */
    fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)

}