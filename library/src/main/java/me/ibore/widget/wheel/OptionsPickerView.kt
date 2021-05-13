package me.ibore.widget.wheel

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.RawRes

class OptionsPickerView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), WheelView.OnWheelChangedListener {

    //WheelView
    val optionsWv1: WheelView<T> = WheelView(context)
    val optionsWv2: WheelView<T> = WheelView(context)
    val optionsWv3: WheelView<T> = WheelView(context)

    //联动数据
    private var mOptionsData1: MutableList<T>? = null
    private var mOptionsData2: MutableList<MutableList<T>>? = null
    private var mOptionsData3: MutableList<MutableList<MutableList<T>>>? = null

    //是否联动
    private var isLinkage: Boolean = false
    private var isResetSelectedPosition: Boolean = false

    private var onOptionsSelectedListener: ((opt1Pos: Int, opt1Data: T?, opt2Pos: Int, opt2Data: T?, opt3Pos: Int, opt3Data: T?) -> Unit)? = null
    private var onPickerScrollStateChangedListener: OnPickerScrollStateChangedListener? = null

    private var onSelectedListener = { wheelView: WheelView<T>, data: T, position: Int ->
        if (isLinkage) {
            //联动
            if (wheelView.id == optionsWv1.id) {
                //第一个
                optionsWv2.datas = mOptionsData2!![position]
                if (mOptionsData3 != null) {
                    optionsWv3.datas = mOptionsData3!![position][optionsWv2.selectedPosition]
                }
            } else if (wheelView.id == optionsWv2.id) {
                //第二个
                if (mOptionsData3 != null) {
                    optionsWv3.datas = mOptionsData3!![optionsWv1.selectedPosition][position]
                }
            }
            if (onOptionsSelectedListener != null) {
                val opt1Pos = optionsWv1.selectedPosition
                val opt2Pos = optionsWv2.selectedPosition
                val opt3Pos = if (mOptionsData3 == null) -1 else optionsWv3.selectedPosition
                val opt1Data = mOptionsData1!![opt1Pos]
                val opt2Data = mOptionsData2!![opt1Pos][opt2Pos]
                var opt3Data: T? = null
                if (mOptionsData3 != null) {
                    opt3Data = mOptionsData3!![opt1Pos][opt2Pos][opt3Pos]
                }
                onOptionsSelectedListener?.invoke(opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data)
            }

        } else {
            //不联动
            if (onOptionsSelectedListener != null) {
                val isOpt1Shown = optionsWv1.visibility == View.VISIBLE
                val opt1Pos = if (isOpt1Shown) optionsWv1.selectedPosition else -1
                val isOpt2Shown = optionsWv2.visibility == View.VISIBLE
                val opt2Pos = if (isOpt2Shown) optionsWv2.selectedPosition else -1
                val isOpt3Shown = optionsWv3.visibility == View.VISIBLE
                val opt3Pos = if (isOpt3Shown) optionsWv3.selectedPosition else -1
                val opt1Data = if (isOpt1Shown) optionsWv1.selectedData else null
                val opt2Data = if (isOpt2Shown) optionsWv2.selectedData else null
                val opt3Data = if (isOpt3Shown) optionsWv3.selectedData else null
                onOptionsSelectedListener?.invoke(opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data)
            }
        }
    }

    init {
        orientation = HORIZONTAL
        optionsWv1.id = generateViewId()
        optionsWv2.id = generateViewId()
        optionsWv3.id = generateViewId()

        val layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 1f
        addView(optionsWv1, layoutParams)
        addView(optionsWv2, layoutParams)
        addView(optionsWv3, layoutParams)

        optionsWv1.onSelectedListener  = onSelectedListener
        optionsWv2.onSelectedListener  = onSelectedListener
        optionsWv3.onSelectedListener  = onSelectedListener

        optionsWv1.onWheelChangedListener = this
        optionsWv2.onWheelChangedListener = this
        optionsWv3.onWheelChangedListener = this
    }


