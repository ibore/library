package me.ibore.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.databinding.DialogRegionBinding
import me.ibore.ktx.dp2px
import me.ibore.model.RegionModel
import me.ibore.utils.ScreenUtils

class XRegionDialog : XDialog<DialogRegionBinding>() {

    companion object {
        fun show(activity: XActivity<*>, title: CharSequence? = null, selectedProvinceCode: String? = null, selectedCityCode: String? = null, selectedCountyCode: String? = null,
                 onRegionSelectedListener: ((provinceModel: RegionModel?, cityModel: RegionModel?, countyModel: RegionModel?) -> Unit)? = null,
                 showBottom: Boolean = false, touchBack: Boolean = false, touchOutside: Boolean = false) {
            val dialog = XRegionDialog()
            val bundle = Bundle()
            bundle.putParcelable("builder", Builder(title, selectedProvinceCode, selectedCityCode, selectedCountyCode, onRegionSelectedListener, showBottom, touchBack, touchOutside))
            dialog.show(activity, bundle)
        }
    }

    private val builder: Builder by lazy {
        arguments?.getParcelable("builder") ?: Builder()
    }

    override fun DialogRegionBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val context = requireContext()
        if (builder.showBottom) {
            mBinding.tvNegative.visibility = View.VISIBLE
            mBinding.tvPositive.visibility = View.VISIBLE
            mBinding.btnPositive.visibility = View.GONE
        } else {
            mBinding.tvNegative.visibility = View.GONE
            mBinding.tvPositive.visibility = View.GONE
            mBinding.btnPositive.visibility = View.VISIBLE
        }
        mBinding.tvTitle.text = builder.title ?: getString(R.string.dialog_timer_title)
        mBinding.tvNegative.setOnClickListener {
            dismiss()
        }
        mBinding.tvPositive.setOnClickListener {
            dismiss()
        }
        mBinding.btnPositive.setOnClickListener {
            dismiss()
        }
        mBinding.regionPickerView.setSelectedRegion(
            builder.selectedProvinceCode,
            builder.selectedCityCode,
            builder.selectedCountyCode
        )
        mBinding.regionPickerView.onRegionSelectedListener = builder.onRegionSelectedListener

    }

    override fun onBindDialogConfig(): DialogConfig {
        return if (builder.showBottom) {
            DialogConfig(
                ScreenUtils.appScreenWidth, DialogConfig.WRAP_CONTENT, gravity = Gravity.BOTTOM,
                touchBack = builder.touchBack, touchOutside = builder.touchOutside
            )
        } else {
            DialogConfig(
                dp2px(280F), DialogConfig.WRAP_CONTENT, gravity = Gravity.CENTER,
                touchBack = builder.touchBack, touchOutside = builder.touchOutside
            )
        }
    }

    @Parcelize
    class Builder(val title: CharSequence? = null, val selectedProvinceCode: String? = null, val selectedCityCode: String? = null, val selectedCountyCode: String? = null,
                  val onRegionSelectedListener: @RawValue ((provinceModel: RegionModel?, cityModel: RegionModel?, countyModel: RegionModel?) -> Unit)? = null,
                  val showBottom: Boolean = false, val touchBack: Boolean = false, val touchOutside: Boolean = false) : Parcelable

}