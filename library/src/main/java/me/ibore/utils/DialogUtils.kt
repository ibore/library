@file:Suppress("UNCHECKED_CAST")

package me.ibore.utils

import kotlinx.parcelize.RawValue
import me.ibore.base.XActivity
import me.ibore.dialog.XAlertDialog
import me.ibore.dialog.XInputDialog
import me.ibore.dialog.XListDialog
import me.ibore.listener.OnSelectListener
import me.ibore.model.RegionModel
import me.ibore.widget.wheel.DatePickerView
import me.ibore.widget.wheel.OptionsPickerView

object DialogUtils {

    /**
     * 普通内容对话框
     */
    @JvmOverloads
    fun showAlert(activity: XActivity<*>, title: CharSequence? = null, content: CharSequence? = null,
                  negative: CharSequence? = null, negativeListener: @RawValue ((dialog: XAlertDialog) -> Unit)? = null,
                  positive: CharSequence? = null, positiveListener: @RawValue ((dialog: XAlertDialog) -> Unit)? = null,
                  touchBack: Boolean = false, touchOutside: Boolean = false) {
        XAlertDialog.show(
            activity,
            XAlertDialog.Builder(
                title, content, negative, negativeListener, positive, positiveListener,
                touchBack, touchOutside
            )
        )
    }

    /**
     * 普通内容对话框
     */
    @JvmOverloads
    fun showInput(activity: XActivity<*>, title: CharSequence? = null, hint: CharSequence? = null, defaultInput: CharSequence? = null,
                  negative: CharSequence? = null, negativeListener: @RawValue ((dialog: XInputDialog) -> Unit)? = null,
                  positive: CharSequence? = null, positiveListener: @RawValue ((input: String, dialog: XInputDialog) -> Unit)? = null,
                  touchBack: Boolean = false, touchOutside: Boolean = false) {
        XInputDialog.show(
            activity,
            XInputDialog.Builder(
                title, hint, defaultInput, negative, negativeListener, positive, positiveListener,
                touchBack, touchOutside
            )
        )
    }

    /**
     * 列表对话框
     */
    @JvmOverloads
    fun showList(
        activity: XActivity<*>, title: CharSequence? = null,
        datas: @RawValue MutableList<CharSequence> = ArrayList(),
        negative: CharSequence? = null, positive: CharSequence? = null,
        showBottom: Boolean = false, maxCount: Int = 1,
        selectedDatas: @RawValue MutableList<CharSequence> = ArrayList(),
        selectedListener: @RawValue ((selectedDatas: MutableList<CharSequence>) -> Unit)? = null,
        touchBack: Boolean = false, touchOutside: Boolean = false
    ) {
        XListDialog.show(
            activity, XListDialog.Builder(
                title, datas, negative, positive, maxCount,
                selectedDatas, selectedListener, showBottom, touchBack, touchOutside
            )
        )
    }

    fun showDateY(activity: XActivity<*>, selectDate: String, startDate: String, endDate: String, onSelectListener: OnSelectListener<String>?) {
        showDate(activity, selectDate, startDate, endDate, DatePickerView.YYYY, onSelectListener)
    }

    fun showDateYM(activity: XActivity<*>, selectDate: String, startDate: String, endDate: String, onSelectListener: OnSelectListener<String>?) {
        showDate(activity, selectDate, startDate, endDate, DatePickerView.YYYY_MM, onSelectListener)
    }

    fun showDateYMD(activity: XActivity<*>, selectDate: String, startDate: String, endDate: String, onSelectListener: OnSelectListener<String>?) {
        showDate(activity, selectDate, startDate, endDate, DatePickerView.YYYY_MM_DD, onSelectListener)
    }

    private fun showDate(activity: XActivity<*>, selectDate: String?, startDate: String?, endDate: String?, @DatePickerView.DateFormat dateFormat: String, onSelectListener: OnSelectListener<String>?) {
//        val dialogDateBody = DialogDateBody.create()
//                .dateFormat(dateFormat)
//                .selectDate(selectDate)
//                .startDate(startDate)
//                .endDate(endDate)
//        val dialogHeader = getDefaultHeader(activity.getText(R.string.dialog_date_title)) {
//            onSelectListener?.select(dialogDateBody.getSelectDate())
//        }
//        showDialog(activity, dialogHeader, dialogDateBody, showBottom = true)
    }

