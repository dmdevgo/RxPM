package me.dmdev.rxpm.map

import me.dmdev.rxpm.PresentationModel

/**
 * Subclass of [PresentationModel] that bind to the [MapPmView]
 *
 * If extending is not possible you can implement [MapPmExtension].
 *
 * @author Dmitriy Gorbunov
 */
abstract class MapPresentationModel : PresentationModel(),
                                      MapPmExtension {

    override val mapReadyState = MapPmExtension.MapReadyState()
}