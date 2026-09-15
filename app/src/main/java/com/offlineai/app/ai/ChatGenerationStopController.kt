package com.offlineai.app.ai

import java.util.concurrent.atomic.AtomicBoolean

class ChatGenerationStopController {

    private val stopRequested = AtomicBoolean(false)

    fun reset() {
        stopRequested.set(false)
    }

    fun requestStop() {
        stopRequested.set(true)
    }

    fun isStopRequested(): Boolean {
        return stopRequested.get()
    }
}
