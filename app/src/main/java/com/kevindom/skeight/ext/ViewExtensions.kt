import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.kevindom.skeight.wrapper.observe

fun <T : ViewDataBinding> Int.bind(activity: Activity): T = DataBindingUtil.setContentView(activity, this)

fun <T : ViewDataBinding> Int.bind(layoutInflater: LayoutInflater, parent: ViewGroup?): T = DataBindingUtil.inflate(layoutInflater, this, parent, false)

fun Int.str(context: Context): String = context.getString(this)

fun View.closeKeyBoard() {
    val inputMethodManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun EditText.onDoneClicked(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE)
            callback()
        false
    }
}

fun ImageView.startAnimation(@DrawableRes animationId: Int, loop: Boolean = false, reset: Boolean = false) {
    visibility = View.VISIBLE
    val animatedDrawable = AnimatedVectorDrawableCompat.create(this.context, animationId)
    animatedDrawable?.let {
        setImageDrawable(it)
        it.start()
        it.registerAnimationCallback(observe {
            onEnd {
                if (loop) {
                    animatedDrawable.start()
                } else if (reset) {
                    visibility = View.GONE
                }
            }
        })
    }
}

fun AnimatedVectorDrawableCompat.loop() {
    this.start()
    this.registerAnimationCallback(observe {
        onEnd { this@loop.start() }
    })
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}