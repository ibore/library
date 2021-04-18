package me.ibore.widget.wheel

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import me.ibore.R
import java.util.*
import kotlin.collections.ArrayList

class YearWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        WheelView<Int>(context, attrs, defStyleAttr) {

    var startYear = 1900
        set(value) {
            field = value
            updateYear()
        }
    var endYear = 2100
        set(value) {
            field = value
            updateYear()
        }

    // 当前选中的年份
    var selectedYear: Int = Calendar.getInstance()[Calendar.YEAR]
        set(value) {
            field = value
            setSelectedYear(field)
        }
        get() {
            field = selectedData?:1
            return field
        }

    // 当前选中的年份
    val selectedYearString: String get() = if (selectedYear < 10) "0$selectedYear" else selectedYear.toString()

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.YearWheelView)
        startYear = typedArray.getInt(R.styleable.YearWheelView_wvStartYear, startYear)
        endYear = typedArray.getInt(R.styleable.YearWheelView_wvEndYear, endYear)
        selectedYear = typedArray.getInt(R.styleable.YearWheelView_wvSelectedYear, selectedYear)
        typedArray.recycle()
        updateYear()
    }

    /**
     * 设置年份区间
     * @param start 起始年份
     * @param end 结束年份
     */
    fun setYearRange(start: Int, end: Int) {
        startYear = start
        endYear = end
        updateYear()
    }

    /**
     * 更新年份数据
     */
    private fun updateYear() {
        val list: MutableList<Int> = ArrayList()
        for (i in startYear..endYear) {
            list.add(i)
        }
        super.datas = list
    }

    /**
     * 设置当前选中的年份
     */
    fun setSelectedYear(selectedYear: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        if (selectedYear in startYear..endYear) {
            setSelectedPosition(selectedYear - startYear, isSmoothScroll, smoothDuration)
        }
    }

    /*@Suppress("UNREACHABLE_CODE")
    override var datas: MutableList<Int> = ArrayList()
        set(value) {
            throw UnsupportedOperationException("You can not invoke setData method in " + YearWheelView::class.java.simpleName + ".")
            field = value
        }*/

}