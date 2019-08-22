package me.dmdev.rxpm.util

import io.reactivex.android.plugins.*
import io.reactivex.plugins.*
import io.reactivex.schedulers.*
import org.junit.rules.*

class SchedulersRule(private val useTestScheduler: Boolean = false) : ExternalResource() {

    private lateinit var _testScheduler: TestScheduler

    val testScheduler: TestScheduler
        get() {
            check(useTestScheduler) { "TestScheduler is switched off." }
            return _testScheduler
        }

    override fun before() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        val computationScheduler = if (useTestScheduler) {
            _testScheduler = TestScheduler()
            _testScheduler
        } else {
            Schedulers.trampoline()
        }
        RxJavaPlugins.setComputationSchedulerHandler { computationScheduler }
    }

    override fun after() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}