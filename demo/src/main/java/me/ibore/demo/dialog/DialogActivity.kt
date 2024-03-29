package me.ibore.demo.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import me.ibore.demo.adapter.TitleAdapter
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityDialogBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.demo.model.TitleItem
import me.ibore.recycler.delegate.Delegate
import me.ibore.utils.*

@Suppress("UNCHECKED_CAST")
class DialogActivity : BaseActivity<ActivityDialogBinding>() {

    private var adapter = TitleAdapter()

    private var selectedDatas : MutableList<CharSequence> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun ActivityDialogBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        recyclerView.layoutManager = GridLayoutManager(getXActivity(), 3)
        recyclerView.adapter = adapter
    }

    override fun onBindData() {

        adapter.addData(TitleItem("普通内容") {
            Utils.sp.put("key", "普通内容")
            ToastUtils.showShort("普通内容")
        })

        adapter.addData(TitleItem("普通内容") {
            DialogUtils.showAlert(getXActivity(), content = "这是一个普通内容对话框。")
        })

        adapter.addData(TitleItem("普通标题内容") {
            DialogUtils.showAlert(getXActivity(), title = "这是标题", content = "这是一个普通标题内容对话框。")
        })

        adapter.addData(TitleItem("普通标题内容超长") {
            DialogUtils.showAlert(getXActivity(), title = "超长长长长长长长长长长长长长长长长长长长长长标题", content = "这是一个普通标题内容超长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长对话框。")
        })

        adapter.addData(TitleItem("标题内容按钮") {
            DialogUtils.showAlert(
                getXActivity(),
                title = "退出登录",
                content = "您确定要退出登录吗？",
                negativeListener = {
                    ToastUtils.showShort("取消")
                },
                positiveListener = {
                    ToastUtils.showShort("确认")
                })
        })


        adapter.addData(TitleItem("普通列表单选") {
            val datas = arrayListOf<CharSequence>()

            datas.add("英语")
            datas.add("数学")
            datas.add("语文")
            datas.add("物理")
            datas.add("化学")
            datas.add("地理")
            datas.add("生物")
            datas.add("政治")

            DialogUtils.showList(getXActivity(), title = "普通列表单选", datas = datas,
                negative = "", positive = "",
                showBottom = false, maxCount = 1,
                selectedDatas = selectedDatas, selectedListener = {
                selectedDatas = it
            }, touchBack = false, touchOutside = false
            )
        })

        adapter.addData(TitleItem("普通列表多选") {
            val datas = arrayListOf<CharSequence>()

            datas.add("英语")
            datas.add("数学")
            datas.add("语文")
            datas.add("物理")
            datas.add("化学")
            datas.add("地理")
            datas.add("生物")
            datas.add("政治")

            DialogUtils.showList(
                getXActivity(), title = "普通列表多选", datas = datas,
                negative = "", positive = "",
                showBottom = true, maxCount = 4,
                selectedDatas = selectedDatas, selectedListener = {
                    selectedDatas = it
                }, touchBack = false, touchOutside = false
            )
        })
    }

    class TitleModel(override val delegateType: Int, val dataString: String) :
        Delegate(delegateType)

}