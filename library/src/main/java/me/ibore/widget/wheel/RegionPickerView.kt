package me.ibore.widget.wheel

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Nullable
import androidx.annotation.RawRes
import me.ibore.model.RegionModel
import me.ibore.utils.ChinaRegionUtils
import me.ibore.utils.StringUtils

class RegionPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    lateinit var provinceWv: WheelView<RegionModel>
    lateinit var cityWv: WheelView<RegionModel>
    lateinit var countyWv: WheelView<RegionModel>
    private var selectedProvinceCode: String? = "110000"
    private var selectedCityCode: String? = "110100"
    private var selectedCountyCode: String? = "110101"
    private var isSmoothScroll: Boolean = true
    private var smoothDuration: Int = 250
    private val onSelectedListener: ((wheelView: WheelView<RegionModel>, data: RegionModel, position: Int) -> Unit) = { wheelView, data, position ->
        if (provinceWv == wheelView) { // 年份选中
            selectedProvinceCode = wheelView.selectedData?.code
            if (cityWv.visibility == View.VISIBLE) {
                val provinceModel = provinceWv.selectedData!!
                cityWv.datas = ChinaRegionUtils.getCityList(provinceModel.code)
                var selectedPosition = 0
                for (index in cityWv.datas.indices) {
                    if (TextUtils.equals(selectedCityCode, provinceModel.code)) {
                        selectedPosition = index
                        break
                    }
                }
                cityWv.setSelectedPosition(selectedPosition, isSmoothScroll, smoothDuration)
            }
        } else if (cityWv == wheelView) { // 月份选中
            selectedCityCode = wheelView.selectedData?.code
            if (countyWv.visibility == View.VISIBLE) {
                val cityModel = cityWv.selectedData!!
                countyWv.datas = ChinaRegionUtils.getCountyList(cityModel.code)
                var selectedPosition = 0
                for (index in countyWv.datas.indices) {
                    if (TextUtils.equals(selectedCountyCode, cityModel.code)) {
                        selectedPosition = index
                        break
                    }
                }
                countyWv.setSelectedPosition(selectedPosition, isSmoothScroll, smoothDuration)
            }
        } else if (countyWv == wheelView) {
            selectedCountyCode = wheelView.selectedData?.code
        }
        val provinceModel = provinceWv.selectedData
        val cityModel = if (cityWv.visibility == View.VISIBLE) cityWv.selectedData else null
        val countyModel = if (countyWv.visibility == View.VISIBLE) countyWv.selectedData else null
        onRegionSelectedListener?.invoke(provinceModel, cityModel, countyModel)
    }

    var onRegionSelectedListener: ((provinceModel: RegionModel?, cityModel: RegionModel?, countyModel: RegionModel?) -> Unit)? = null
        set(value) {
            field = value
            provinceWv.onSelectedListener = onSelectedListener
            cityWv.onSelectedListener = onSelectedListener
            countyWv.onSelectedListener = onSelectedListener
        }

    init {
        orientation = HORIZONTAL
        setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
        provinceWv = createWheelView(attrs, defStyleAttr)
        cityWv = createWheelView(attrs, defStyleAttr)
        countyWv = createWheelView(attrs, defStyleAttr)
        addView(provinceWv)
        addView(cityWv)
        addView(countyWv)
        provinceWv.datas = ChinaRegionUtils.getProvinceList()
    }

    // 创建年WheelView
    private fun createWheelView(@Nullable attrs: AttributeSet?, defStyleAttr: Int): WheelView<RegionModel> {
        val wheelView = WheelView<RegionModel>(context, attrs, defStyleAttr)
        val params = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        params.weight = 1F
        params.gravity = Gravity.CENTER_VERTICAL
        wheelView.layoutParams = params
        wheelView.onSelectedListener = onSelectedListener
        return wheelView
    }

    fun setSelectedRegion(selectedProvinceCode: String?, selectedCityCode: String?, selectedCountyCode: String?) {
        this.selectedProvinceCode = selectedProvinceCode ?: "110000"
        this.selectedCityCode = selectedCityCode ?: "110100"
        this.selectedCountyCode = selectedCountyCode ?: "110101"
        val provinceModels = ChinaRegionUtils.getProvinceList()
        for (position in 0 until provinceModels.size) {
            if (StringUtils.equals(provinceModels[position].code, selectedProvinceCode)) {
                provinceWv.setSelectedPosition(position)
            }
        }
        if (provinceWv.selectedPosition <= 0) {
            provinceWv.setSelectedPosition(0)
        }
    }

    fun setSelectedRegion(provinceModel: RegionModel?, cityModel: RegionModel?, countyModel: RegionModel?) {
        this.selectedProvinceCode = provinceModel?.code
        this.selectedCityCode = cityModel?.code
        this.selectedCountyCode = countyModel?.code
        val provinceModels = ChinaRegionUtils.getProvinceList()
        for (position in 0 until provinceModels.size) {
            if (StringUtils.equals(provinceModels[position].code, provinceModel?.code)) {
                provinceWv.setSelectedPosition(position)
            }
        }
        if (provinceWv.selectedPosition <= 0) {
            provinceWv.setSelectedPosition(0)
        }
    }

    var provinceVisible: Boolean = true
        set(value) {
            field = value
            provinceWv.visibility = if (field) View.VISIBLE else View.GONE
        }

    var cityVisible: Boolean = true
        set(value) {
            field = value
            cityWv.visibility = if (field) View.VISIBLE else View.GONE
        }

    var countyVisible: Boolean = true
        set(value) {
            field = value
            countyWv.visibility = if (field) View.VISIBLE else View.GONE
        }

    // 选择的省份
    val selectedProvince: RegionModel? get() = provinceWv.selectedData

    // 选择的城市
    val selectedCity: RegionModel? get() = cityWv.selectedData

    // 选择的县区
    val selectedCounty: RegionModel? get() = countyWv.selectedData

    // 可见item数
    var visibleItems: Int = provinceWv.visibleItems
        set(value) {
            provinceWv.visibleItems = value
            cityWv.visibleItems = value
            countyWv.visibleItems = value
            field = provinceWv.visibleItems
        }

    // 是否重置选中下标到第一个
    var resetSelectedPosition: Boolean = provinceWv.resetSelectedPosition
        set(value) {
            field = value
            provinceWv.resetSelectedPosition = resetSelectedPosition
            cityWv.resetSelectedPosition = resetSelectedPosition
            countyWv.resetSelectedPosition = resetSelectedPosition
        }

    // 是否自动调整字体大小，以显示完全
    var autoFitTextSize: Boolean = provinceWv.autoFitTextSize
        set(value) {
            field = value
            provinceWv.autoFitTextSize = autoFitTextSize
            cityWv.autoFitTextSize = autoFitTextSize
            countyWv.autoFitTextSize = autoFitTextSize
        }

    // 是否自动调整字体大小，以显示完全
    var textSize: Int = provinceWv.textSize
        set(value) {
            field = value
            provinceWv.textSize = textSize
            cityWv.textSize = textSize
            countyWv.textSize = textSize
        }

    // 字体
    var typeface: Typeface = provinceWv.typeface
        set(value) {
            field = value
            provinceWv.typeface = typeface
            cityWv.typeface = typeface
            countyWv.typeface = typeface
        }

    // 文字距离边界的外边距
    var textBoundaryMargin: Int = provinceWv.textBoundaryMargin
        set(value) {
            field = value
            provinceWv.textBoundaryMargin = textBoundaryMargin
            cityWv.textBoundaryMargin = textBoundaryMargin
            countyWv.textBoundaryMargin = textBoundaryMargin
        }

    // 未选中item文字颜色
    @ColorInt
    var normalTextColor: Int = provinceWv.normalTextColor
        set(value) {
            field = value
            provinceWv.normalTextColor = normalTextColor
            cityWv.normalTextColor = normalTextColor
            countyWv.normalTextColor = normalTextColor
        }

    // 选中item文字颜色
    @ColorInt
    var selectedTextColor: Int = provinceWv.selectedTextColor
        set(value) {
            field = value
            provinceWv.selectedTextColor = selectedTextColor
            cityWv.selectedTextColor = selectedTextColor
            countyWv.selectedTextColor = selectedTextColor
        }

    // 是否循环滚动
    var cyclic: Boolean = provinceWv.cyclic
        set(value) {
            field = value
            provinceWv.cyclic = cyclic
            cityWv.cyclic = cyclic
            countyWv.cyclic = cyclic
        }

    // 行间距
    var lineSpacing: Int = provinceWv.lineSpacing
        set(value) {
            field = value
            provinceWv.lineSpacing = lineSpacing
            cityWv.lineSpacing = lineSpacing
            countyWv.lineSpacing = lineSpacing
        }

    // 滚动音效
    var soundEffect: Boolean = provinceWv.soundEffect
        set(value) {
            field = value
            provinceWv.soundEffect = soundEffect
            cityWv.soundEffect = soundEffect
            countyWv.soundEffect = soundEffect
        }

    // 滚动音效资源
    @RawRes
    var soundEffectResource: Int = provinceWv.soundEffectResource
        set(value) {
            field = value
            provinceWv.soundEffectResource = soundEffectResource
            cityWv.soundEffectResource = soundEffectResource
            countyWv.soundEffectResource = soundEffectResource
        }

    // 滚动音效播放音量
    @FloatRange(from = 0.0, to = 1.0)
    var playVolume: Float = provinceWv.playVolume
        set(value) {
            field = value
            provinceWv.playVolume = playVolume
            cityWv.playVolume = playVolume
            countyWv.playVolume = playVolume
        }

    // 是否显示分割线
    var showDivider: Boolean = provinceWv.showDivider
        set(value) {
            field = value
            provinceWv.showDivider = showDivider
            cityWv.showDivider = showDivider
            countyWv.showDivider = showDivider
        }

    // 分割线颜色
    @ColorInt
    var dividerColor: Int = provinceWv.dividerColor
        set(value) {
            field = value
            provinceWv.dividerColor = dividerColor
            cityWv.dividerColor = dividerColor
            countyWv.dividerColor = dividerColor
        }

    // 分割线高度
    var dividerHeight: Int = provinceWv.dividerHeight
        set(value) {
            field = value
            provinceWv.dividerHeight = dividerHeight
            cityWv.dividerHeight = dividerHeight
            countyWv.dividerHeight = dividerHeight
        }


    // 分割线类型
    @WheelView.DividerType
    var dividerType: Int = provinceWv.dividerType
        set(value) {
            field = value
            provinceWv.dividerType = dividerType
            cityWv.dividerType = dividerType
            countyWv.dividerType = dividerType
        }

    // 自适应分割线类型时的分割线内边距
    var dividerPaddingForWrap: Float = provinceWv.dividerPaddingForWrap
        set(value) {
            field = value
            provinceWv.dividerPaddingForWrap = dividerPaddingForWrap
            cityWv.dividerPaddingForWrap = dividerPaddingForWrap
            countyWv.dividerPaddingForWrap = dividerPaddingForWrap
        }

    // 是否绘制选中区域
    var drawSelectedRect: Boolean = provinceWv.drawSelectedRect
        set(value) {
            field = value
            provinceWv.drawSelectedRect = drawSelectedRect
            cityWv.drawSelectedRect = drawSelectedRect
            countyWv.drawSelectedRect = drawSelectedRect
        }

    // 选中区域颜色
    @ColorInt
    var selectedRectColor: Int = provinceWv.selectedRectColor
        set(value) {
            field = value
            provinceWv.selectedRectColor = selectedRectColor
            cityWv.selectedRectColor = selectedRectColor
            countyWv.selectedRectColor = selectedRectColor
        }

    // 是否开启弯曲效果
    var curved: Boolean = provinceWv.curved
        set(value) {
            field = value
            provinceWv.curved = curved
            cityWv.curved = curved
            countyWv.curved = curved
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @WheelView.CurvedArc
    var curvedArc: Int = provinceWv.curvedArc
        set(value) {
            field = value
            provinceWv.curvedArc = curvedArc
            cityWv.curvedArc = curvedArc
            countyWv.curvedArc = curvedArc
        }

    // 弯曲（3D）效果左右圆弧效果方向
    @FloatRange(from = 0.0, to = 1.0)
    var curvedArcFactor: Float = provinceWv.curvedArcFactor
        set(value) {
            field = value
            provinceWv.curvedArcFactor = curvedArcFactor
            cityWv.curvedArcFactor = curvedArcFactor
            countyWv.curvedArcFactor = curvedArcFactor
        }

    // 选中条目折射偏移比例
    @FloatRange(from = 0.0, to = 1.0)
    var refractRatio: Float = provinceWv.refractRatio
        set(value) {
            field = value
            provinceWv.refractRatio = refractRatio
            cityWv.refractRatio = refractRatio
            countyWv.refractRatio = refractRatio
        }

}