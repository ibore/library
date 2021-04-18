package me.ibore.update

import android.os.Parcel
import android.os.Parcelable

data class Update(val isForceUpdate: Boolean,
                  val apkUrl: String,
                  val saveApkPath: String,
                  val apkName: String,
                  val desc: String,
                  val packageName: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isForceUpdate) 1 else 0)
        parcel.writeString(apkUrl)
        parcel.writeString(saveApkPath)
        parcel.writeString(apkName)
        parcel.writeString(desc)
        parcel.writeString(packageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Update> {
        override fun createFromParcel(parcel: Parcel): Update {
            return Update(parcel)
        }

        override fun newArray(size: Int): Array<Update?> {
            return arrayOfNulls(size)
        }
    }
}