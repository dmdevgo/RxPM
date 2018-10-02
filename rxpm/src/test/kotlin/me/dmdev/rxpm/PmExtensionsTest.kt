package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class PmExtensionsTest {

    private lateinit var progress: BehaviorRelay<Boolean>

    private lateinit var isIdleObservable: BehaviorRelay<Boolean>
    private lateinit var relay: PublishRelay<Int>

    @Before fun setUp() {
        progress = BehaviorRelay.createDefault(false)

        relay = PublishRelay.create()
        isIdleObservable = BehaviorRelay.createDefault(false)
    }

    @Test fun bindProgressSingle() {
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Single.just(1)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertResult(1)
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun bindProgressOnErrorSingle() {
        val error = IllegalArgumentException()
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Single.error<Int>(error)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertError(error)
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun bindProgressMaybe() {
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Maybe.just(1)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertResult(1)
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun bindProgressOnErrorMaybe() {
        val error = IllegalArgumentException()
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Maybe.error<Int>(error)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertError(error)
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun bindProgressOnEmptyMaybe() {
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Maybe.empty<Int>()
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver
            .assertNoValues()
            .assertComplete()
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun bindProgressCompletable() {
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Completable.complete()
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertComplete()
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun bindProgressOnErrorCompletable() {
        val error = IllegalArgumentException()
        val testObserver = TestObserver<Int>()
        val progressObserver = progress.test()

        Completable.error(error)
            .bindProgress(progress)
            .subscribe(testObserver)

        testObserver.assertError(error)
        progressObserver.assertValuesOnly(false, true, false)
    }

    @Test fun skipWhileInProgress() {
        val testObserver = relay.skipWhileInProgress(progress).test()

        relay.accept(1)
        relay.accept(2)
        progress.accept(true)
        relay.accept(3)
        relay.accept(4)
        progress.accept(false)
        relay.accept(5)
        relay.accept(6)

        testObserver.assertValuesOnly(1, 2, 5, 6)
    }

    @Test fun bufferWhileIdleReceiveItems() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun bufferWhileIdleBlockItemsWhenIdle() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun bufferWhileIdlePassItemsAfterIdle() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValuesOnly(1, 2, 3, 4)
    }

    @Test fun bufferWhileIdleRestrictBufferedItemsCount() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable, bufferSize = 1).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValuesOnly(1, 2, 4)
    }

    @Test fun bufferWhileIdleCanStartIdlingWhenConsumingValue() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable)
            .doOnNext { value ->
                // Use already consumed value to open buffer
                if (value == 2) isIdleObservable.accept(true)
            }
            .test()

        relay.accept(1)
        relay.accept(2)
        // Here idling will begin
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValuesOnly(1, 2, 3, 4)
    }

    @Test fun bufferWhileIdleOpensOneBufferAtATime() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)

        testObserver.assertValuesOnly(1, 2, 3, 4)
    }

    @Test fun bufferWhileIdleNoReactionOnMultipleCloses() {
        val testObserver = relay.bufferWhileIdle(isIdleObservable).test()

        relay.accept(1)
        relay.accept(2)
        isIdleObservable.accept(true)
        relay.accept(3)
        relay.accept(4)
        isIdleObservable.accept(false)
        isIdleObservable.accept(false)

        testObserver.assertValuesOnly(1, 2, 3, 4)
    }

    @Test fun bufferWhileIdleWithObservable() {
        val testObserver = Observable.just(1).bufferWhileIdle(isIdleObservable).test()

        testObserver.assertResult(1)
    }
}