    /**
     * 设置不联动数据
     *
     * @param data 数据
     */
    fun setData(data: MutableList<T>) {
        setData(data, null, null)
    }

    /**
     * 设置不联动数据
     *
     * @param data1 数据1
     * @param data2 数据2
     */
    fun setData(data1: MutableList<T>, data2: MutableList<T>) {
        setData(data1, data2, null)
    }

    /**
     * 设置不联动数据
     *
     * @param data1 数据1
     * @param data2 数据2
     * @param data3 数据3
     */
    fun setData(data1: MutableList<T>, data2: MutableList<T>?, data3: MutableList<T>?) {
        isLinkage = false
        setDataOrGone(data1, optionsWv1)
        setDataOrGone(data2, optionsWv2)
        setDataOrGone(data3, optionsWv3)
    }

    /**
     * 设置数据，如果数据为null隐藏对应的WheelView
     *
     * @param data      数据
     * @param wheelView WheelView
     */
    private fun setDataOrGone(data: MutableList<T>?, wheelView: WheelView<T>) {
        if (data != null) {
            wheelView.datas = data
        } else {
            wheelView.setVisibility(View.GONE)
        }
    }

    /**
     * 设置联动数据
     *
     * @param linkageData1 联动数据1
     * @param linkageData2 联动数据2
     */
    fun setLinkageData(linkageData1: MutableList<T>?, linkageData2: MutableList<MutableList<T>>?) {
        setLinkageData(linkageData1, linkageData2, null)
    }

    /**
     * 设置联动数据
     *
     * @param linkageData1 联动数据1
     * @param linkageData2 联动数据2
     * @param linkageData3 联动数据3
     */
    fun setLinkageData(linkageData1: MutableList<T>?, linkageData2: MutableList<MutableList<T>>?, linkageData3: MutableList<MutableList<MutableList<T>>>?) {
        if (linkageData1 == null || linkageData1.size == 0 || linkageData2 == null || linkageData2.size == 0) {
            return
        }
        //数据限制，联动需保持 最外层List size一致，及linkageData1.size()==linkageData2.size()==linkageData3.size()
        //理论上第二层 linkageData2每一项的size及get(i).size()都要和linkageData3.get(i).size()一致
        require(linkageData1.size == linkageData2.size) { "linkageData1 and linkageData3 are not the same size." }
        isLinkage = true
        mOptionsData1 = linkageData1
        mOptionsData2 = linkageData2
        if (linkageData3 == null) {
            mOptionsData3 = null
            optionsWv3.visibility = View.GONE
            //两级联动
            optionsWv1.datas = linkageData1
            optionsWv2.datas = linkageData2[0]
        } else {
            optionsWv3.visibility = View.VISIBLE
            require(linkageData1.size == linkageData3.size) { "linkageData1 and linkageData3 are not the same size." }

            for (i in linkageData1.indices) {
                require(linkageData2[i].size == linkageData3[i].size) {
                    ("linkageData2 index " + i + " List and linkageData3 index "
                            + i + " List are not the same size.")
                }
            }

            mOptionsData3 = linkageData3
            //三级联动
            optionsWv1.datas = linkageData1
            optionsWv2.datas = linkageData2[0]
            optionsWv3.datas = linkageData3[0][0]
            if (isResetSelectedPosition) {
                optionsWv1.setSelectedPosition(0)
                optionsWv2.setSelectedPosition(0)
                optionsWv3.setSelectedPosition(0)
            }
        }

    }

    // 可见item数
    var visibleItems: Int = optionsWv1.visibleItems
        set(value) {
            optionsWv1.visibleItems = value
            optionsWv2.visibleItems = value
            optionsWv3.visibleItems = value
            field = optionsWv1.visibleItems
        }

    // 是否重置选中下标到第一个
    var resetSelectedPosition: Boolean = optionsWv1.resetSelectedPosition
        set(value) {
            field = value
            optionsWv1.resetSelectedPosition = resetSelectedPosition
            optionsWv2.resetSelectedPosition = resetSelectedPosition
            optionsWv3.resetSelectedPosition = resetSelectedPosition
        }

