//package me.ibore.dialog
//
//import android.view.View
//import androidx.core.content.ContextCompat
//import me.ibore.R
//import me.ibore.builder.DialogBuilder
//import me.ibore.listener.OnInitViewListener
//import me.ibore.utils.UIUtils
//
//import me.ibore.widget.wheel.OptionsPickerView
//
//class DialogPickerBody<T> private constructor() : DialogBuilder<DialogPickerBody<T>>() {
//
//    companion object {
//        fun <T> create() = DialogPickerBody<T>()
//    }
//
//    private var isLinkage: Boolean = false
//    private var data1: MutableList<T>? = null
//    private var data2: MutableList<T>? = null
//    private var data3: MutableList<T>? = null
//    private var linkageData1: MutableList<T>? = null
//    private var linkageData2: MutableList<MutableList<T>>? = null
//    private var linkageData3: MutableList<MutableList<MutableList<T>>>? = null
//    private var selectPosition1: Int = 0
//    private var selectPosition2: Int = 0
//    private var selectPosition3: Int = 0
//
//    private var pickerView: OptionsPickerView<T>? = null
//    private var onInitViewListener: OnInitViewListener<OptionsPickerView<T>>? = null
//
//    val opt1SelectedData: T? = pickerView?.getOpt1SelectedData()
//
//    val opt2SelectedData: T? = pickerView?.getOpt2SelectedData()
//
//    val opt3SelectedData: T? = pickerView?.getOpt3SelectedData()
//
//    val opt1SelectedPosition: Int = pickerView?.getOpt1SelectedPosition() ?: 0
//
//    val opt2SelectedPosition: Int = pickerView?.getOpt2SelectedPosition() ?: 0
//
//    val opt3SelectedPosition: Int = pickerView?.getOpt3SelectedPosition() ?: 0
//
//    init {
//        bgColor = R.color.dialog_view_bg
//        paddingStart = 16F
//        paddingTop = 22F
//        paddingEnd = 16F
//        paddingBottom = 28F
//    }
//
//    /**
//     * 设置不联动数据
//     *
//     * @return
//     */
//    fun setData(data: MutableList<T>, selectPosition1: Int): DialogPickerBody<T> {
//        return setData(data, selectPosition1, null, 0, null, 0)
//    }
//
//    /**
//     * 设置不联动数据
//     * @return
//     */
//    @JvmOverloads
//    fun setData(data1: MutableList<T>, selectPosition1: Int, data2: MutableList<T>?, selectPosition2: Int, data3: MutableList<T>? = null, selectPosition3: Int = 0): DialogPickerBody<T> {
//        isLinkage = false
//        this.data1 = data1
//        this.data2 = data2
//        this.data3 = data3
//        this.selectPosition1 = selectPosition1
//        this.selectPosition2 = selectPosition2
//        this.selectPosition3 = selectPosition3
//        return this
//    }
//
//
//    /**
//     * 设置联动数据
//     *
//     * @return
//     */
//    fun setLinkageData(linkageData1: MutableList<T>, selectPosition1: Int, linkageData2: MutableList<MutableList<T>>, selectPosition2: Int): DialogPickerBody<T> {
//        return setLinkageData(linkageData1, selectPosition1, linkageData2, selectPosition2)
//    }
//
//    /**
//     * 设置联动数据
//     *
//     * @return
//     */
//    fun setLinkageData(linkageData1: MutableList<T>, selectPosition1: Int, linkageData2: MutableList<MutableList<T>>, selectPosition2: Int,
//                       linkageData3: MutableList<MutableList<MutableList<T>>>?, selectPosition3: Int): DialogPickerBody<T> {
//        isLinkage = true
//        this.linkageData1 = linkageData1
//        this.linkageData2 = linkageData2
//        this.linkageData3 = linkageData3
//        this.selectPosition1 = selectPosition1
//        this.selectPosition2 = selectPosition2
//        this.selectPosition3 = selectPosition3
//        return this
//    }
//
//    fun setOnInitViewListener(onInitViewListener: OnInitViewListener<OptionsPickerView<T>>): DialogPickerBody<T> {
//        this.onInitViewListener = onInitViewListener
//        return this
//    }
//
//    override fun builder(target: DialogView): View {
//        val content = target.requireContext()
//        pickerView = OptionsPickerView(content)
//        pickerView!!.setPadding(dp2px(content, paddingStart.toFloat()), dp2px(content, paddingTop.toFloat()),
//                dp2px(content, paddingEnd.toFloat()), dp2px(content, paddingBottom.toFloat()))
//        pickerView!!.setBackgroundResource(bgColor)
//
//        if (null != onInitViewListener) {
//            onInitViewListener!!.initView(pickerView!!)
//        } else {
//            pickerView!!.apply {
//                this.textSize = sp2px(context, 18F)
//                this.autoFitTextSize = true
//                this.showDivider = true
//                this.dividerHeight = dp2px(context, 1F)
//                this.dividerColor = color(R.color.dialog_view_line)
//                this.visibleItems = 9
//                this.lineSpacing = dp2px(context, 16F)
//                this.textBoundaryMargin = dp2px(context, 16F)
//            }
//        }
//        if (isLinkage) {
//            pickerView!!.setLinkageData(linkageData1, linkageData2, linkageData3)
//        } else {
//            pickerView!!.setData(data1!!, data2, data3)
//        }
//        pickerView!!.setOpt1SelectedPosition(selectPosition1)
//        pickerView!!.setOpt2SelectedPosition(selectPosition2)
//        pickerView!!.setOpt3SelectedPosition(selectPosition3)
//        return pickerView!!
//    }
//
//}
//
