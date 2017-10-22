package me.dmdev.rxpm.map

import me.dmdev.rxpm.PresentationModel

/**
 * Subclass of [PresentationModel] that binds to the [MapPmView].
 *
 * If extending is not possible you can implement [MapPmExtension] by your [PresentationModel].
 *
 * @author Dmitriy Gorbunov
 */
abstract class MapPresentationModel : PresentationModel(),
                                      MapPmExtension {

    override val mapReadyState = MapPmExtension.MapReadyState()
}