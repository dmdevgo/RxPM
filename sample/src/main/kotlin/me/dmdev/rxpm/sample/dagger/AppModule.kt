package me.dmdev.rxpm.sample.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import me.dmdev.rxpm.sample.util.NetworkHelper
import me.dmdev.rxpm.sample.util.PhoneUtil
import me.dmdev.rxpm.sample.util.ResourceProvider
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext() = context

    @Provides
    @Singleton
    fun provideResourceProvider(context: Context) = ResourceProvider(context)

    @Provides
    @Singleton
    fun provideNetworkHelper(context: Context) = NetworkHelper(context)

    @Provides
    @Singleton
    fun providePhoneUtil() = PhoneUtil()

}