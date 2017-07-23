package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test

/**
 * @author Dmitriy Gorbunov
 */
class PmExtensionsTest {

    @Test
    fun testSingleBindProgress() {
        val progress = BehaviorRelay.createDefault<Boolean>(false)
        val to = TestObserver<Int>()
        val toProgress = TestObserver<Boolean>()

        progress.subscribe(toProgress)

        Single.just(1)
                .bindProgress(progress)
                .subscribe(to)

        to.assertResult(1)

        toProgress.assertValues(false, true, false)
        toProgress.assertNoErrors()
    }

    @Test
    fun testSingleBindProgressOnFailure() {
        val progress = BehaviorRelay.createDefault<Boolean>(false)
        val to = TestObserver<Int>()
        val toProgress = TestObserver<Boolean>()
        val error = IllegalArgumentException()

        progress.subscribe(toProgress)

        Single.error<Int>(error)
                .bindProgress(progress)
                .subscribe(to)

        to.assertNotComplete()
        to.assertError(error)

        toProgress.assertValues(false, true, false)
        toProgress.assertNoErrors()
    }

    @Test
    fun testCompletableBindProgress() {
        val progress = BehaviorRelay.createDefault<Boolean>(false)
        val to = TestObserver<Int>()
        val toProgress = TestObserver<Boolean>()

        progress.subscribe(toProgress)

        Completable.complete()
                .bindProgress(progress)
                .subscribe(to)

        to.assertComplete()
        to.assertNoErrors()

        toProgress.assertValues(false, true, false)
        toProgress.assertNoErrors()
    }

    @Test
    fun testCompletableBindProgressOnFailure() {
        val progress = BehaviorRelay.createDefault<Boolean>(false)
        val to = TestObserver<Int>()
        val toProgress = TestObserver<Boolean>()
        val error = IllegalArgumentException()

        progress.subscribe(toProgress)

        Completable.error(error)
                .bindProgress(progress)
                .subscribe(to)

        to.assertNotComplete()
        to.assertError(error)

        toProgress.assertValues(false, true, false)
        toProgress.assertNoErrors()
    }

    @Test
    fun testSkipWhileProgress() {
        val progress = BehaviorRelay.createDefault<Boolean>(false)
        val relay = PublishRelay.create<Int>()
        val to = TestObserver<Int>()

        relay.skipWhileProgress(progress).subscribe(to)

        relay.accept(1)
        relay.accept(2)
        progress.accept(true)
        relay.accept(3)
        relay.accept(4)
        progress.accept(false)
        relay.accept(5)
        relay.accept(6)

        to.assertValues(1, 2, 5, 6)
        to.assertNoErrors()
    }

}