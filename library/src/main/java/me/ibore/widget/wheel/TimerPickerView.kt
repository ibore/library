package me.ibore.widget.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.RawRes
import androidx.appcompat.widget.AppCompatTextView
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.utils.TimeUtils
import me.ibore.utils.UIUtils
import java.text.SimpleDateFormat

class TimerPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
        LinearLayout(context, attrs, defStyleAttr) {

    @IntRange(from = 0, to = 23)
    private var startHour: Int = 0

    @IntRange(from = 0, to = 59)
    private var startMinute: Int = 0

    @IntRange(from = 0, to = 23)
    private var selectHour: Int = 0

    @IntRange(from = 0, to = 59)
    private var selectMinute: Int = 0

    @IntRange(from = 0, to = 23)
    private var endHour: Int = 23

    @IntRange(from = 0, to = 59)
    private var endMinute: Int = 59

    val hourWv: HourWheelView = HourWheelView(context, attrs, defStyleAttr)
    val minuteWv: MinuteWheelView = MinuteWheelView(context, attrs, defStyleAttr)
    val hourTv: AppCompatTextView = AppCompatTextView(context)
    val minuteTv: AppCompatTextView = AppCompatTextView(context)

    private val onSelectedListener: ((wheelView: WheelView<Int>, data: Int, position: Int) -> Unit) = { wheelView, data, position ->
        if (wheelView is HourWheelView) { // 小时选中
            when (this.hourWv.selectedHour) {
                startHour -> {
                    minuteWv.startMinute = startMinute
                    minuteWv.endMinute = 59
                }
                endHour -> {
                    minuteWv.startMinute = 0
                    minuteWv.endMinute = endMinute
                }
                else -> {
                    minuteWv.startMinute = 0
                    minuteWv.endMinute = 59
                }
            }
            selectHour = wheelView.selectedHour
            minuteWv.setSelectedMinute(selectMinute)
        } else if (wheelView is MinuteWheelView) {
            selectMinute = wheelView.selectedMinute
        }
        onTimerSelectedListener?.invoke(this, selectHour, selectMinute, selectedTimer)
    }

    var onTimerSelectedListener: ((timerPickerView: TimerPickerView, hour: Int, minute: Int, timer: String) -> Unit)? = null
        set(value) {
            field = value
            hourWv.onSelectedListener = onSelectedListener
            minuteWv.onSelectedListener = onSelectedListener
        }

    init {
        orientation = HORIZONTAL
        setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
        addView(initWheelView(hourWv))
        hourTv.setText(R.string.picker_view_hour)
        addView(initLabelView(hourTv))
        addView(initWheelView(minuteWv))
        minuteTv.setText(R.string.picker_view_minute)
        addView(initLabelView(minuteTv))
    }

    // 创建WheelView
    private fun <T : WheelView<*>> initWheelView(wheelView: T): T {
        val params = LayoutParams(dp2px(72F), LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        wheelView.apply {
            /*this.integerNeedFormat = true
            this.layoutParams = params
            this.textSize = dp2px(24F)
            this.lineSpacing = dp2px(2F)
            this.textBoundaryMargin = dp2px(10F)*/
        }
        return wheelView
    }

    // 创建 label View
    private fun initLabelView(textView: AppCompatTextView): AppCompatTextView {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.setMargins(dp2px(8F), 0, dp2px(8F), 0)
        params.gravity = Gravity.CENTER_VERTICAL
        textView.layoutParams = params
        textView.textSize = 17F
        textView.setTextColor(Color.BLACK)
        return textView
    }


    /**
     * 设置开始结束时间
     * @param startTimer    开始的时间   00-00
     * @param endTimer      结束的时间   23-59
     */
    fun setStartEndTimer(startTimer: String?, endTimer: String?) {
        try {
            val startTimers = startTimer?.split(":") ?: "00:00".split(":")
            startHour = startTimers[0].toInt()
            startMinute = startTimers[1].toInt()
        } catch (e: Exception) {
        }
        try {
            val endTimers = endTimer?.split(":") ?: "23:59".split(":")
            endHour = endTimers[0].toInt()
            endMinute = endTimers[1].toInt()
        } catch (e: Exception) {
        }
        hourWv.startHour = startHour
        hourWv.endHour = endHour
    }

    /**
     * 设置开始结束时间
     * @param startHour     开始的小时
     * @param startMinute   开始的分钟
     * @param endHour       结束的分钟
     * @param endMinute     结束的分钟
     */
    fun setStartEndTimer(@IntRange(from = 0, to = 23) startHour: Int, @IntRange(from = 0, to = 59) startMinute: Int,
                         @IntRange(from = 0, to = 23) endHour: Int, @IntRange(from = 0, to = 59) endMinute: Int) {
        this.startHour = startHour
        this.startMinute = startMinute
        this.endHour = endHour
        this.endMinute = endMinute
        hourWv.startHour = startHour
        hourWv.endHour = endHour
    }

    // 选中的小时
    var selectedHour: Int = hourWv.selectedHour
        set(value) {
            field = value
            setSelectedHour(selectedHour)
        }
        get() {
            field = hourWv.selectedHour
            return field
        }

    // 选中的小时
    val selectedHourString: String get() = hourWv.selectedHourString

    // 设置选中的小时
    fun setSelectedHour(hour: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        hourWv.setSelectedHour(hour, isSmoothScroll, smoothDuration)
    }

    // 选中的分钟
    var selectedMinute: Int = minuteWv.selectedMinute
        set(value) {
            field = value
            setSelectedMinute(selectedMinute)
        }
        get() {
            field = minuteWv.selectedMinute
            return field
        }

    // 选中的分钟
    val selectedMinuteString: String get() = minuteWv.selectedMinuteString

    /**
     * 设置选中的分钟
     *
     */
    fun setSelectedMinute(month: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        minuteWv.setSelectedMinute(month, isSmoothScroll, smoothDuration)
    }

    // 选中的时间 格式 00:00
    var selectedTimer: String = "$selectedHourString:$selectedMinuteString"
        @SuppressLint("SimpleDateFormat")
        set(value) {
            try {
                val selectedTimers = value.split(":")
                selectHour = selectedTimers[0].toInt()
                selectMinute = selectedTimers[1].toInt()
                field = value
            } catch (e: Exception) {
                field = TimeUtils.getNowString(SimpleDateFormat("HH:mm"))
                val selectedTimers = field.split(":")
                selectHour = selectedTimers[0].toInt()
                selectMinute = selectedTimers[1].toInt()
            }
            setSelectedHour(selectHour)
        }
        get() {
            field = "$selectedHourString:$selectedMinuteString"
            return field
        }


    // 标签字体大小
    var labelTextSize: Float = hourTv.textSize
        set(value) {
            field = value
            hourTv.textSize = labelTextSize
            minuteTv.textSize = labelTextSize
        }

    // 标签字体颜色
    @ColorInt
    var labelTextColor: Int = Color.BLACK
        set(value) {
            field = value
            hourTv.setTextColor(labelTextColor)
            minuteTv.setTextColor(labelTextColor)
        }

    var labelVisible: Boolean = true
        set(value) {
            field = value
            hourTv.visibility = if (labelVisible) VISIBLE else GONE
            minuteTv.visibility = if (labelVisible) VISIBLE else GONE
        }

    // 显示小时
    var hourItemVisible: Boolean = true
        set(value) {
            field = value
            hourTv.visibility = if (hourItemVisible) VISIBLE else GONE
            hourWv.visibility = if (hourItemVisible) VISIBLE else GONE
        }

    // 显示分钟
    var minuteItemVisible: Boolean = true
        set(value) {
            field = value
            minuteTv.visibility = if (minuteItemVisible) VISIBLE else GONE
            minuteWv.visibility = if (minuteItemVisible) VISIBLE else GONE
        }

    // 可见item数
    var visibleItems: Int = hourWv.visibleItems
        set(value) {
            hourWv.visibleItems = visibleItems
            minuteWv.visibleItems = visibleItems
            field = hourWv.visibleItems
        }

    // 是否重置选中下标到第一个
    var resetSelectedPosition: Boolean = hourWv.resetSelectedPosition
        set(value) {
            field = value
            hourWv.resetSelectedPosition = resetSelectedPosition
            minuteWv.resetSelectedPosition = resetSelectedPosition
        }

    // 是否自动调整字体大小，以显示完全
    var autoFitTextSize: Boolean = hourWv.autoFitTextSize
        set(value) {
            field = value
            hourWv.autoFitTextSize = autoFitTextSize
            minuteWv.autoFitTextSize = autoFitTextSize
        }

    // 是否自动调整字体大小，以显示完全
    var textSize: Int = hourWv.textSize
        set(value) {
            field = value
            hourWv.textSize = textSize
            minuteWv.textSize = textSize
        }

    // 字体
    var typeface: Typeface = hourWv.typeface
        set(value) {
            field = value
            hourWv.typeface = typeface
            minuteWv.typeface = typeface
        }

    // 文字距离边界的外边距
    var textBoundaryMargin: Int = hourWv.textBoundaryMargin
        set(value) {
            field = value
            hourWv.textBoundaryMargin = textBoundaryMargin
            minuteWv.textBoundaryMargin = textBoundaryMargin
        }

    // 未选中item文字颜色
    @ColorInt
    var normalTextColor: Int = hourWv.normalTextColor
        set(value) {
            field = value
            hourWv.normalTextColor = normalTextColor
            minuteWv.normalTextColor = normalTextColor
        }

    // 选中item文字颜色
    @ColorInt
    var selectedTextColor: Int = hourWv.selectedTextColor
        set(value) {
            field = value
            hourWv.selectedTextColor = selectedTextColor
            minuteWv.selectedTextColor = selectedTextColor
        }

    // 是否循环滚动
    var cyclic: Boolean = hourWv.cyclic
        set(value) {
            field = value
            hourWv.cyclic = cyclic
            minuteWv.cyclic = cyclic
        }

    // 行间距
    var lineSpacing: Int = hourWv.lineSpacing
        set(value) {
            field = value
            hourWv.lineSpacing = lineSpacing
            minuteWv.lineSpacing = lineSpacing
        }

    // 滚动音效
    var soundEffect: Boolean = hourWv.soundEffect
        set(value) {
            field = value
            hourWv.soundEffect = soundEffect
            minuteWv.soundEffect = soundEffect
        }

    // 滚动音效资源
    @RawRes
    var soundEffectResource: Int = hourWv.soundEffectResource
        set(value) {
            field = value
            hourWv.soundEffectResource = soundEffectResource
            minuteWv.soundEffectResource = soundEffectResource
        }

    // 滚动音效播放音量
    @FloatRange(from = 0.0, to = 1.0)
    var playVolume: Float = hourWv.playVolume
        set(value) {
            field = value
            hourWv.playVolume = playVolume
            minuteWv.playVolume = playVolume
        }

    // 是否显示分割线
    var showDivider: Boolean = hourWv.showDivider
        set(value) {
            field = value
            hourWv.showDivider = showDivider
            minuteWv.showDivider = showDivider
        }

    // 分割线颜色
    @ColorInt
    var dividerColor: Int = hourWv.dividerColor
        set(value) {
            field = value
            hourWv.dividerColor = dividerColor
            minuteWv.dividerColor = dividerColor
        }

    // 分割线高度
    var dividerHeight: Int = hourWv.dividerHeight
        set(value) {
            field = value
            hourWv.dividerHeight = dividerHeight
            minuteWv.dividerHeight = dividerHeight
        }


    // 分割线类型
    @WheelView.DividerType
    var dividerType: Int = hourWv.dividerType
        set(value) {
            field = value
            hourWv.dividerType = dividerType
            minuteWv.dividerType = dividerType
        }

    // 自适应分割线类型时的分割线内边距
    var dividerPaddingForWrap: Float = hourWv.dividerPaddingForWrap
        set(value) {
            field = value
            hourWv.dividerPaddingForWrap = dividerPaddingForWrap
            minuteWv.dividerPaddingForWrap = dividerPaddingForWrap
        }

    // 是否绘制选中区域
    var drawSelectedRect: Boolean = hourWv.drawSelectedRect
        set(value) {
            field = value
            hourWv.drawSelectedRect = drawSelectedRect
            minuteWv.drawSelectedRect = drawSelectedRect
        }

    // 选中区域颜色
    @ColorInt
    var selectedRectColor: Int = hourWv.selectedRectColor
        set(value) {
            field = value
            hourWv.selectedRectColor = selectedRectColor
            minuteWv.selectedRectColor = selectedRectColor
        }

    // 是否开启弯曲效果
    var curved: Boolean = hourWv.curved
        set(value) {
            field = value
            hourWv.curved = curved
            minuteWv.curved = curved
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @WheelView.CurvedArc
    var curvedArc: Int = hourWv.curvedArc
        set(value) {
            field = value
            hourWv.curvedArc = curvedArc
            minuteWv.curvedArc = curvedArc
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @FloatRange(from = 0.0, to = 1.0)
    var curvedArcFactor: Float = hourWv.curvedArcFactor
        set(value) {
            field = value
            hourWv.curvedArcFactor = curvedArcFactor
            minuteWv.curvedArcFactor = curvedArcFactor
        }

    // 选中条目折射偏移比例
    @FloatRange(from = 0.0, to = 1.0)
    var refractRatio: Float = hourWv.refractRatio
        set(value) {
            field = value
            hourWv.refractRatio = refractRatio
            minuteWv.refractRatio = refractRatio
        }

}