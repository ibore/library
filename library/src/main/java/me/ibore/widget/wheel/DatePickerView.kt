package me.ibore.widget.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatTextView
import me.ibore.R
import me.ibore.ktx.color
import me.ibore.ktx.dp2px
import me.ibore.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

class DatePickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val YYYY = "yyyy"
        const val YYYY_MM = "yyyy-MM"
        const val YYYY_MM_DD = "yyyy-MM-dd"
    }

    @StringDef(YYYY, YYYY_MM, YYYY_MM_DD)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class DateFormat

    private var startYear: Int = 1900
    private var startMonth: Int = 1
    private var startDay: Int = 1

    private var endYear: Int = 2099
    private var endMonth: Int = 12
    private var endDay: Int = 31

    lateinit var yearWv: YearWheelView
        private set

    lateinit var monthWv: MonthWheelView
        private set

    lateinit var dayWv: DayWheelView
        private set

    lateinit var yearTv: AppCompatTextView
        private set

    lateinit var monthTv: AppCompatTextView
        private set

    lateinit var dayTv: AppCompatTextView
        private set

    private val onSelectedListener: ((wheelView: WheelView<Int>, data: Int, position: Int) -> Unit) = { wheelView, data, position ->
        if (wheelView is YearWheelView) { // 年份选中
            if (monthWv.visibility == View.VISIBLE) {
                dayWv.currentYear = data
                if (yearWv.selectedYear == startYear) {
                    monthWv.setMonthRange(startMonth, 12)
                    if (monthWv.selectedMonth == startMonth) {
                        val calendar = Calendar.getInstance()
                        calendar[Calendar.YEAR] = yearWv.selectedYear
                        calendar[Calendar.MONTH] = monthWv.selectedMonth - 1
                        calendar[Calendar.DATE] = 1
                        calendar.roll(Calendar.DATE, -1)
                        dayWv.setDayRange(startDay, calendar[Calendar.DATE])
                    }
                } else if (yearWv.selectedYear == endYear) {
                    monthWv.setMonthRange(1, endMonth)
                    if (monthWv.selectedMonth == endMonth) {
                        dayWv.setDayRange(1, endDay)
                    }
                } else {
                    monthWv.setMonthRange(1, 12)
                }
            }
        } else if (wheelView is MonthWheelView) { // 月份选中
            if (dayWv.visibility == View.VISIBLE) {
                if (yearWv.selectedYear == startYear && monthWv.selectedMonth == startMonth) {
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.YEAR] = yearWv.selectedYear
                    calendar[Calendar.MONTH] = monthWv.selectedMonth - 1
                    calendar[Calendar.DATE] = 1
                    calendar.roll(Calendar.DATE, -1)
                    dayWv.setDayRange(startDay, calendar[Calendar.DATE])
                } else if (yearWv.selectedYear == endYear && monthWv.selectedMonth == endMonth) {
                    dayWv.setDayRange(1, endDay)
                } else {
                    dayWv.currentMonth = data
                }
            }
        }
        when {
            dayWv.visibility == View.VISIBLE -> {
                onDateSelectedListener?.invoke(this, yearWv.selectedYear, monthWv.selectedMonth, dayWv.selectedDay, selectedDate)
            }
            monthWv.visibility == View.VISIBLE -> {
                onDateSelectedListener?.invoke(this, yearWv.selectedYear, monthWv.selectedMonth, 0, selectedDate)
            }
            else -> {
                onDateSelectedListener?.invoke(this, yearWv.selectedYear, 0, 0, selectedDate)
            }
        }
    }

    var onDateSelectedListener: ((datePickerView: DatePickerView, year: Int, month: Int, day: Int, date: String) -> Unit)? = null
        set(value) {
            field = value
            yearWv.onSelectedListener = onSelectedListener
            monthWv.onSelectedListener = onSelectedListener
            dayWv.onSelectedListener = onSelectedListener
        }

    init {
        orientation = HORIZONTAL
        setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
        addView(createYearWheelView(context, attrs, defStyleAttr))
        addView(createYearLabelView())
        addView(createMonthWheelView(context, attrs, defStyleAttr))
        addView(createMonthLabelView())
        addView(createDayWheelView(context, attrs, defStyleAttr))
        addView(createDayLabelView())
    }

    // 创建年WheelView
    private fun createYearWheelView(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int): YearWheelView {
        yearWv = YearWheelView(context, attrs, defStyleAttr)
        val params = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        params.weight = 1F
        yearWv.apply {
            this.layoutParams = params
            this.autoFitTextSize = true
            this.textSize = dp2px(24F)
            this.lineSpacing = dp2px(8F)
            this.textBoundaryMargin = dp2px(0F)
        }
        return yearWv
    }

    // 创建月WheelView
    private fun createMonthWheelView(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int): MonthWheelView {
        monthWv = MonthWheelView(context, attrs, defStyleAttr)
        val params = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        params.weight = 1F
        monthWv.apply {
            this.layoutParams = params
            this.autoFitTextSize = true
            this.textSize = dp2px(24F)
            this.lineSpacing = dp2px(8F)
            this.textBoundaryMargin = dp2px(0F)
        }
        return monthWv
    }

    // 创建日WheelView
    private fun createDayWheelView(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int): DayWheelView {
        dayWv = DayWheelView(context, attrs, defStyleAttr)
        val params = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        params.weight = 1F
        dayWv.apply {
            this.layoutParams = params
            this.autoFitTextSize = true
            this.textSize = dp2px(24F)
            this.lineSpacing = dp2px(8F)
            this.textBoundaryMargin = dp2px(0F)
        }
        return dayWv
    }

    // 创建年label View
    private fun createYearLabelView(): View {
        yearTv = AppCompatTextView(context)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        yearTv.layoutParams = params
        yearTv.setPadding(10, 0, 30, 0)
        yearTv.text = context.getString(R.string.picker_view_year)
        yearTv.textSize = 20F
        yearTv.setTextColor(Color.BLACK)
        return yearTv
    }

    // 创建月label View
    private fun createMonthLabelView(): View {
        monthTv = AppCompatTextView(context)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        monthTv.layoutParams = params
        monthTv.setPadding(10, 0, 30, 0)
        monthTv.text = context.getString(R.string.picker_view_month)
        monthTv.setTextColor(Color.BLACK)
        monthTv.textSize = 20F
        return monthTv
    }

    // 创建日label View
    private fun createDayLabelView(): View {
        dayTv = AppCompatTextView(context)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL
        dayTv.layoutParams = params
        dayTv.setPadding(10, 0, 30, 0)
        dayTv.text = context.getString(R.string.picker_view_day)
        dayTv.setTextColor(Color.BLACK)
        dayTv.textSize = 20F
        return dayTv
    }

    /**
     * 设置标签字体大小
     *
     * @param textSize 字体大小
     */
    fun setLabelTextSize(textSize: Float) {
        yearTv.textSize = textSize
        monthTv.textSize = textSize
        dayTv.textSize = textSize
    }

    /**
     * 设置标签字体大小
     *
     * @param unit     单位
     * @param textSize 字体大小
     */
    fun setLabelTextSize(unit: Int, textSize: Float) {
        yearTv.setTextSize(unit, textSize)
        monthTv.setTextSize(unit, textSize)
        dayTv.setTextSize(unit, textSize)
    }

    /**
     * 设置标签字体颜色
     *
     * @param textColorRes 颜色资源
     */
    fun setLabelTextColorRes(@ColorRes textColorRes: Int) {
        setLabelTextColor(color(textColorRes))
    }

    /**
     * 设置标签字体颜色
     *
     * @param textColor 颜色值
     */
    fun setLabelTextColor(@ColorInt textColor: Int) {
        yearTv.setTextColor(textColor)
        monthTv.setTextColor(textColor)
        dayTv.setTextColor(textColor)
    }

    /**
     * 设置是否显示标签
     *
     * @param isShowLabel 是否显示标签
     */
    fun setShowLabel(isShowLabel: Boolean) {
        if (isShowLabel) {
            setLabelVisibility(View.VISIBLE)
        } else {
            setLabelVisibility(View.GONE)
        }
    }

    /**
     * 统一设置标签可见性
     *
     * @param visibility 可见性值
     */
    private fun setLabelVisibility(visibility: Int) {
        yearTv.visibility = visibility
        monthTv.visibility = visibility
        dayTv.visibility = visibility
    }

    /**
     * 隐藏 日
     */
    fun hideDayItem() {
        setItemVisibility(View.GONE, dayWv, dayTv)
    }

    /**
     * 显示 日
     */
    fun showDayItem() {
        setItemVisibility(View.VISIBLE, dayWv, dayTv)
    }

    /**
     * 隐藏 月
     */
    fun hideMonthItem() {
        setItemVisibility(View.GONE, monthWv, monthTv)
    }

    /**
     * 显示 月
     */
    fun showMonthItem() {
        setItemVisibility(View.VISIBLE, monthWv, monthTv)
    }

    /**
     * 隐藏 年
     */
    fun hideYearItem() {
        setItemVisibility(View.GONE, yearWv, yearTv)
    }

    /**
     * 显示 年
     */
    fun showYearItem() {
        setItemVisibility(View.VISIBLE, yearWv, yearTv)
    }

    /**
     * 设置 item可见性
     *
     * @param visibility 可见性值
     * @param wheelView  WheelView
     * @param textView   labelView
     */
    private fun setItemVisibility(visibility: Int, wheelView: WheelView<*>?, textView: AppCompatTextView?) {
        wheelView?.visibility = visibility
        if (textView != null) {
            textView.visibility = visibility
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setDate(selectDate: String?, startDate: String?, endDate: String?, @DateFormat dateFormat: String) {
        val selects = (if (isValidDate(
                selectDate,
                dateFormat
            )
        ) selectDate else TimeUtils.nowString(TimeUtils.getSafeDateFormat("yyyy-MM-dd")))!!.split("-")
        val starts = (if (isValidDate(startDate, dateFormat)) startDate else {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 100)
            TimeUtils.getString(calendar.time, 0, 0, TimeUtils.getSafeDateFormat("yyyy-MM-dd"))
        })!!.split("-")
        val ends = (if (isValidDate(endDate, dateFormat)) endDate else {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 100)
            TimeUtils.getString(calendar.time, 0, 0, TimeUtils.getSafeDateFormat("yyyy-MM-dd"))
        })!!.split("-")
        when (dateFormat) {
            YYYY -> {
                setStartDate(starts[0].toInt(), 0, 0)
                setEndDate(ends[0].toInt(), 0, 0)
                hideMonthItem()
                hideDayItem()
                setSelectedYear(selects[0].toInt())
            }
            YYYY_MM -> {
                setStartDate(starts[0].toInt(), starts[1].toInt(), 0)
                setEndDate(ends[0].toInt(), ends[1].toInt(), 0)
                hideDayItem()
                setSelectedYear(selects[0].toInt())
                setSelectedMonth(selects[1].toInt())
            }
            YYYY_MM_DD -> try {
                setStartDate(starts[0].toInt(), starts[1].toInt(), starts[2].toInt())
                setEndDate(ends[0].toInt(), ends[1].toInt(), ends[2].toInt())
                setSelectedYear(selects[0].toInt())
                setSelectedMonth(selects[1].toInt())
                setSelectedDay(selects[2].toInt())
            } catch (ignored: Exception) {
            }
        }
    }


    /**
     * 判断时间格式 格式必须为“YYYY-MM-dd”
     * 2004-2-30 是无效的
     * 2003-2-29 是无效的
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private fun isValidDate(str: String?, dateFormat: String): Boolean {
        val formatter = SimpleDateFormat(dateFormat)
        formatter.isLenient = false
        return try {
            val date = formatter.parse(str ?: return false) ?: return false
            str == formatter.format(date)
        } catch (e: Exception) {
            false
        }
    }

    fun setStartDate(startYear: Int, startMonth: Int, startDay: Int) {
        this.startYear = startYear
        this.startMonth = startMonth
        this.startDay = startDay
        yearWv.setYearRange(this.startYear, endYear)
    }

    fun setEndDate(endYear: Int, endMonth: Int, endDay: Int) {
        this.endYear = endYear
        this.endMonth = endMonth
        this.endDay = endDay
        yearWv.setYearRange(startYear, this.endYear)
    }

    // 选中的年份
    var selectedYear: Int = yearWv.selectedYear
        set(value) {
            field = value
            yearWv.selectedYear = selectedYear
        }

    // 获取选中的年份
    val selectedYearString: String get() = yearWv.selectedYearString

    /**
     * 设置选中的年份
     */
    fun setSelectedYear(year: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        yearWv.setSelectedYear(year, isSmoothScroll, smoothDuration)
    }

    // 选中的月份
    var selectedMonth: Int = monthWv.selectedMonth
        set(value) {
            field = value
            monthWv.selectedMonth = selectedMonth
        }

    // 获取选中的月份
    val selectedMonthString: String get() = monthWv.selectedMonthString

    /**
     * 设置选中的月份
     */
    fun setSelectedMonth(month: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        monthWv.setSelectedMonth(month, isSmoothScroll, smoothDuration)
    }


    // 获取选中的日
    var selectedDay: Int = dayWv.selectedDay
        set(value) {
            field = value
            dayWv.selectedDay = selectedDay
        }

    // 获取选中的日
    val selectedDayString: String get() = dayWv.selectedDayString

    /**
     * 设置选中的日
     */
    fun setSelectedDay(day: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        dayWv.setSelectedDay(day, isSmoothScroll, smoothDuration)
    }

    // 获取选中的日期 格式 2018-08-22
    val selectedDate: String
        get() = when {
            dayWv.visibility == View.VISIBLE -> "$selectedYearString-$selectedMonthString-$selectedDayString"
            monthWv.visibility == View.VISIBLE -> "$selectedYearString-$selectedMonthString"
            else -> selectedYearString
        }

    // 可见item数
    var visibleItems: Int = yearWv.visibleItems
        set(value) {
            yearWv.visibleItems = value
            monthWv.visibleItems = value
            dayWv.visibleItems = value
            field = yearWv.visibleItems
        }

    // 是否重置选中下标到第一个
    var resetSelectedPosition: Boolean = yearWv.resetSelectedPosition
        set(value) {
            field = value
            yearWv.resetSelectedPosition = resetSelectedPosition
            monthWv.resetSelectedPosition = resetSelectedPosition
            dayWv.resetSelectedPosition = resetSelectedPosition
        }

    // 是否自动调整字体大小，以显示完全
    var autoFitTextSize: Boolean = yearWv.autoFitTextSize
        set(value) {
            field = value
            yearWv.autoFitTextSize = autoFitTextSize
            monthWv.autoFitTextSize = autoFitTextSize
            dayWv.autoFitTextSize = autoFitTextSize
        }

    // 是否自动调整字体大小，以显示完全
    var textSize: Int = yearWv.textSize
        set(value) {
            field = value
            yearWv.textSize = textSize
            monthWv.textSize = textSize
            dayWv.textSize = textSize
        }

    // 字体
    var typeface: Typeface = yearWv.typeface
        set(value) {
            field = value
            yearWv.typeface = typeface
            monthWv.typeface = typeface
            dayWv.typeface = typeface
        }

    // 文字距离边界的外边距
    var textBoundaryMargin: Int = yearWv.textBoundaryMargin
        set(value) {
            field = value
            yearWv.textBoundaryMargin = textBoundaryMargin
            monthWv.textBoundaryMargin = textBoundaryMargin
            dayWv.textBoundaryMargin = textBoundaryMargin
        }

    // 未选中item文字颜色
    @ColorInt
    var normalTextColor: Int = yearWv.normalTextColor
        set(value) {
            field = value
            yearWv.normalTextColor = normalTextColor
            monthWv.normalTextColor = normalTextColor
            dayWv.normalTextColor = normalTextColor
        }

    // 选中item文字颜色
    @ColorInt
    var selectedTextColor: Int = yearWv.selectedTextColor
        set(value) {
            field = value
            yearWv.selectedTextColor = selectedTextColor
            monthWv.selectedTextColor = selectedTextColor
            dayWv.selectedTextColor = selectedTextColor
        }

    // 是否循环滚动
    var cyclic: Boolean = yearWv.cyclic
        set(value) {
            field = value
            yearWv.cyclic = cyclic
            monthWv.cyclic = cyclic
            dayWv.cyclic = cyclic
        }

    // 行间距
    var lineSpacing: Int = yearWv.lineSpacing
        set(value) {
            field = value
            yearWv.lineSpacing = lineSpacing
            monthWv.lineSpacing = lineSpacing
            dayWv.lineSpacing = lineSpacing
        }

    // 滚动音效
    var soundEffect: Boolean = yearWv.soundEffect
        set(value) {
            field = value
            yearWv.soundEffect = soundEffect
            monthWv.soundEffect = soundEffect
            dayWv.soundEffect = soundEffect
        }

    // 滚动音效资源
    @RawRes
    var soundEffectResource: Int = yearWv.soundEffectResource
        set(value) {
            field = value
            yearWv.soundEffectResource = soundEffectResource
            monthWv.soundEffectResource = soundEffectResource
            dayWv.soundEffectResource = soundEffectResource
        }

    // 滚动音效播放音量
    @FloatRange(from = 0.0, to = 1.0)
    var playVolume: Float = yearWv.playVolume
        set(value) {
            field = value
            yearWv.playVolume = playVolume
            monthWv.playVolume = playVolume
            dayWv.playVolume = playVolume
        }

    // 是否显示分割线
    var showDivider: Boolean = yearWv.showDivider
        set(value) {
            field = value
            yearWv.showDivider = showDivider
            monthWv.showDivider = showDivider
            dayWv.showDivider = showDivider
        }

    // 分割线颜色
    @ColorInt
    var dividerColor: Int = yearWv.dividerColor
        set(value) {
            field = value
            yearWv.dividerColor = dividerColor
            monthWv.dividerColor = dividerColor
            dayWv.dividerColor = dividerColor
        }

    // 分割线高度
    var dividerHeight: Int = yearWv.dividerHeight
        set(value) {
            field = value
            yearWv.dividerHeight = dividerHeight
            monthWv.dividerHeight = dividerHeight
            dayWv.dividerHeight = dividerHeight
        }


    // 分割线类型
    @WheelView.DividerType
    var dividerType: Int = yearWv.dividerType
        set(value) {
            field = value
            yearWv.dividerType = dividerType
            monthWv.dividerType = dividerType
            dayWv.dividerType = dividerType
        }

    // 自适应分割线类型时的分割线内边距
    var dividerPaddingForWrap: Float = yearWv.dividerPaddingForWrap
        set(value) {
            field = value
            yearWv.dividerPaddingForWrap = dividerPaddingForWrap
            monthWv.dividerPaddingForWrap = dividerPaddingForWrap
            dayWv.dividerPaddingForWrap = dividerPaddingForWrap
        }

    // 是否绘制选中区域
    var drawSelectedRect: Boolean = yearWv.drawSelectedRect
        set(value) {
            field = value
            yearWv.drawSelectedRect = drawSelectedRect
            monthWv.drawSelectedRect = drawSelectedRect
            dayWv.drawSelectedRect = drawSelectedRect
        }

    // 选中区域颜色
    @ColorInt
    var selectedRectColor: Int = yearWv.selectedRectColor
        set(value) {
            field = value
            yearWv.selectedRectColor = selectedRectColor
            monthWv.selectedRectColor = selectedRectColor
            dayWv.selectedRectColor = selectedRectColor
        }

    // 是否开启弯曲效果
    var curved: Boolean = yearWv.curved
        set(value) {
            field = value
            yearWv.curved = curved
            monthWv.curved = curved
            dayWv.curved = curved
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @WheelView.CurvedArc
    var curvedArc: Int = yearWv.curvedArc
        set(value) {
            field = value
            yearWv.curvedArc = curvedArc
            monthWv.curvedArc = curvedArc
            dayWv.curvedArc = curvedArc
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @FloatRange(from = 0.0, to = 1.0)
    var curvedArcFactor: Float = yearWv.curvedArcFactor
        set(value) {
            field = value
            yearWv.curvedArcFactor = curvedArcFactor
            monthWv.curvedArcFactor = curvedArcFactor
            dayWv.curvedArcFactor = curvedArcFactor
        }

    // 选中条目折射偏移比例
    @FloatRange(from = 0.0, to = 1.0)
    var refractRatio: Float = yearWv.refractRatio
        set(value) {
            field = value
            yearWv.refractRatio = refractRatio
            monthWv.refractRatio = refractRatio
            dayWv.refractRatio = refractRatio
        }
}