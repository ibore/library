package me.ibore.widget.wheel

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.IntRange
import me.ibore.R
import java.util.*

class MonthWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WheelView<Int>(context, attrs, defStyleAttr) {

    var startMonth: Int = 1
    var endMonth: Int = 12


    // 获取选中的月
    var selectedMonth: Int = Calendar.getInstance()[Calendar.MONTH] + 1
        set(value) {
            field = value
            setSelectedMonth(field)
        }
        get() {
            field = selectedData?:1
            return field
        }

    // 获取选中的月
    val selectedMonthString: String get() = if (selectedMonth < 10) "0$selectedMonth" else selectedMonth.toString()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonthWheelView)
        selectedMonth = typedArray.getInt(R.styleable.MonthWheelView_wvSelectedMonth, selectedMonth)
        typedArray.recycle()
        updateMonth()
    }

    /**
     * 初始化数据
     */
    private fun updateMonth() {
        val list = ArrayList<Int>()
        for (i in startMonth..endMonth) {
            list.add(i)
        }
        super.datas = list
    }

    fun setMonthRange(@IntRange(from = 1, to = 12) start: Int, @IntRange(from = 1, to = 12) end: Int) {
        startMonth = start
        endMonth = end
        updateMonth()
    }

    /**
     * 设置选中的月
     */
    fun setSelectedMonth(selectedMonth: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        if (selectedMonth in 1..12) {
            setSelectedPosition(selectedMonth - 1, isSmoothScroll, smoothDuration)
        }
    }


    /*@Suppress("UNREACHABLE_CODE")
    override var datas: MutableList<Int> = ArrayList()
        set(value) {
            throw UnsupportedOperationException("You can not invoke setData method in " + this::class.java.simpleName + ".")
            field = value
        }*/

}