package me.ibore.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import me.ibore.R
import me.ibore.databinding.LayoutBottomBarBinding
import me.ibore.ktx.color
import me.ibore.ktx.dp2px
import me.ibore.ktx.drawable
import me.ibore.ktx.sp2px
import me.ibore.utils.UIUtils
import java.util.*

class BottomBarItem @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private var normalIcon: Drawable? = null//普通状态图标的资源id
    private var selectedIcon: Drawable? = null//选中状态图标的资源id
    private var title: String? = null//文本
    private var titleTextSize: Int = 12//文字大小 默认为12sp
    private var titleNormalColor: Int = 0    //描述文本的默认显示颜色
    private var titleSelectedColor: Int = 0  //述文本的默认选中显示颜色
    private var marginTop: Int = 0//文字和图标的距离,默认0dp
    private var openTouchBg: Boolean = false// 是否开启触摸背景，默认关闭
    private var touchDrawable: Drawable? = null//触摸时的背景
    private var iconWidth: Int = 0//图标的宽度
    private var iconHeight: Int = 0//图标的高度
    private var itemPadding: Int = 0//BottomBarItem的padding
    private var unreadTextSize: Int = 10 //未读数默认字体大小10sp
    private var unreadNumThreshold: Int = 99//未读数阈值
    private var unreadTextColor: Int = 0//未读数字体颜色
    private var unreadTextBg: Drawable? //未读数字体背景
    private var msgTextSize: Int = 6 //消息默认字体大小6sp
    private var msgTextColor: Int = 0//消息文字颜色
    private var msgTextBg: Drawable? //消息文字背景
    private var notifyPointBg: Drawable?//小红点背景
    private val binding: LayoutBottomBarBinding
    init {
        val osa = context.obtainStyledAttributes(attrs, R.styleable.BottomBarItem)
        normalIcon = osa.getDrawable(R.styleable.BottomBarItem_bblIconNormal)
        selectedIcon = osa.getDrawable(R.styleable.BottomBarItem_bblIconSelected)

        title = osa.getString(R.styleable.BottomBarItem_bblText)
        titleTextSize = osa.getDimensionPixelSize(R.styleable.BottomBarItem_bblTextSize, sp2px(titleTextSize.toFloat()))
        titleNormalColor = osa.getColor(R.styleable.BottomBarItem_bblTextColorNormal, color(R.color.text_weak))
        titleSelectedColor =
                osa.getColor(R.styleable.BottomBarItem_bblTextColorSelected, color(R.color.red))

        marginTop = osa.getDimensionPixelSize(
            R.styleable.BottomBarItem_bblMarginTextIcon,
            dp2px(0F)
        )

        openTouchBg = osa.getBoolean(R.styleable.BottomBarItem_bblOpenTouchBg, openTouchBg)
        touchDrawable = osa.getDrawable(R.styleable.BottomBarItem_bblTouchDrawable)

        iconWidth = osa.getDimensionPixelSize(R.styleable.BottomBarItem_bblIconWidth, 0)
        iconHeight = osa.getDimensionPixelSize(R.styleable.BottomBarItem_bblIconHeight, 0)
        itemPadding = osa.getDimensionPixelSize(R.styleable.BottomBarItem_bblItemPadding, 0)

        unreadTextSize =
                osa.getDimensionPixelSize(R.styleable.BottomBarItem_bblUnreadTextSize, sp2px( unreadTextSize.toFloat()))
        unreadTextColor = osa.getColor(R.styleable.BottomBarItem_bblUnreadTextColor, color(R.color.white))
        unreadTextBg = osa.getDrawable(R.styleable.BottomBarItem_bblUnreadTextBg)

        msgTextSize =
                osa.getDimensionPixelSize(R.styleable.BottomBarItem_bblMsgTextSize, sp2px(msgTextSize.toFloat()))
        msgTextColor = osa.getColor(R.styleable.BottomBarItem_bblMsgTextColor, color(R.color.white))
        msgTextBg = osa.getDrawable(R.styleable.BottomBarItem_bblMsgTextBg)

        notifyPointBg = osa.getDrawable(R.styleable.BottomBarItem_bblNotifyPointBg)

        unreadNumThreshold = osa.getInteger(R.styleable.BottomBarItem_bblUnreadThreshold, unreadNumThreshold)
        osa.recycle()
        binding = LayoutBottomBarBinding.inflate(LayoutInflater.from(context))
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        if (itemPadding != 0) {
            //如果有设置item的padding
            setPadding(itemPadding, itemPadding, itemPadding, itemPadding)
        }
        binding.ivIcon.setImageDrawable(normalIcon)
        if (iconWidth != 0 && iconHeight != 0) {
            //如果有设置图标的宽度和高度，则设置ImageView的宽高
            val imageLayoutParams = binding.ivIcon.layoutParams as FrameLayout.LayoutParams
            imageLayoutParams.width = iconWidth
            imageLayoutParams.height = iconHeight
            binding.ivIcon.layoutParams = imageLayoutParams
        }
        binding.tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())//设置底部文字字体大小
        binding.tvUnReadNum.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            unreadTextSize.toFloat()
        )//设置未读数的字体大小
        binding.tvUnReadNum.setTextColor(unreadTextColor)//设置未读数字体颜色
        if (null != unreadTextBg) {
            binding.tvUnReadNum.background = unreadTextBg//设置未读数背景
        } else {
            binding.tvUnReadNum.background = drawable(R.drawable.bottom_bar_unread)
        }
        binding.tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, msgTextSize.toFloat())//设置提示文字的字体大小
        binding.tvMsg.setTextColor(msgTextColor)//设置提示文字的字体颜色
        if (null != msgTextBg) {
            binding.tvMsg.background = msgTextBg//设置提示文字的背景颜色
        } else {
            binding.tvMsg.background = drawable(R.drawable.bottom_bar_msg)
        }

        if (null != notifyPointBg) {
            binding.tvPoint.background = notifyPointBg//设置提示文字的背景颜色
        } else {
            binding.tvPoint.background = drawable(R.drawable.bottom_bar_notify_point)
        }

        binding.tvText.setTextColor(titleNormalColor)//设置底部文字字体颜色
        title?.let { binding.tvText.text = title }//设置标签文字
        val textLayoutParams = binding.tvText.layoutParams as LayoutParams
        textLayoutParams.topMargin = marginTop
        binding.tvText.layoutParams = textLayoutParams

        if (openTouchBg) {
            //如果有开启触摸背景
            background = touchDrawable
        }
    }
    fun getImageView(): ImageView {
        return binding.ivIcon
    }

    fun getTextView(): TextView {
        return binding.tvText
    }
    fun setNormalIcon(normalIcon: Drawable?) {
        this.normalIcon = normalIcon
        refreshTab()
    }

    fun setNormalIcon(resId: Int) {
        setNormalIcon(drawable(resId))
    }

    fun setSelectedIcon(selectedIcon: Drawable?) {
        this.selectedIcon = selectedIcon
        refreshTab()
    }

    fun setSelectedIcon(resId: Int) {
        setSelectedIcon(drawable(resId))
    }

    fun refreshTab(isSelected: Boolean) {
        setSelected(isSelected)
        refreshTab()
    }

    fun refreshTab() {
        binding.ivIcon.setImageDrawable(if (isSelected) selectedIcon else normalIcon)
        binding.tvText.setTextColor(if (isSelected) titleSelectedColor else titleNormalColor)
    }

    private fun setTvVisible(tv: TextView) {
        //都设置为不可见
        binding.tvUnReadNum.visibility = View.GONE
        binding.tvMsg.visibility = View.GONE
        binding.tvPoint.visibility = View.GONE
        tv.visibility = View.VISIBLE//设置为可见
    }

    fun setUnreadNum(unreadNum: Int) {
        setTvVisible(binding.tvUnReadNum)
        when {
            unreadNum <= 0 -> binding.tvUnReadNum.visibility = View.GONE
            unreadNum <= unreadNumThreshold -> binding.tvUnReadNum.text = unreadNum.toString()
            else -> binding.tvUnReadNum.text =
                String.format(Locale.CHINA, "%d+", unreadNumThreshold)
        }
    }

    fun setMsg(msg: String) {
        setTvVisible(binding.tvMsg)
        binding.tvMsg.text = msg
    }

    fun hideMsg() {
        binding.tvMsg.visibility = View.GONE
    }

    fun showNotify() {
        setTvVisible(binding.tvPoint)
    }

    fun hideNotify() {
        binding.tvPoint.visibility = View.GONE
    }

    fun create(builder: Builder): BottomBarItem {
        this.normalIcon = builder.normalIcon
        this.selectedIcon = builder.selectedIcon
        this.title = builder.title
        this.titleTextSize = builder.titleTextSize
        this.titleNormalColor = builder.titleNormalColor
        this.titleSelectedColor = builder.titleSelectedColor
        this.marginTop = builder.marginTop
        this.openTouchBg = builder.openTouchBg
        this.touchDrawable = builder.touchDrawable
        this.iconWidth = builder.iconWidth
        this.iconHeight = builder.iconHeight
        this.itemPadding = builder.itemPadding
        this.unreadTextSize = builder.unreadTextSize
        this.unreadTextColor = builder.unreadTextColor
        this.unreadTextBg = builder.unreadTextBg
        this.unreadNumThreshold = builder.unreadNumThreshold
        this.msgTextSize = builder.msgTextSize
        this.msgTextColor = builder.msgTextColor
        this.msgTextBg = builder.msgTextBg
        this.notifyPointBg = builder.notifyPointBg
        initView()
        return this
    }

    inner class Builder {
        var normalIcon: Drawable? = null//普通状态图标的资源id
        var selectedIcon: Drawable? = null//选中状态图标的资源id
        var title: String? = null//标题
        var titleTextSize: Int = 0//字体大小
        var titleNormalColor: Int = 0    //描述文本的默认显示颜色
        var titleSelectedColor: Int = 0  //述文本的默认选中显示颜色
        var marginTop: Int = 0//文字和图标的距离
        var openTouchBg: Boolean = false// 是否开启触摸背景，默认关闭
        var touchDrawable: Drawable? = null//触摸时的背景
        var iconWidth: Int = 0//图标的宽度
        var iconHeight: Int = 0//图标的高度
        var itemPadding: Int = 0//BottomBarItem的padding
        var unreadTextSize: Int = 0 //未读数字体大小
        var unreadNumThreshold: Int = 0//未读数阈值
        var unreadTextColor: Int = 0//未读数字体颜色
        var unreadTextBg: Drawable? = drawable(R.drawable.bottom_bar_unread)//未读数文字背景
        var msgTextSize: Int = 0 //消息字体大小
        var msgTextColor: Int = 0//消息文字颜色
        var msgTextBg: Drawable? = drawable(R.drawable.bottom_bar_msg)//消息提醒背景颜色
        var notifyPointBg: Drawable? = drawable(R.drawable.bottom_bar_notify_point)//小红点背景颜色

        init {
            titleTextSize = sp2px(12f)
            titleNormalColor = color(R.color.text_weak)
            titleSelectedColor = color(R.color.red)
            unreadTextSize = sp2px(10f)
            msgTextSize = sp2px(6f)
            unreadTextColor = color(R.color.white)
            unreadNumThreshold = 99
            msgTextColor = color(R.color.white)
        }

        /**
         * Sets the default icon's resourceId
         */
        fun normalIcon(normalIcon: Drawable): Builder {
            this.normalIcon = normalIcon
            return this
        }

        /**
         * Sets the selected icon's resourceId
         */
        fun selectedIcon(selectedIcon: Drawable): Builder {
            this.selectedIcon = selectedIcon
            return this
        }

        /**
         * Sets the title's resourceId
         */
        fun title(titleId: Int): Builder {
            this.title = context.getString(titleId)
            return this
        }

        /**
         * Sets the title string
         */
        fun title(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Sets the title's text size
         */
        fun titleTextSize(titleTextSize: Int): Builder {
            this.titleTextSize = sp2px(  titleTextSize.toFloat())
            return this
        }

        /**
         * Sets the title's normal color resourceId
         */
        fun titleNormalColor(titleNormalColor: Int): Builder {
            this.titleNormalColor = color(titleNormalColor)
            return this
        }

        /**
         * Sets the title's selected color resourceId
         */
        fun titleSelectedColor(titleSelectedColor: Int): Builder {
            this.titleSelectedColor = color(titleSelectedColor)
            return this
        }

        /**
         * Sets the item's margin top
         */
        fun marginTop(marginTop: Int): Builder {
            this.marginTop = marginTop
            return this
        }

        /**
         * Sets whether to open the touch background effect
         */
        fun openTouchBg(openTouchBg: Boolean): Builder {
            this.openTouchBg = openTouchBg
            return this
        }

        /**
         * Sets touch background
         */
        fun touchDrawable(touchDrawable: Drawable): Builder {
            this.touchDrawable = touchDrawable
            return this
        }

        /**
         * Sets icon's width
         */
        fun iconWidth(iconWidth: Int): Builder {
            this.iconWidth = iconWidth
            return this
        }

        /**
         * Sets icon's height
         */
        fun iconHeight(iconHeight: Int): Builder {
            this.iconHeight = iconHeight
            return this
        }


        /**
         * Sets padding for item
         */
        fun itemPadding(itemPadding: Int): Builder {
            this.itemPadding = itemPadding
            return this
        }

        /**
         * Sets unread font size
         */
        fun unreadTextSize(unreadTextSize: Int): Builder {
            this.unreadTextSize = sp2px(unreadTextSize.toFloat())
            return this
        }

        /**
         * Sets the number of unread array thresholds greater than the threshold to be displayed as n + n as the set threshold
         */
        fun unreadNumThreshold(unreadNumThreshold: Int): Builder {
            this.unreadNumThreshold = unreadNumThreshold
            return this
        }

        /**
         * Sets the message font size
         */
        fun msgTextSize(msgTextSize: Int): Builder {
            this.msgTextSize = sp2px(msgTextSize.toFloat())
            return this
        }

        /**
         * Sets the message font background
         */
        fun unreadTextBg(unreadTextBg: Drawable): Builder {
            this.unreadTextBg = unreadTextBg
            return this
        }

        /**
         * Sets unread font color
         */
        fun unreadTextColor(unreadTextColor: Int): Builder {
            this.unreadTextColor = color(unreadTextColor)
            return this
        }

        /**
         * Sets the message font color
         */
        fun msgTextColor(msgTextColor: Int): Builder {
            this.msgTextColor = color(msgTextColor)
            return this
        }

        /**
         * Sets the message font background
         */
        fun msgTextBg(msgTextBg: Drawable): Builder {
            this.msgTextBg = msgTextBg
            return this
        }

        /**
         * Set the message prompt point background
         */
        fun notifyPointBg(notifyPointBg: Drawable): Builder {
            this.notifyPointBg = notifyPointBg
            return this
        }

        /**
         * Create a BottomBarItem object
         */
        fun create(normalIcon: Drawable?, selectedIcon: Drawable?, text: String): BottomBarItem {
            this.normalIcon = normalIcon
            this.selectedIcon = selectedIcon
            title = text

            val bottomBarItem = BottomBarItem(context)
            return bottomBarItem.create(this)
        }

        fun create(normalIconId: Int, selectedIconId: Int, text: String): BottomBarItem {
            return create(drawable(normalIconId), drawable(selectedIconId), text)
        }
    }

}