package me.ibore.qrcode

import android.graphics.PointF

data class ScanResult(val result: String?, val resultPoints: Array<PointF>? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanResult

        if (result != other.result) return false
        if (resultPoints != null) {
            if (other.resultPoints == null) return false
            if (!resultPoints.contentEquals(other.resultPoints)) return false
        } else if (other.resultPoints != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = result?.hashCode() ?: 0
        result1 = 31 * result1 + (resultPoints?.contentHashCode() ?: 0)
        return result1
    }
}