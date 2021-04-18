package me.ibore.demo.web

import android.os.Bundle
import me.ibore.base.XWebActivity
import me.ibore.demo.base.BaseActivity
import me.ibore.demo.databinding.ActivityWebBinding
import me.ibore.demo.databinding.TitleBarBinding
import me.ibore.utils.DialogUtils


class WebActivity : BaseActivity<ActivityWebBinding>() {

    override fun ActivityWebBinding.onBindView(bundle: Bundle?, savedInstanceState: Bundle?) {
        setTitleBar(TitleBarBinding.bind(mBinding.titleBar), bundle?.getString("title"))
        mBinding.btnCommonWeb.setOnClickListener {
            XWebActivity.startActivity(getXActivity(), XWebActivity.Builder("android", "file:///android_asset/android.html"))
        }
//        binding.btnCommonWeb.setOnClickListener {
//            XWebActivity.startActivity(getXActivity(), XWebActivity.Builder("百度一下", "https://www.baidu.com/"))
//        }
        mBinding.btnSogouMap.setOnClickListener {
            XWebActivity.startActivity(getXActivity(), XWebActivity.Builder("搜狗地图", "https://map.sogou.com/m/webapp/m.html"))
        }
        mBinding.btnWebDialog.setOnClickListener {
            DialogUtils.showInput(getXActivity(), "", "请输入姓名", "张三", negativeListener = {

            }, positiveListener = { input, _ ->
                DialogUtils.showAlert(getXActivity(), content = input)
            })
            //XWebActivity.startActivity(getXActivity(), XWebActivity.Builder("H5弹框", "file:android_asset/android.html"))
            /*XWebActivity.startActivity(getXActivity(), XWebActivity.Builder("H5弹框", data = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<meta charset=\"utf-8\">\n" +
                    "<script>\n" +
                    "//方式一\n" +
                    "prompt(\"开心吗?\"); // 这个显示内容也可以不写，但就没有交互的意义了。\n" +
                    "//方式二\n" +
                    "var x;\n" +
                    "var name=prompt(\"请输入你的名字\",\"Keafmd\"); //显示默认文本 \"Keafmd\"\n" +
                    "if (name!=null && name!=\"\"){\n" +
                    "x=\"你好! \" + name + \"。\";\n" +
                    "document.write(x)\n" +
                    "}\n" +
                    "</script>\n" +
                    "<title></title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "</body>\n" +
                    "</html>"))*/
        }
        mBinding.btnVideoWeb.setOnClickListener {
            XWebActivity.startActivity(getXActivity(), XWebActivity.Builder("视频网站", "https://m.bilibili.com/"))
        }
    }

    override fun onBindData() {

    }

}