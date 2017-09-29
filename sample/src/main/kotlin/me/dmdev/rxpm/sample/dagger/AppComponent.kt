package me.dmdev.rxpm.sample.dagger

import dagger.Component
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.util.NetworkHelper
import me.dmdev.rxpm.sample.util.PhoneUtil
import me.dmdev.rxpm.sample.util.ResourceProvider
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun resourceProvider(): ResourceProvider
    fun networkHelper(): NetworkHelper
    fun phoneUtil(): PhoneUtil
    fun authModel(): AuthModel
}