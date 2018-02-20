import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import com.kevindom.skeight.wrapper.observe

fun <T : ViewDataBinding> Int.bind(activity: Activity): T = DataBindingUtil.setContentView(activity, this)

fun <T : ViewDataBinding> Int.bind(layoutInflater: LayoutInflater, parent: ViewGroup?): T = DataBindingUtil.inflate(layoutInflater, this, parent, false)

fun Int.str(context: Context): String = context.getString(this)

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

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}