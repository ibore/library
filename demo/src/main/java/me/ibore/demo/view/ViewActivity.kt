package me.ibore.demo.view

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.ActivityAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityViewBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.ActivityItem

class ViewActivity : BaseActivity<ActivityViewBinding>() {


    private var adapter = ActivityAdapter()

    override fun ActivityViewBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.recyclerView.layoutManager = GridLayoutManager(getXActivity(), 2)
        mBinding.recyclerView.adapter = adapter
    }

    override fun onBindData() {

        adapter.addData(ActivityItem("Wheel滚动", WheelActivity::class.java))
        adapter.addData(ActivityItem("Picker选择", PickerActivity::class.java))


    }

}
