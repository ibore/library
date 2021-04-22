package me.ibore.demo.recycler

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.animation.*
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityRecyclerBinding
import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.ktx.dp2px
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class RecyclerAnimActivity : BaseActivity<ActivityRecyclerBinding>() {

    private var adapter = object : BindingAdapter<ItemActivityBinding, String>() {
        override fun ItemActivityBinding.onBindHolder(
            holder: RecyclerHolder, data: String, dataPosition: Int
        ) {
            tvTitle.text = data
            tvTitle.updateLayoutParams<LinearLayout.LayoutParams> {
                height = dp2px(56F)
            }
        }
    }

    override fun ActivityRecyclerBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 2)
        mBinding.recyclerView.adapter = adapter
    }

    override fun onBindData() {
        mBinding.rgAnim.setOnCheckedChangeListener { group, checkedId ->
            adapter.clearDatas()
            group.postDelayed({
                when (checkedId) {
                    mBinding.rbAlphaIn.id -> {
                        adapter.setDatas(getDatas())
                        adapter.setAnimation(AlphaInAnimation())
                    }
                    mBinding.rbScaleIn.id -> {
                        adapter.setDatas(getDatas())
                        adapter.setAnimation(ScaleInAnimation())
                    }
                    mBinding.rbSiBottom.id -> {
                        adapter.setDatas(getDatas())
                        adapter.setAnimation(SlideBottomAnimation())
                    }
                    mBinding.rbSiLeft.id -> {
                        adapter.setDatas(getDatas())
                        adapter.setAnimation(SlideLeftAnimation())
                    }
                    mBinding.rbSiRight.id -> {
                        adapter.setDatas(getDatas())
                        adapter.setAnimation(SlideRightAnimation())
                    }
                }
            }, 300)
        }
        mBinding.rgAnim.check(mBinding.rbSiRight.id)
        mBinding.cbAnimatorFirstOnly.setOnCheckedChangeListener { _, isChecked ->
            adapter.setAnimatorFirstOnly(isChecked)
        }
        mBinding.cbAnimatorFirstOnly.isChecked = true
    }

    private fun getDatas(): ArrayList<String> {
        val datas = ArrayList<String>()
        for (i in 1..100) {
            datas.add("测试数据：$i")
        }
        return datas
    }

}