    // 是否自动调整字体大小，以显示完全
    var autoFitTextSize: Boolean = optionsWv1.autoFitTextSize
        set(value) {
            field = value
            optionsWv1.autoFitTextSize = autoFitTextSize
            optionsWv2.autoFitTextSize = autoFitTextSize
            optionsWv3.autoFitTextSize = autoFitTextSize
        }

    // 是否自动调整字体大小，以显示完全
    var textSize: Int = optionsWv1.textSize
        set(value) {
            field = value
            optionsWv1.textSize = textSize
            optionsWv2.textSize = textSize
            optionsWv3.textSize = textSize
        }

    // 字体
    var typeface: Typeface = optionsWv1.typeface
        set(value) {
            field = value
            optionsWv1.typeface = typeface
            optionsWv2.typeface = typeface
            optionsWv3.typeface = typeface
        }

    // 文字距离边界的外边距
    var textBoundaryMargin: Int = optionsWv1.textBoundaryMargin
        set(value) {
            field = value
            optionsWv1.textBoundaryMargin = textBoundaryMargin
            optionsWv2.textBoundaryMargin = textBoundaryMargin
            optionsWv3.textBoundaryMargin = textBoundaryMargin
        }

    // 未选中item文字颜色
    @ColorInt
    var normalTextColor: Int = optionsWv1.normalTextColor
        set(value) {
            field = value
            optionsWv1.normalTextColor = normalTextColor
            optionsWv2.normalTextColor = normalTextColor
            optionsWv3.normalTextColor = normalTextColor
        }

    // 选中item文字颜色
    @ColorInt
    var selectedTextColor: Int = optionsWv1.selectedTextColor
        set(value) {
            field = value
            optionsWv1.selectedTextColor = selectedTextColor
            optionsWv2.selectedTextColor = selectedTextColor
            optionsWv3.selectedTextColor = selectedTextColor
        }

    // 是否循环滚动
    var cyclic: Boolean = optionsWv1.cyclic
        set(value) {
            field = value
            optionsWv1.cyclic = cyclic
            optionsWv2.cyclic = cyclic
            optionsWv3.cyclic = cyclic
        }

    // 行间距
    var lineSpacing: Int = optionsWv1.lineSpacing
        set(value) {
            field = value
            optionsWv1.lineSpacing = lineSpacing
            optionsWv2.lineSpacing = lineSpacing
            optionsWv3.lineSpacing = lineSpacing
        }

    // 滚动音效
    var soundEffect: Boolean = optionsWv1.soundEffect
        set(value) {
            field = value
            optionsWv1.soundEffect = soundEffect
            optionsWv2.soundEffect = soundEffect
            optionsWv3.soundEffect = soundEffect
        }

    // 滚动音效资源
    @RawRes
    var soundEffectResource: Int = optionsWv1.soundEffectResource
        set(value) {
            field = value
            optionsWv1.soundEffectResource = soundEffectResource
            optionsWv2.soundEffectResource = soundEffectResource
            optionsWv3.soundEffectResource = soundEffectResource
        }

    // 滚动音效播放音量
    @FloatRange(from = 0.0, to = 1.0)
    var playVolume: Float = optionsWv1.playVolume
        set(value) {
            field = value
            optionsWv1.playVolume = playVolume
            optionsWv2.playVolume = playVolume
            optionsWv3.playVolume = playVolume
        }

    // 是否显示分割线
    var showDivider: Boolean = optionsWv1.showDivider
        set(value) {
            field = value
            optionsWv1.showDivider = showDivider
            optionsWv2.showDivider = showDivider
            optionsWv3.showDivider = showDivider
        }

    // 分割线颜色
    @ColorInt
    var dividerColor: Int = optionsWv1.dividerColor
        set(value) {
            field = value
            optionsWv1.dividerColor = dividerColor
            optionsWv2.dividerColor = dividerColor
            optionsWv3.dividerColor = dividerColor
        }

