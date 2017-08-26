package me.dmdev.rxpm.sample

/**
 * @author Dmitriy Gorbunov
 */

interface PmMessageHandler {
    fun handleMessage(message: PmMessage): Boolean
}

interface PmMessage

class UpMessage : PmMessage
class BackMessage : PmMessage