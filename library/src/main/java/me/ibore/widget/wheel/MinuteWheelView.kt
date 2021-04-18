package me.ibore.widget.wheel

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.IntRange
import me.ibore.R
import java.util.*

class MinuteWheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WheelView<Int>(context, attrs, defStyleAttr) {

    @IntRange(from = 0, to = 59)
    var startMinute: Int = 0
        set(value) {
            field = value
            updateMinute()
        }

    @IntRange(from = 0, to = 59)
    var endMinute: Int = 59
        set(value) {
            field = value
            updateMinute()
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MinuteWheelView)
        val selectedMinute = typedArray.getInt(R.styleable.MinuteWheelView_wvSelectedMinute, Calendar.getInstance()[Calendar.MINUTE])
        typedArray.recycle()
        updateMinute()
        setSelectedMinute(selectedMinute)
    }

    // 选中的分钟
    @IntRange(from = 0, to = 59)
    var selectedMinute: Int = selectedData ?: startMinute
        set(value) {
            field = value
            setSelectedMinute(field)
        }
        get() {
            field = selectedData ?: startMinute
            return field
        }

    // 选中的分钟
    val selectedMinuteString: String get() = if (integerNeedFormat) String.format(integerFormat, selectedMinute) else selectedMinute.toString()

    /**
     * 初始化数据
     */
    private fun updateMinute() {
        val list = ArrayList<Int>()
        for (i in startMinute..endMinute) {
            list.add(i)
        }
        datas = list
    }

    /**
     * 设置选中的分钟
     */
    fun setSelectedMinute(@IntRange(from = 0, to = 59) selectedMinute: Int, isSmoothScroll: Boolean = false, smoothDuration: Int = 0) {
        if (selectedMinute in 0..59) {
            setSelectedPosition(selectedMinute - startMinute, isSmoothScroll, smoothDuration)
        }
    }

    /*@Suppress("UNREACHABLE_CODE")
    override var datas: MutableList<Int> = ArrayList()
        set(value) {
            throw UnsupportedOperationException("You can not invoke setData method in " + this::class.java.simpleName + ".")
            field = value
        }*/

}