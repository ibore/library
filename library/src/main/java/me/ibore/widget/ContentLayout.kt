package me.ibore.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import me.ibore.ktx.dp2px

class ContentLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    @IntDef(HORIZONTAL, VERTICAL)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OrientationMode


    private val titleView: AppCompatTextView = AppCompatTextView(context)
    private val contentView: AppCompatTextView = AppCompatTextView(context)
    private val arrowView: AppCompatImageView = AppCompatImageView(context)
    private val errorView: AppCompatTextView = AppCompatTextView(context)

    @OrientationMode
    private var mOrientation: Int = HORIZONTAL
    private var titleMinWidth = 0
    private var titleMaxWidth = 0
    private var distance = 0

    init {

        titleView.text = "测试标题"
        contentView.text = "测试内容"
        addView(titleView)
        addView(contentView)
        addView(arrowView)
        addView(errorView)
        clipToPadding = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        var layoutWidth = paddingLeft + paddingEnd
        var layoutHeight = paddingTop + paddingBottom
        var heightUsed = 0


        setMeasuredDimension(layoutWidth, layoutHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mOrientation == HORIZONTAL) {
            onHorizontalLayout(changed, l, t, r, b)
        } else {
            onVerticalLayout(changed, l, t, r, b)
        }
    }

    protected fun onHorizontalLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        titleView.layout(l, t, r, b)
        contentView.layout(l, t, r, b)
        arrowView.layout(l, t, r, b)
    }

    protected fun onVerticalLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        titleView.layout(l, t, r, b)
        contentView.layout(l, t, r, b)
        arrowView.layout(l, t, r, b)
    }

    fun setTextColor(@ColorInt title: Int, @ColorInt content: Int) {
        titleView.setTextColor(title)
        contentView.setTextColor(content)
    }

    fun setTextSize(title: Float, content: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, title, content)
    }

    fun setTextSize(unit: Int, title: Float, content: Float) {
        titleView.setTextSize(unit, title)
        contentView.setTextSize(unit, content)
    }

    fun setTextBold(title: Boolean, content: Boolean) {
        titleView.typeface = if (title) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        contentView.typeface = if (content) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    fun setText(@StringRes title: Int, @StringRes content: Int) {
        titleView.setText(title)
        contentView.setText(content)
    }

    fun setText(title: CharSequence, content: CharSequence) {
        titleView.text = title
        contentView.text = content
    }

    fun getTitleView(): AppCompatTextView {
        return titleView
    }

    fun getContentView(): AppCompatTextView {
        return contentView
    }

    fun getArrowView(): AppCompatImageView {
        return arrowView
    }

}
