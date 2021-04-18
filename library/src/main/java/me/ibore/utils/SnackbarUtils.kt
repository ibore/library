package me.ibore.utils

import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.annotation.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import me.ibore.R
import java.lang.ref.WeakReference

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/10/16
 * desc  : utils about snackbar
</pre> *
 */
class SnackbarUtils private constructor(parent: View) {

    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Duration

    private val view: View
    private var message: CharSequence? = null
    private var messageColor = 0
    private var bgColor = 0
    private var bgResource = 0
    private var duration = 0
    private var actionText: CharSequence? = null
    private var actionTextColor = 0
    private var actionListener: View.OnClickListener? = null
    private var bottomMargin = 0
    private fun setDefault() {
        message = ""
        messageColor = COLOR_DEFAULT
        bgColor = COLOR_DEFAULT
        bgResource = -1
        duration = LENGTH_SHORT
        actionText = ""
        actionTextColor = COLOR_DEFAULT
        bottomMargin = 0
    }

    /**
     * Set the message.
     *
     * @param msg The message.
     * @return the single [SnackbarUtils] instance
     */
    fun setMessage(msg: CharSequence): SnackbarUtils {
        message = msg
        return this
    }

    /**
     * Set the color of message.
     *
     * @param color The color of message.
     * @return the single [SnackbarUtils] instance
     */
    fun setMessageColor(@ColorInt color: Int): SnackbarUtils {
        messageColor = color
        return this
    }

    /**
     * Set the color of background.
     *
     * @param color The color of background.
     * @return the single [SnackbarUtils] instance
     */
    fun setBgColor(@ColorInt color: Int): SnackbarUtils {
        bgColor = color
        return this
    }

    /**
     * Set the resource of background.
     *
     * @param bgResource The resource of background.
     * @return the single [SnackbarUtils] instance
     */
    fun setBgResource(@DrawableRes bgResource: Int): SnackbarUtils {
        this.bgResource = bgResource
        return this
    }

    /**
     * Set the duration.
     *
     * @param duration The duration.
     *
     *  * [Duration.LENGTH_INDEFINITE]
     *  * [Duration.LENGTH_SHORT]
     *  * [Duration.LENGTH_LONG]
     *
     * @return the single [SnackbarUtils] instance
     */
    fun setDuration(@Duration duration: Int): SnackbarUtils {
        this.duration = duration
        return this
    }

    /**
     * Set the action.
     *
     * @param text     The text.
     * @param listener The click listener.
     * @return the single [SnackbarUtils] instance
     */
    fun setAction(
        text: CharSequence,
        listener: View.OnClickListener
    ): SnackbarUtils {
        return setAction(text, COLOR_DEFAULT, listener)
    }

    /**
     * Set the action.
     *
     * @param text     The text.
     * @param color    The color of text.
     * @param listener The click listener.
     * @return the single [SnackbarUtils] instance
     */
    fun setAction(
        text: CharSequence,
        @ColorInt color: Int,
        listener: View.OnClickListener
    ): SnackbarUtils {
        actionText = text
        actionTextColor = color
        actionListener = listener
        return this
    }