    // 分割线高度
    var dividerHeight: Int = optionsWv1.dividerHeight
        set(value) {
            field = value
            optionsWv1.dividerHeight = dividerHeight
            optionsWv2.dividerHeight = dividerHeight
            optionsWv3.dividerHeight = dividerHeight
        }


    // 分割线类型
    @WheelView.DividerType
    var dividerType: Int = optionsWv1.dividerType
        set(value) {
            field = value
            optionsWv1.dividerType = dividerType
            optionsWv2.dividerType = dividerType
            optionsWv3.dividerType = dividerType
        }

    // 自适应分割线类型时的分割线内边距
    var dividerPaddingForWrap: Float = optionsWv1.dividerPaddingForWrap
        set(value) {
            field = value
            optionsWv1.dividerPaddingForWrap = dividerPaddingForWrap
            optionsWv2.dividerPaddingForWrap = dividerPaddingForWrap
            optionsWv3.dividerPaddingForWrap = dividerPaddingForWrap
        }

    // 是否绘制选中区域
    var drawSelectedRect: Boolean = optionsWv1.drawSelectedRect
        set(value) {
            field = value
            optionsWv1.drawSelectedRect = drawSelectedRect
            optionsWv2.drawSelectedRect = drawSelectedRect
            optionsWv3.drawSelectedRect = drawSelectedRect
        }

    // 选中区域颜色
    @ColorInt
    var selectedRectColor: Int = optionsWv1.selectedRectColor
        set(value) {
            field = value
            optionsWv1.selectedRectColor = selectedRectColor
            optionsWv2.selectedRectColor = selectedRectColor
            optionsWv3.selectedRectColor = selectedRectColor
        }

