//package me.ibore.dialog
//
//import android.annotation.SuppressLint
//import android.view.View
//import androidx.core.content.ContextCompat
//import me.ibore.R
//import me.ibore.builder.DialogBuilder
//import me.ibore.utils.UIUtils
//import me.ibore.widget.wheel.DatePickerView
//import me.ibore.widget.wheel.DatePickerView.Companion.YYYY
//import me.ibore.widget.wheel.DatePickerView.Companion.YYYY_MM
//import me.ibore.widget.wheel.DatePickerView.Companion.YYYY_MM_DD
//
//@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//open class DialogDateBody private constructor() : DialogBuilder<DialogDateBody>() {
//
//    companion object {
//
//        fun create(): DialogDateBody = DialogDateBody()
//    }
//
//    private var selectDate: String? = null
//    private var startDate: String? = null
//    private var endDate: String? = null
//
//    @DatePickerView.DateFormat
//    private var dateFormat = YYYY_MM_DD
//
//    private var onInitViewListener: ((DatePickerView) -> Unit)? = null
//
//    init {
//        bgColor = R.color.dialog_view_bg
//        paddingStart = 16F
//        paddingTop = 22F
//        paddingEnd = 16F
//        paddingBottom = 28F
//    }
//
//    fun selectDate(selectDate: String?): DialogDateBody {
//        this.selectDate = selectDate
//        return this
//    }
//
//    fun startDate(startDate: String?): DialogDateBody {
//        this.startDate = startDate
//        return this
//    }
//
//    fun endDate(endDate: String?): DialogDateBody {
//        this.endDate = endDate
//        return this
//    }
//
//    fun dateFormat(@DatePickerView.DateFormat dateFormat: String): DialogDateBody {
//        this.dateFormat = dateFormat
//        return this
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    override fun builder(target: DialogView): View {
//        val datePickerView = DatePickerView(target.requireContext())
//        setPadding(datePickerView, paddingStart, paddingTop, paddingEnd, paddingBottom)
//        datePickerView.setBackgroundResource(bgColor)
//        if (null != onInitViewListener) {
//            onInitViewListener!!.invoke(datePickerView)
//        } else {
//            datePickerView.apply {
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
//        datePickerView.setDate(selectDate, startDate, endDate, dateFormat)
//        datePickerView.onDateSelectedListener = { _, _, _, _, _ ->
//            selectDate = when (dateFormat) {
//                YYYY -> datePickerView.selectedYearString
//                YYYY_MM -> datePickerView.selectedYearString + "-" + datePickerView.selectedMonthString
//                else -> datePickerView.selectedDate
//            }
//        }
//        return datePickerView
//    }
//
//    fun getSelectDate(): String? {
//        return selectDate
//    }
//
//
//}