    /**
     * Set the bottom margin.
     *
     * @param bottomMargin The size of bottom margin, in pixel.
     */
    fun setBottomMargin(@androidx.annotation.IntRange(from = 1) bottomMargin: Int): SnackbarUtils {
        this.bottomMargin = bottomMargin
        return this
    }
    /**
     * Show the snackbar.
     *
     * @param isShowTop True to show the snack bar on the top, false otherwise.
     */
    @JvmOverloads
    fun show(isShowTop: Boolean = false): Snackbar? {
        var view: View? = view
        if (view == null) return null
        if (isShowTop) {
            val suitableParent = findSuitableParentCopyFromSnackbar(view)
            var topSnackBarContainer =
                suitableParent!!.findViewWithTag<View>("topSnackBarCoordinatorLayout")
            if (topSnackBarContainer == null) {
                val topSnackBarCoordinatorLayout = CoordinatorLayout(view.context)
                topSnackBarCoordinatorLayout.tag = "topSnackBarCoordinatorLayout"
                topSnackBarCoordinatorLayout.rotation = 180f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // bring to front
                    topSnackBarCoordinatorLayout.elevation = 100f
                }
                suitableParent.addView(
                    topSnackBarCoordinatorLayout,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                topSnackBarContainer = topSnackBarCoordinatorLayout
            }
            view = topSnackBarContainer
        }
        if (messageColor != COLOR_DEFAULT) {
            val spannableString = SpannableString(message)
            val colorSpan = ForegroundColorSpan(messageColor)
            spannableString.setSpan(
                colorSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            sReference = WeakReference(Snackbar.make(view, spannableString, duration))
        } else {
            sReference = WeakReference(Snackbar.make(view, message!!, duration))
        }
        val snackbar = sReference!!.get()
        val snackbarView = snackbar!!.view as SnackbarLayout
        if (isShowTop) {
            for (i in 0 until snackbarView.childCount) {
                val child = snackbarView.getChildAt(i)
                child.rotation = 180f
            }
        }
        if (bgResource != -1) {
            snackbarView.setBackgroundResource(bgResource)
        } else if (bgColor != COLOR_DEFAULT) {
            snackbarView.setBackgroundColor(bgColor)
        }
        if (bottomMargin != 0) {
            val params = snackbarView.layoutParams as MarginLayoutParams
            params.bottomMargin = bottomMargin
        }
        if (actionText!!.isNotEmpty() && actionListener != null) {
            if (actionTextColor != COLOR_DEFAULT) {
                snackbar.setActionTextColor(actionTextColor)
            }
            snackbar.setAction(actionText, actionListener)
        }
        snackbar.show()
        return snackbar
    }
    /**
     * Show the snackbar with success style.
     *
     * @param isShowTop True to show the snack bar on the top, false otherwise.
     */
    @JvmOverloads
    fun showSuccess(isShowTop: Boolean = false) {
        bgColor = COLOR_SUCCESS
        messageColor = COLOR_MESSAGE
        actionTextColor = COLOR_MESSAGE
        show(isShowTop)
    }
    /**
     * Show the snackbar with warning style.
     *
     * @param isShowTop True to show the snackbar on the top, false otherwise.
     */
    @JvmOverloads
    fun showWarning(isShowTop: Boolean = false) {
        bgColor = COLOR_WARNING
        messageColor = COLOR_MESSAGE
        actionTextColor = COLOR_MESSAGE
        show(isShowTop)
    }
    /**
     * Show the snackbar with error style.
     *
     * @param isShowTop True to show the snackbar on the top, false otherwise.
     */
    @JvmOverloads
    fun showError(isShowTop: Boolean = false) {
        bgColor = COLOR_ERROR
        messageColor = COLOR_MESSAGE
        actionTextColor = COLOR_MESSAGE
        show(isShowTop)
    }

    companion object {
        const val LENGTH_INDEFINITE = -2
        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0
        private const val COLOR_DEFAULT = -0x1000001
        private const val COLOR_SUCCESS = -0xd44a00
        private const val COLOR_WARNING = -0x3f00
        private const val COLOR_ERROR = -0x10000
        private const val COLOR_MESSAGE = -0x1
        private var sReference: WeakReference<Snackbar?>? = null

        /**
         * Set the view to find a parent from.
         *
         * @param view The view to find a parent from.
         * @return the single [SnackbarUtils] instance
         */
        fun with(view: View): SnackbarUtils {
            return SnackbarUtils(view)
        }

        /**
         * Dismiss the snackbar.
         */
        fun dismiss() {
            if (sReference != null && sReference!!.get() != null) {
                sReference!!.get()!!.dismiss()
                sReference = null
            }
        }

        /**
         * Return the view of snackbar.
         *
         * @return the view of snackbar
         */
        fun getView(): View? {
            val snackbar = sReference!!.get() ?: return null
            return snackbar.view
        }

        /**
         * Add view to the snackbar.
         *
         * Call it after [.show]
         *
         * @param layoutId The id of layout.
         * @param params   The params.
         */
        fun addView(
            @LayoutRes layoutId: Int,
            params: ViewGroup.LayoutParams
        ) {
            val view = getView()
            if (view != null) {
                view.setPadding(0, 0, 0, 0)
                val layout = view as SnackbarLayout
                val child = LayoutInflater.from(view.getContext()).inflate(layoutId, null)
                layout.addView(child, -1, params)
            }
        }

        /**
         * Add view to the snackbar.
         *
         * Call it after [.show]
         *
         * @param child  The child view.
         * @param params The params.
         */
        fun addView(
            child: View,
            params: ViewGroup.LayoutParams
        ) {
            val view = getView()
            if (view != null) {
                view.setPadding(0, 0, 0, 0)
                val layout = view as SnackbarLayout
                layout.addView(child, params)
            }
        }

        private fun findSuitableParentCopyFromSnackbar(view: View): ViewGroup? {
            var viewTemp: View? = view
            var fallback: ViewGroup? = null
            do {
                if (viewTemp is CoordinatorLayout) {
                    return viewTemp
                }
                if (viewTemp is FrameLayout) {
                    if (viewTemp.getId() == R.id.content) {
                        return viewTemp
                    }
                    fallback = viewTemp
                }
                if (viewTemp != null) {
                    val parent = viewTemp.parent
                    viewTemp = if (parent is View) parent else null
                }
            } while (viewTemp != null)
            return fallback
        }
    }

    init {
        setDefault()
        view = parent
    }
}