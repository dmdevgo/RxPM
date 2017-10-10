package me.dmdev.rxpm.map

import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
abstract class MapPresentationModel : PresentationModel(),
                                      MapPmExtension {

    override val mapReadyState = MapPmExtension.MapReadyState()
}