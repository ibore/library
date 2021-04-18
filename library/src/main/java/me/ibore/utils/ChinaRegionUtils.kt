package me.ibore.utils

import com.google.gson.internal.LinkedTreeMap
import me.ibore.model.RegionModel
import java.io.File
import java.io.FileReader

object ChinaRegionUtils {

    const val CHINA_REGION: String = "china_region.json"
    private var CHINA_REGION_LIST: LinkedTreeMap<String, String>? = null

    private fun getRegionMap(): LinkedTreeMap<String, String> {
        if (null != CHINA_REGION_LIST) return CHINA_REGION_LIST!!
        val file = File(Utils.app.filesDir, CHINA_REGION)
        CHINA_REGION_LIST = if (FileUtils.isFileExists(file)) {
            GsonUtils.fromJson(FileReader(file), GsonUtils.getMapType(String::class.java, String::class.java))
        } else {
            GsonUtils.fromJson(ResourceUtils.readAssets2String(CHINA_REGION), GsonUtils.getMapType(String::class.java, String::class.java))
        }
        return CHINA_REGION_LIST!!
    }

    fun getRegionModel(code: String) : RegionModel{
        return RegionModel(code, getRegionMap()[code]!!)
    }

    fun getProvinceList(): MutableList<RegionModel> {
        val provinceModels: MutableList<RegionModel> = ArrayList()
        for (key in getRegionMap().keys) {
            if (key.endsWith("0000")) {
                provinceModels.add(RegionModel(key, getRegionMap()[key]!!))
            }
        }
        return provinceModels
    }

    fun getCityList(provinceCode: String): MutableList<RegionModel> {
        val startWith = provinceCode.substring(0, 2)
        val cityModels: MutableList<RegionModel> = ArrayList()
        for (key in getRegionMap().keys) {
            if (key.startsWith(startWith) && key.endsWith("00")
                    && !key.endsWith("0000")) {
                cityModels.add(RegionModel(key, getRegionMap()[key]!!))
            }
        }
        if (cityModels.isEmpty()) {
            cityModels.add(RegionModel(provinceCode, getRegionMap()[provinceCode]!!))
        }
        return cityModels
    }

    fun getCountyList(cityCode: String): MutableList<RegionModel> {
        val startWith = if (cityCode.endsWith("0000")) {
            cityCode.substring(0, 2) + "01"
        } else {
            cityCode.substring(0, 4)
        }
        val countyModels: MutableList<RegionModel> = ArrayList()
        for (key in getRegionMap().keys) {
            if (key.startsWith(startWith) && !key.endsWith("00")) {
                countyModels.add(RegionModel(key, getRegionMap()[key]!!))
            }
        }
        return countyModels
    }



}