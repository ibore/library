package me.ibore.dialog

import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.ibore.R
import me.ibore.base.XActivity
import me.ibore.base.XDialog
import me.ibore.databinding.XDialogListBinding
import me.ibore.databinding.XItemDialogListBinding
import me.ibore.ktx.dp2px
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.layoutmanager.CenterLayoutManager
import me.ibore.utils.ScreenUtils
import me.ibore.utils.ToastUtils

class XListDialog : XDialog<XDialogListBinding>() {

    companion object {

        fun show(activity: XActivity<*>, builder: Builder) {
            val dialog = XListDialog()
            val bundle = Bundle()
            bundle.putParcelable("builder", builder)
            dialog.show(activity, bundle)
        }
    }

    private val builder: Builder by lazy {
        arguments?.getParcelable("builder") ?: Builder()
    }

    override fun XDialogListBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val context = requireContext()
        if (builder.showBottom) {
            mBinding.titleBar.tvNegative.visibility = View.VISIBLE
            mBinding.titleBar.tvPositive.visibility = View.VISIBLE
            mBinding.bottomBar.root.visibility = View.GONE
        } else {
            mBinding.titleBar.tvNegative.visibility = View.GONE
            mBinding.titleBar.tvPositive.visibility = View.GONE
            mBinding.bottomBar.root.visibility = View.VISIBLE
        }
        mBinding.titleBar.tvTitle.text = builder.title ?: ""
        mBinding.titleBar.tvNegative.setOnClickListener {
            dismiss()
        }
        mBinding.titleBar.tvPositive.setOnClickListener {
            if (builder.selectedDatas.isEmpty()) {
                ToastUtils.showShort(getString(R.string.dialog_list_at_least_one))
            } else {
                dismiss()
                builder.selectedListener?.invoke(builder.selectedDatas)
            }
        }
        mBinding.bottomBar.btnNegative.setOnClickListener {
            dismiss()
        }
        mBinding.bottomBar.btnPositive.setOnClickListener {
            if (builder.selectedDatas.isEmpty()) {
                ToastUtils.showShort(getString(R.string.dialog_list_at_least_one))
            } else {
                dismiss()
                builder.selectedListener?.invoke(builder.selectedDatas)
            }
        }
        if (builder.maxCount < builder.selectedDatas.size) {
            for (index in builder.selectedDatas.count() - 1 downTo 0) {
                if (index >= builder.maxCount) {
                    builder.selectedDatas.removeAt(index)
                }
            }
        }
        val adapter = Adapter(this@XListDialog, builder)
        mBinding.recyclerView.layoutManager = CenterLayoutManager(context)
        mBinding.recyclerView.adapter = adapter
        val layoutParams = mBinding.recyclerView.layoutParams
        if (builder.datas.size >= 6) {
            if ((context.resources.getDimensionPixelOffset(R.dimen.dialog_view_list_content_height) * 6) >= (ScreenUtils.getAppScreenHeight() / 2)) {
                layoutParams.height = ScreenUtils.getAppScreenHeight() / 2
            } else {
                layoutParams.height =
                    context.resources.getDimensionPixelOffset(R.dimen.dialog_view_list_content_height) * 6
            }
        } else {
            for (index in 0..4) {
                if ((context.resources.getDimensionPixelOffset(R.dimen.dialog_view_list_content_height) * index) >= (ScreenUtils.getAppScreenHeight() / 2)) {
                    layoutParams.height = ScreenUtils.getAppScreenHeight() / 2
                    break
                }
            }
        }
        mBinding.recyclerView.layoutParams = layoutParams
        adapter.setDatas(builder.datas)
        if (builder.selectedDatas.isNotEmpty()) {
            val firstSelected = builder.selectedDatas.first()
            var position = builder.datas.indexOf(firstSelected)
            if (position < 0) {
                for (index in builder.datas.indices) {
                    if (TextUtils.equals(builder.datas[index], firstSelected)) {
                        position = index
                        break
                    }
                }
            }
            mBinding.recyclerView.smoothScrollToPosition(position)
        }
    }

    override fun onBindDialogConfig(): DialogConfig {
        return if (builder.showBottom) {
            DialogConfig(
                ScreenUtils.getAppScreenWidth(), DialogConfig.WRAP_CONTENT, gravity = Gravity.BOTTOM,
                touchBack = builder.touchBack, touchOutside = builder.touchOutside
            )
        } else {
            DialogConfig(
                dp2px(280F), DialogConfig.WRAP_CONTENT, gravity = Gravity.CENTER,
                touchBack = builder.touchBack, touchOutside = builder.touchOutside
            )
        }
    }

    class Adapter(private val dialog: XListDialog, private val builder: Builder) :
        BindingAdapter<XItemDialogListBinding, CharSequence>() {

        private fun removeSelected(data: CharSequence): Boolean {
            if (builder.selectedDatas.remove(data)) {
                return true
            }
            for (selected in builder.selectedDatas.iterator()) {
                if (TextUtils.equals(data, selected)) {
                    builder.selectedDatas.remove(selected)
                    return true
                }
            }
            return false
        }

        fun isSelected(data: CharSequence): Boolean {
            if (builder.selectedDatas.contains(data)) {
                return true
            }
            for (selected in builder.selectedDatas.iterator()) {
                if (TextUtils.equals(data, selected)) {
                    return true
                }
            }
            return false
        }

        override fun XItemDialogListBinding.onBindHolder(
            holder: RecyclerHolder, data: CharSequence, dataPosition: Int
        ) {
            tvContent.text = data
            viewBottomLine.visibility =
                if (dataPosition + 1 == getDataSize()) View.GONE else View.VISIBLE
            tvContent.setOnClickListener {
                if (builder.maxCount == 1) {
                    if (isSelected(data)) {
                        builder.selectedDatas.clear()
                    } else {
                        builder.selectedDatas.clear()
                        builder.selectedDatas.add(data)
                    }
                } else if (builder.selectedDatas.size < builder.maxCount) {
                    if (isSelected(data)) {
                        removeSelected(data)
                    } else {
                        builder.selectedDatas.add(data)
                    }
                } else if (builder.selectedDatas.size == builder.maxCount && isSelected(data)) {
                    removeSelected(data)
                } else {
                    ToastUtils.showShort(dialog.getString(R.string.dialog_list_max_count, builder.maxCount))
                }
                notifyDataSetChanged()
            }
            tvContent.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, if (isSelected(data)) R.drawable.x_selector else 0, 0
            )
            llItem.isSelected = isSelected(data)
        }
    }

    @Parcelize
    data class Builder(
        val title: CharSequence? = null,
        val datas: @RawValue MutableList<CharSequence> = ArrayList(),
        val negative: CharSequence? = null,
        val positive: CharSequence? = null,
        val maxCount: Int = 1,
        val selectedDatas: @RawValue MutableList<CharSequence> = ArrayList(),
        val selectedListener: @RawValue ((selectedDatas: MutableList<CharSequence>) -> Unit)? = null,
        val showBottom: Boolean = false,
        val touchBack: Boolean = false,
        val touchOutside: Boolean = false
    ) : Parcelable

}