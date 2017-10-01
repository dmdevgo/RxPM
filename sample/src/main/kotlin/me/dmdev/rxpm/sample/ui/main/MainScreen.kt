package me.dmdev.rxpm.sample.ui.main

import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.ui.base.Screen

/**
 * @author Dmitriy Gorbunov
 */
class MainScreen : Screen<MainPm>() {

    override val screenLayout = R.layout.screen_main

    override fun providePresentationModel() = MainPm()

}