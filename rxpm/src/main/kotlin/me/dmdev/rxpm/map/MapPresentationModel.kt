package me.dmdev.rxpm.map

import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
class MapPresentationModel : PresentationModel(),
                             MapPmExtension {

    override val mapReadiness = MapPmExtension.MapReadiness()
}