package me.ibore.widget.wheel

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.IntRange
import me.ibore.R
import java.util.*

class HourWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WheelView<Int>(context, attrs, defStyleAttr) {

    @IntRange(from = 0, to = 23)
    var startHour: Int = 0
        set(value) {
            field = value
            updateHour()
        }

    @IntRange(from = 0, to = 23)
    var endHour: Int = 23
        set(value) {
            field = value
            updateHour()
        }

    // 获取选中的小时
    @IntRange(from = 0, to = 23)
    var selectedHour: Int = selectedData ?: startHour
        set(value) {
            field = value
            setSelectedHour(field)
        }
        get() {
            field = selectedData ?: startHour
            return field
        }

    // 获取选中的小时
    val selectedHourString: String get() = if (selectedHour < 10) "0$selectedHour" else selectedHour.toString()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HourWheelView)
        selectedHour = typedArray.getInt(R.styleable.HourWheelView_wvSelectedHour, Calendar.getInstance()[Calendar.HOUR_OF_DAY])
        typedArray.recycle()
        updateHour ()
        integerFormat
    }

    /**
     * 初始化数据
     */
    private fun updateHour() {
        val list = ArrayList<Int>()
        for (i in startHour..endHour) {
            list.add(i)
        }
        super.datas = list
    }


    // 设置选中的小时
    fun setSelectedHour(selectedHour: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        if (selectedHour in 0..23) {
            setSelectedPosition(selectedHour - startHour, isSmoothScroll, smoothDuration)
        }
    }

    /*@Suppress("UNREACHABLE_CODE", "UNUSED_PARAMETER")
    override var datas: MutableList<Int> = ArrayList()
        set(value) {
            throw UnsupportedOperationException("You can not invoke setData method in " + this::class.java.simpleName + ".")
            field = value
        }*/

}