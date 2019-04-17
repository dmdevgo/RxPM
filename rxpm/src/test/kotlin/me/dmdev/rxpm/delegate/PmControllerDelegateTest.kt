package me.dmdev.rxpm.delegate

import android.view.*
import com.bluelinelabs.conductor.*
import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.util.*
import org.junit.*
import org.junit.Test
import org.mockito.*
import kotlin.test.*


class PmControllerDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var pmController: PmController<PresentationModel>
    private lateinit var view: View
    private lateinit var delegate: PmControllerDelegate<PresentationModel, PmController<PresentationModel>>
    private lateinit var controllerLifecycleListener: Controller.LifecycleListener

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        pmController = mockPmController()
        view = mock()
        delegate = PmControllerDelegate(pmController)
        controllerLifecycleListener = captureControllerLifecycleListener()
    }

    private fun captureControllerLifecycleListener(): Controller.LifecycleListener {
        val argument = ArgumentCaptor.forClass(Controller.LifecycleListener::class.java)
        verify(pmController).addLifecycleListener(argument.capture())
        return argument.value
    }

    private fun mockPmController(): PmController<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {

        controllerLifecycleListener.preCreateView(pmController)

        verify(pmController).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        controllerLifecycleListener.postCreateView(pmController, view)

        verify(pmController).onBindPresentationModel(pm)

        controllerLifecycleListener.preAttach(pmController, view)
        controllerLifecycleListener.postAttach(pmController, view)
        controllerLifecycleListener.preDetach(pmController, view)
        controllerLifecycleListener.postDetach(pmController, view)
        controllerLifecycleListener.preDestroyView(pmController, view)

        verify(pmController).onUnbindPresentationModel()

        controllerLifecycleListener.postDestroyView(pmController)
        controllerLifecycleListener.preDestroy(pmController)
        controllerLifecycleListener.postDestroy(pmController)

    }

    @Test fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        controllerLifecycleListener.preCreateView(pmController)
        controllerLifecycleListener.postCreateView(pmController, view)
        controllerLifecycleListener.preAttach(pmController, view)
        controllerLifecycleListener.postAttach(pmController, view)
        controllerLifecycleListener.preDetach(pmController, view)
        controllerLifecycleListener.postDetach(pmController, view)
        controllerLifecycleListener.preDestroyView(pmController, view)
        controllerLifecycleListener.postDestroyView(pmController)
        controllerLifecycleListener.preDestroy(pmController)
        controllerLifecycleListener.postDestroy(pmController)

        testObserver.assertValuesOnly(
            CREATED,
            BINDED,
            RESUMED,
            PAUSED,
            UNBINDED,
            DESTROYED
        )
    }
}