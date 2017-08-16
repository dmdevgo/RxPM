package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
interface MapPmView<PM> : AndroidPmView<PM> where PM : PresentationModel, PM : MapPmExtension {
    var mapView: MapView?
    var googleMap: GoogleMap?
    fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)
}