    fun showTimer(activity: XActivity<*>, title: CharSequence? = null, selectedTimer: String, startTimer: String, endTimer: String, onSelectListener: ((String?) -> Unit)?,
                  showBottom: Boolean = false, touchBack: Boolean = false, touchOutside: Boolean = false) {
        //TimerDialog.show(activity, title, selectedTimer, startTimer, endTimer, onSelectListener, showBottom, touchBack, touchOutside)
    }

    fun showRegion(activity: XActivity<*>, title: CharSequence, selectedProvinceCode: String? = null, selectedCityCode: String? = null, selectedCountyCode: String? = null,
                   onRegionSelectedListener: ((provinceModel: RegionModel?, cityModel: RegionModel?, countyModel: RegionModel?) -> Unit)? = null,
                   showBottom: Boolean = false, touchBack: Boolean = false, touchOutside: Boolean = false) {
        //RegionDialog.show(activity, title, selectedProvinceCode, selectedCityCode, selectedCountyCode, onRegionSelectedListener, showBottom, touchBack, touchOutside)
    }

    fun <T> showPicker(activity: XActivity<*>, title: CharSequence, data1: MutableList<T>, selectPosition1: Int,
                       onSelectedListener: OptionsPickerView.OnSelectedListener<T>) {
        showPicker(activity, title, data1, selectPosition1, null, 0, onSelectedListener)
    }

    fun <T> showPicker(activity: XActivity<*>, title: CharSequence, data1: MutableList<T>, selectPosition1: Int,
                       data2: MutableList<T>?, selectPosition2: Int,
                       onSelectedListener: OptionsPickerView.OnSelectedListener<T>) {
        showPicker(activity, title, data1, selectPosition1, data2, selectPosition2, null, 0, onSelectedListener)
    }

    fun <T> showPicker(activity: XActivity<*>, title: CharSequence, data1: MutableList<T>, selectPosition1: Int,
                       data2: MutableList<T>?, selectPosition2: Int, data3: MutableList<T>?, selectPosition3: Int,
                       onSelectedListener: OptionsPickerView.OnSelectedListener<T>?) {
//        val dialogPickerBody = DialogPickerBody.create<T>()
//                .setData(data1, selectPosition1, data2, selectPosition2, data3, selectPosition3)
//        val dialogHeader = getDefaultHeader(title) {
//            onSelectedListener?.onSelected(
//                    dialogPickerBody.opt1SelectedPosition, dialogPickerBody.opt1SelectedData,
//                    dialogPickerBody.opt2SelectedPosition, dialogPickerBody.opt2SelectedData,
//                    dialogPickerBody.opt3SelectedPosition, dialogPickerBody.opt3SelectedData)
//        }
//        showDialog(activity, dialogHeader, dialogPickerBody, showBottom = true)
    }

    fun <T> showPickerLinkage(activity: XActivity<*>, title: CharSequence, linkageData1: MutableList<T>, selectPosition1: Int,
                              linkageData2: MutableList<MutableList<T>>, selectPosition2: Int,
                              onSelectedListener: OptionsPickerView.OnSelectedListener<T>) {
        showPickerLinkage(activity, title, linkageData1, selectPosition1, linkageData2, selectPosition2, null, 0, onSelectedListener)
    }

    fun <T> showPickerLinkage(activity: XActivity<*>, title: CharSequence,
                              linkageData1: MutableList<T>, selectPosition1: Int,
                              linkageData2: MutableList<MutableList<T>>, selectPosition2: Int,
                              linkageData3: MutableList<MutableList<MutableList<T>>>?, selectPosition3: Int,
                              onSelectedListener: OptionsPickerView.OnSelectedListener<T>?) {
//        val dialogPickerBody = DialogPickerBody.create<T>().setLinkageData(linkageData1, selectPosition1,
//                linkageData2, selectPosition2, linkageData3, selectPosition3)
//        val dialogHeader = getDefaultHeader(title) {
//            onSelectedListener?.onSelected(dialogPickerBody.opt1SelectedPosition, dialogPickerBody.opt1SelectedData,
//                    dialogPickerBody.opt2SelectedPosition, dialogPickerBody.opt2SelectedData,
//                    dialogPickerBody.opt3SelectedPosition, dialogPickerBody.opt3SelectedData)
//        }
//        showDialog(activity, dialogHeader, dialogPickerBody, showBottom = true)
    }

}