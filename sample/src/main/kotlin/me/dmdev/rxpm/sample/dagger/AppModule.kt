package me.dmdev.rxpm.sample.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import me.dmdev.rxpm.sample.api.ServerApi
import me.dmdev.rxpm.sample.api.ServerApiSimulator
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.model.TokenStorage
import me.dmdev.rxpm.sample.util.PhoneUtil
import me.dmdev.rxpm.sample.util.ResourceProvider
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideResourceProvider() = ResourceProvider(context)

    @Provides
    @Singleton
    fun providePhoneUtil() = PhoneUtil()

    @Provides
    @Singleton
    fun provideServerApi(): ServerApi = ServerApiSimulator(context)

    @Provides
    @Singleton
    fun provideTokenStorage() = TokenStorage()

    @Provides
    @Singleton
    fun provideAuthModel(api: ServerApi, tokenStorage: TokenStorage)
            = AuthModel(api, tokenStorage)

}