    // 是否开启弯曲效果
    var curved: Boolean = optionsWv1.curved
        set(value) {
            field = value
            optionsWv1.curved = curved
            optionsWv2.curved = curved
            optionsWv3.curved = curved
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @WheelView.CurvedArc
    var curvedArc: Int = optionsWv1.curvedArc
        set(value) {
            field = value
            optionsWv1.curvedArc = curvedArc
            optionsWv2.curvedArc = curvedArc
            optionsWv3.curvedArc = curvedArc
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @FloatRange(from = 0.0, to = 1.0)
    var curvedArcFactor: Float = optionsWv1.curvedArcFactor
        set(value) {
            field = value
            optionsWv1.curvedArcFactor = curvedArcFactor
            optionsWv2.curvedArcFactor = curvedArcFactor
            optionsWv3.curvedArcFactor = curvedArcFactor
        }

    // 选中条目折射偏移比例
    @FloatRange(from = 0.0, to = 1.0)
    var refractRatio: Float = optionsWv1.refractRatio
        set(value) {
            field = value
            optionsWv1.refractRatio = refractRatio
            optionsWv2.refractRatio = refractRatio
            optionsWv3.refractRatio = refractRatio
        }

    /**
     * 获取选项1WheelView 选中下标
     *
     * @return 选中下标
     */
    fun getOpt1SelectedPosition(): Int {
        return optionsWv1.selectedPosition
    }

    /**
     * 设置选项1WheelView 选中下标
     *
     * @param position 选中下标
     */
    fun setOpt1SelectedPosition(position: Int) {
        setOpt1SelectedPosition(position, false)
    }

    /**
     * 设置选项1WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     */
    fun setOpt1SelectedPosition(position: Int, isSmoothScroll: Boolean) {
        setOpt1SelectedPosition(position, isSmoothScroll, 0)
    }

    /**
     * 设置选项1WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    fun setOpt1SelectedPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        optionsWv1.setSelectedPosition(position, isSmoothScroll, smoothDuration)
    }

    /**
     * 获取选项2WheelView 选中下标
     *
     * @return 选中下标
     */
    fun getOpt2SelectedPosition(): Int {
        return optionsWv2.selectedPosition
    }

    /**
     * 设置选项2WheelView 选中下标
     *
     * @param position 选中下标
     */
    fun setOpt2SelectedPosition(position: Int) {
        setOpt2SelectedPosition(position, false)
    }

    /**
     * 设置选项2WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     */
    fun setOpt2SelectedPosition(position: Int, isSmoothScroll: Boolean) {
        setOpt2SelectedPosition(position, isSmoothScroll, 0)
    }

    /**
     * 设置选项2WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    fun setOpt2SelectedPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        optionsWv2.setSelectedPosition(position, isSmoothScroll, smoothDuration)
    }

    /**
     * 获取选项3WheelView 选中下标
     *
     * @return 选中下标
     */
    fun getOpt3SelectedPosition(): Int {
        return optionsWv3.selectedPosition
    }

    /**
     * 设置选项3WheelView 选中下标
     *
     * @param position 选中下标
     */
    fun setOpt3SelectedPosition(position: Int) {
        setOpt3SelectedPosition(position, false)
    }

    /**
     * 设置选项3WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     */
    fun setOpt3SelectedPosition(position: Int, isSmoothScroll: Boolean) {
        setOpt3SelectedPosition(position, isSmoothScroll, 0)
    }

    /**
     * 设置选项3WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    fun setOpt3SelectedPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        optionsWv3.setSelectedPosition(position, isSmoothScroll, smoothDuration)
    }

    /**
     * 获取选项1WheelView 选中的数据
     *
     * @return 选中的数据
     */
    fun getOpt1SelectedData(): T? {
        return if (isLinkage) {
            mOptionsData1!![optionsWv1.selectedPosition]
        } else {
            optionsWv1.selectedData
        }
    }

    /**
     * 获取选项2WheelView 选中的数据
     *
     * @return 选中的数据
     */
    fun getOpt2SelectedData(): T? {
        return if (isLinkage) {
            mOptionsData2!!.get(optionsWv1.selectedPosition)[optionsWv2.selectedPosition]
        } else {
            optionsWv2.selectedData
        }
    }

    /**
     * 获取选项3WheelView 选中的数据
     *
     * @return 选中的数据
     */
    fun getOpt3SelectedData(): T? {
        return if (isLinkage) {
            if (mOptionsData3 == null) {
                return null
            } else {
                return mOptionsData3!![optionsWv1.selectedPosition][optionsWv2.selectedPosition][optionsWv3.selectedPosition]

            }
        } else {
            optionsWv3.selectedData
        }
    }

    override fun onWheelScroll(scrollOffsetY: Int) {

    }

    override fun onWheelItemChanged(oldPosition: Int, newPosition: Int) {

    }

    override fun onWheelSelected(position: Int) {

    }

    override fun onWheelScrollStateChanged(state: Int) {
        onPickerScrollStateChangedListener?.onScrollStateChanged(state)
    }

    /**
     * 选项选中回调
     *
     * @param <T> 泛型
    </T> */
    interface OnSelectedListener<T> {

        /**
         * 选项选中回调
         *
         * @param opt1Pos  选项1WheelView 选中的下标
         * @param opt1Data 选项1WheelView 选中的下标对应的数据（普通用法第一项无数据返回null）
         * @param opt2Pos  选项2WheelView 选中的下标
         * @param opt2Data 选项2WheelView 选中的下标对应的数据（普通用法第二项无数据返回null）
         * @param opt3Pos  选项3WheelView 选中的下标（两级联动返回 -1）
         * @param opt3Data 选项3WheelView 选中的下标对应的数据（普通用法第三项无数据或者两级联动返回null）
         */
        fun onSelected(opt1Pos: Int, opt1Data: T?, opt2Pos: Int, opt2Data: T?, opt3Pos: Int, opt3Data: T?)
    }

    interface OnPickerScrollStateChangedListener {

        fun onScrollStateChanged(state: Int)
    }
}