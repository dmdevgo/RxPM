package me.dmdev.rxpm.map

import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
open class MapPresentationModel : PresentationModel(),
                                  MapPmExtension {

    override val mapReadyState = MapPmExtension.MapReadyState()
}