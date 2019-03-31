package me.dmdev.rxpm.map

import com.google.android.gms.maps.*
import me.dmdev.rxpm.*

/**
 * Interface that need to be implemented by the View that contains a [MapView].
 */
interface MapPmView<PM> : PmView<PM> where PM : PresentationModel, PM : MapPmExtension {

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