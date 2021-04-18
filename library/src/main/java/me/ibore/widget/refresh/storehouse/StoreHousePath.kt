package me.ibore.widget.refresh.storehouse

import android.util.SparseArray
import java.util.*

object StoreHousePath {

    private var sPointList: SparseArray<FloatArray>? = null

    fun addChar(c: Char, points: FloatArray) {
        sPointList!!.append(c.toInt(), points)
    }

    fun getPath(str: String): ArrayList<FloatArray> {
        return getPath(str, 1f, 14)
    }

    /**
     * @param str
     * @param scale
     * @param gapBetweenLetter
     * @return ArrayList of float[] {x1, y1, x2, y2}
     */
    fun getPath(str: String, scale: Float, gapBetweenLetter: Int): ArrayList<FloatArray> {
        val list = ArrayList<FloatArray>()
        var offsetForWidth = 0f
        for (element in str) {
            val pos = element.toInt()
            val key = sPointList!!.indexOfKey(pos)
            if (key == -1) {
                continue
            }
            val points = sPointList!![pos]
            val pointCount = points.size / 4
            for (j in 0 until pointCount) {
                val line = FloatArray(4)
                for (k in 0..3) {
                    val l = points[j * 4 + k]
                    // x
                    if (k % 2 == 0) {
                        line[k] = (l + offsetForWidth) * scale
                    } else {
                        line[k] = l * scale
                    }
                }
                list.add(line)
            }
            offsetForWidth += 57 + gapBetweenLetter.toFloat()
        }
        return list
    }

    init {
        sPointList = SparseArray()
        val LETTERS = arrayOf(floatArrayOf(24f, 0f, 1f, 22f, 1f, 22f, 1f, 72f, 24f, 0f, 47f, 22f, 47f, 22f, 47f, 72f, 1f, 48f, 47f, 48f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 37f, 0f, 37f, 0f, 47f, 11f, 47f, 11f, 47f, 26f, 47f, 26f, 38f, 36f, 38f, 36f, 0f, 36f, 38f, 36f, 47f, 46f, 47f, 46f, 47f, 61f, 47f, 61f, 38f, 71f, 37f, 72f, 0f, 72f), floatArrayOf(47f, 0f, 0f, 0f, 0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 24f, 0f, 24f, 0f, 47f, 22f, 47f, 22f, 47f, 48f, 47f, 48f, 23f, 72f, 23f, 72f, 0f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 47f, 0f, 0f, 36f, 37f, 36f, 0f, 72f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 47f, 0f, 0f, 36f, 37f, 36f), floatArrayOf(47f, 23f, 47f, 0f, 47f, 0f, 0f, 0f, 0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f, 47f, 72f, 47f, 48f, 47f, 48f, 24f, 48f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 36f, 47f, 36f, 47f, 0f, 47f, 72f), floatArrayOf(0f, 0f, 47f, 0f, 24f, 0f, 24f, 72f, 0f, 72f, 47f, 72f), floatArrayOf(47f, 0f, 47f, 72f, 47f, 72f, 24f, 72f, 24f, 72f, 0f, 48f), floatArrayOf(0f, 0f, 0f, 72f, 47f, 0f, 3f, 33f, 3f, 38f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 24f, 23f, 24f, 23f, 47f, 0f, 47f, 0f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 47f, 72f, 47f, 72f, 47f, 0f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f, 47f, 72f, 47f, 0f, 47f, 0f, 0f, 0f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 47f, 0f, 47f, 0f, 47f, 36f, 47f, 36f, 0f, 36f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 23f, 72f, 23f, 72f, 47f, 48f, 47f, 48f, 47f, 0f, 47f, 0f, 0f, 0f, 24f, 28f, 47f, 71f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 0f, 47f, 0f, 47f, 0f, 47f, 36f, 47f, 36f, 0f, 36f, 0f, 37f, 47f, 72f), floatArrayOf(47f, 0f, 0f, 0f, 0f, 0f, 0f, 36f, 0f, 36f, 47f, 36f, 47f, 36f, 47f, 72f, 47f, 72f, 0f, 72f), floatArrayOf(0f, 0f, 47f, 0f, 24f, 0f, 24f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f, 47f, 72f, 47f, 0f), floatArrayOf(0f, 0f, 24f, 72f, 24f, 72f, 47f, 0f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 24f, 49f, 24f, 49f, 47f, 72f, 47f, 72f, 47f, 0f), floatArrayOf(0f, 0f, 47f, 72f, 47f, 0f, 0f, 72f), floatArrayOf(0f, 0f, 24f, 23f, 47f, 0f, 24f, 23f, 24f, 23f, 24f, 72f), floatArrayOf(0f, 0f, 47f, 0f, 47f, 0f, 0f, 72f, 0f, 72f, 47f, 72f))
        val NUMBERS = arrayOf(floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f, 47f, 72f, 47f, 0f, 47f, 0f, 0f, 0f), floatArrayOf(24f, 0f, 24f, 72f), floatArrayOf(0f, 0f, 47f, 0f, 47f, 0f, 47f, 36f, 47f, 36f, 0f, 36f, 0f, 36f, 0f, 72f, 0f, 72f, 47f, 72f), floatArrayOf(0f, 0f, 47f, 0f, 47f, 0f, 47f, 36f, 47f, 36f, 0f, 36f, 47f, 36f, 47f, 72f, 47f, 72f, 0f, 72f), floatArrayOf(0f, 0f, 0f, 36f, 0f, 36f, 47f, 36f, 47f, 0f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 36f, 0f, 36f, 47f, 36f, 47f, 36f, 47f, 72f, 47f, 72f, 0f, 72f, 0f, 0f, 47f, 0f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f, 47f, 72f, 47f, 36f, 47f, 36f, 0f, 36f), floatArrayOf(0f, 0f, 47f, 0f, 47f, 0f, 47f, 72f), floatArrayOf(0f, 0f, 0f, 72f, 0f, 72f, 47f, 72f, 47f, 72f, 47f, 0f, 47f, 0f, 0f, 0f, 0f, 36f, 47f, 36f), floatArrayOf(47f, 0f, 0f, 0f, 0f, 0f, 0f, 36f, 0f, 36f, 47f, 36f, 47f, 0f, 47f, 72f))
        // A - Z
        for (i in LETTERS.indices) {
            sPointList!!.append(i + 65, LETTERS[i])
        }
        // a - z
        for (i in LETTERS.indices) {
            sPointList!!.append(i + 65 + 32, LETTERS[i])
        }
        // 0 - 9
        for (i in NUMBERS.indices) {
            sPointList!!.append(i + 48, NUMBERS[i])
        }
        // blank
        addChar(' ', floatArrayOf())
        // -
        addChar('-', floatArrayOf(0f, 36f, 47f, 36f))
        // .
        addChar('.', floatArrayOf(24f, 60f, 24f, 72f))
    }
}