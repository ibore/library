package me.ibore.widget.wheel

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import me.ibore.R
import java.util.*

class DayWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        WheelView<Int>(context, attrs, defStyleAttr) {

    companion object {
        private val DAYS = SparseArray<MutableList<Int>>()
    }

    private var calendar: Calendar = Calendar.getInstance()

    // 年份
    var currentYear = Calendar.getInstance().get(Calendar.YEAR)
        set(value) {
            field = value
            updateDay()
        }
    // 月份
    var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        set(value) {
            field = value
            updateDay()
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayWheelView)
        currentYear = typedArray.getInt(R.styleable.DayWheelView_wvYear, calendar.get(Calendar.YEAR))
        currentMonth = typedArray.getInt(R.styleable.DayWheelView_wvMonth, calendar.get(Calendar.MONTH) + 1)
        val selectedDay = typedArray.getInt(R.styleable.DayWheelView_wvSelectedDay, calendar.get(Calendar.DATE))
        typedArray.recycle()
        updateDay()
        setSelectedDay(selectedDay)
    }

    // 获取选中的日
    var selectedDay: Int = Calendar.getInstance()[Calendar.MONTH] + 1

    // 获取选中的日
    val selectedDayString: String get() = if (selectedDay < 10) "0$selectedDay" else selectedDay.toString()

    /**
     * 更新数据
     */
    private fun updateDay() {
        calendar[Calendar.YEAR] = currentYear
        calendar[Calendar.MONTH] = currentMonth - 1
        calendar[Calendar.DATE] = 1
        calendar.roll(Calendar.DATE, -1)
        val days = calendar[Calendar.DATE]
        var data: MutableList<Int>? = DAYS[days]
        if (data == null) {
            data = ArrayList()
            for (i in 1..days) {
                data.add(i)
            }
            DAYS.put(days, data)
        }
        super.datas = datas
    }

    /**
     * 设置选中的日
     */
    fun setSelectedDay(selectedDay: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        val days = calendar[Calendar.DATE]
        if (selectedDay in 1..days) {
            setSelectedPosition(selectedDay - 1, isSmoothScroll, smoothDuration)
        }
    }

    /*@Suppress("UNREACHABLE_CODE")
    override var datas: MutableList<Int> = ArrayList()
        set(value) {
            throw UnsupportedOperationException("You can not invoke setData method in " + YearWheelView::class.java.simpleName + ".")
            field = value
        }*/

    fun setDayRange(start: Int, end: Int) {
        val list = ArrayList<Int>(1)
        for (i in start..end) {
            list.add(i)
        }
        super.datas = list
    }
}