package me.ibore.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import me.ibore.widget.wheel.WheelView

@Parcelize
data class RegionModel(val code: String, val name: String) : WheelView.IWheelEntity, Parcelable {

    @IgnoredOnParcel
    override val wheelText: String = name

    override fun toString(): String {
        return "RegionModel(code='$code', name='$name')"
    }
}
