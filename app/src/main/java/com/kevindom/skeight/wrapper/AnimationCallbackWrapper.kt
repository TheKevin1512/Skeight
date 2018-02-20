package com.kevindom.skeight.wrapper

import android.graphics.drawable.Drawable
import android.support.graphics.drawable.Animatable2Compat

class AnimationCallbackWrapper : Animatable2Compat.AnimationCallback() {

    private var callbackStart: ((Drawable) -> Unit)? = null
    private var callbackEnd: ((Drawable) -> Unit)? = null

    fun onEnd(callbackEnd: ((Drawable) -> Unit)? = null) {
        this.callbackEnd = callbackEnd
    }

    override fun onAnimationEnd(drawable: Drawable) {
        super.onAnimationEnd(drawable)
        callbackEnd?.invoke(drawable)
    }

    fun onStart(callbackStart: ((Drawable) -> Unit)? = null) {
        this.callbackStart = callbackStart
    }

    override fun onAnimationStart(drawable: Drawable) {
        super.onAnimationStart(drawable)
        callbackStart?.invoke(drawable)
    }
}

inline fun observe(wrap: AnimationCallbackWrapper.() -> Unit) : AnimationCallbackWrapper {
    val wrapper = AnimationCallbackWrapper()
    wrapper.wrap()
    return wrapper
}