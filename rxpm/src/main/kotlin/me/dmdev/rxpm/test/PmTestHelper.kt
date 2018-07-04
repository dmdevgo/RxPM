package me.dmdev.rxpm.test

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.*

/**
 * Helps to test [PresentationModel].
 *
 * @param pm presentation model under test.
 * @since 1.2
 */
class PmTestHelper(val pm: PresentationModel) {

    private val lifecycleStates = PresentationModel.Lifecycle.values()

    /**
     * Sets the lifecycle of the [presentation model][pm] under test to the specified [state][lifecycleState].
     * This will also create natural sequence of states before the requested one.
     *
     * **Note** that because of it's nature [Command][PresentationModel.Command] emits items right away
     * only in [BINDED] lifecycle state. So if you want to test it, be sure to set the state.
     *
     * @param lifecycleState lifecycle state to set to.
     * @param shortSequence defines if the sequence of states should be short.
     * It makes sense for the [DESTROYED] state, because there is two possible sequences of states:
     * long (created, binded, unbinded, destroyed), and short (created, destroyed).
     * By default is false, means the long sequence is used.
     *
     * @throws IllegalStateException if requested state is not acceptable considering the current state.
     */
    fun setLifecycleTo(
        lifecycleState: PresentationModel.Lifecycle,
        shortSequence: Boolean = false
    ) {

        checkStateAllowed(lifecycleState)

        val sequenceOfLifecycleStateOrdinals = when {
            isBindedAgain(lifecycleState) -> listOf(BINDED.ordinal)

            isShortDestroyed(lifecycleState, shortSequence) -> {
                listOf(CREATED.ordinal, DESTROYED.ordinal)
            }

            else -> listOfSequencedStateOrdinals(lifecycleState)
        }

        sequenceOfLifecycleStateOrdinals.forEach { ordinal ->
            pm.lifecycleConsumer.accept(lifecycleStates[ordinal])
        }
    }

    private fun checkStateAllowed(lifecycleState: PresentationModel.Lifecycle) {
        pm.currentLifecycleState?.let { currentState ->
            if (lifecycleState <= currentState && !isBindedAgain(lifecycleState)) {
                throw IllegalStateException(
                    "You can't set lifecycle state as $lifecycleState when it already is $pm.currentLifecycleState."
                )
            }
        }
    }

    private fun isBindedAgain(lifecycleState: PresentationModel.Lifecycle): Boolean {
        return pm.currentLifecycleState == UNBINDED && lifecycleState == BINDED
    }

    private fun isShortDestroyed(
        lifecycleState: PresentationModel.Lifecycle,
        shortSequence: Boolean
    ): Boolean {
        return lifecycleState == DESTROYED && shortSequence
    }

    private fun listOfSequencedStateOrdinals(lifecycleState: PresentationModel.Lifecycle): List<Int> {
        val nextStateOrdinal = pm.currentLifecycleState?.let { it.ordinal + 1 } ?: 0
        val wantedStateOrdinal = lifecycleState.ordinal
        return (nextStateOrdinal..wantedStateOrdinal).toList()
    }
}