package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
interface MapPmView<PM> : PmView<PM> where PM : PresentationModel, PM : MapPmExtension {
    var mapView: MapView?
    var googleMap: GoogleMap?
    fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)
    fun onUnbindMapPresentationModel() {}
}