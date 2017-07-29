package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.PmView

/**
 * @author Dmitriy Gorbunov
 */
interface MapPmView<PM : MapPresentationModel> : PmView<PM> {
    var mapView: MapView?
    var googleMap: GoogleMap?
    fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)
    fun onUnbindMapPresentationModel()
}