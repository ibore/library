package me.ibore.utils

import java.util.*

/**
 * <pre>
 * author: blankj
 * blog  : http://blankj.com
 * time  : 2019/08/10
 * desc  : utils about array
</pre> *
 */
object ArrayUtils {

    const val INDEX_NOT_FOUND = -1

    /**
     * Returns a new array only of those given elements.
     *
     * @param array The array.
     * @return a new array only of those given elements.
     */
    @SafeVarargs
    fun <T> newArray(vararg array: T): Array<out T> {
        return array
    }

    fun newLongArray(vararg array: Long): LongArray {
        return array
    }

    fun newIntArray(vararg array: Int): IntArray {
        return array
    }

    fun newShortArray(vararg array: Short): ShortArray {
        return array
    }

    fun newCharArray(vararg array: Char): CharArray {
        return array
    }

    fun newByteArray(vararg array: Byte): ByteArray {
        return array
    }

    fun newDoubleArray(vararg array: Double): DoubleArray {
        return array
    }

    fun newFloatArray(vararg array: Float): FloatArray {
        return array
    }

    fun newBooleanArray(vararg array: Boolean): BooleanArray {
        return array
    }

    /**
     * Return the array is empty.
     *
     * @param array The array.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isEmpty(array: Any?): Boolean {
        return getLength(array) == 0
    }

    /**
     * Return the size of array.
     *
     * @param array The array.
     * @return the size of array
     */
    fun getLength(array: Any?): Int {
        return if (array == null) 0 else java.lang.reflect.Array.getLength(array)
    }

    fun isSameLength(array1: Any?, array2: Any?): Boolean {
        return getLength(array1) == getLength(array2)
    }
    /**
     * Get the value of the specified index of the array.
     *
     * @param array        The array.
     * @param index        The index into the array.
     * @param defaultValue The default value.
     * @return the value of the specified index of the array
     */
    @JvmOverloads
    operator fun get(array: Any?, index: Int, defaultValue: Any? = null): Any? {
        return if (array == null) defaultValue else try {
            java.lang.reflect.Array.get(array, index)
        } catch (ignore: Exception) {
            defaultValue
        }
    }

    /**
     * Set the value of the specified index of the array.
     *
     * @param array The array.
     * @param index The index into the array.
     * @param value The new value of the indexed component.
     */
    operator fun set(array: Any?, index: Int, value: Any?) {
        if (array == null) return
        java.lang.reflect.Array.set(array, index, value)
    }

    /**
     * Return whether the two arrays are equals.
     *
     * @param a  One array.
     * @param a2 The other array.
     * @return `true`: yes<br></br>`false`: no
     */
    fun equals(a: Array<Any?>?, a2: Array<Any?>?): Boolean {
        return Arrays.deepEquals(a, a2)
    }

    fun equals(a: BooleanArray?, a2: BooleanArray?): Boolean {
        return Arrays.equals(a, a2)
    }

    fun equals(a: ByteArray?, a2: ByteArray?): Boolean {
        return Arrays.equals(a, a2)
    }

    fun equals(a: CharArray?, a2: CharArray?): Boolean {
        return Arrays.equals(a, a2)
    }

    fun equals(a: DoubleArray?, a2: DoubleArray?): Boolean {
        return Arrays.equals(a, a2)
    }

    fun equals(a: FloatArray?, a2: FloatArray?): Boolean {
        return Arrays.equals(a, a2)
    }

    fun equals(a: IntArray?, a2: IntArray?): Boolean {
        return Arrays.equals(a, a2)
    }

    fun equals(a: ShortArray?, a2: ShortArray?): Boolean {
        return Arrays.equals(a, a2)
    }
    ///////////////////////////////////////////////////////////////////////////
    // reverse
    ///////////////////////////////////////////////////////////////////////////
    /**
     *
     * Reverses the order of the given array.
     *
     *
     * There is no special handling for multi-dimensional arrays.
     *
     *
     * This method does nothing for a `null` input array.
     *
     * @param array the array to reverse, may be `null`
     */
    fun <T> reverse(array: Array<T>?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: T
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: LongArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Long
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: IntArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Int
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: ShortArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Short
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: CharArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Char
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: ByteArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Byte
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: DoubleArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Double
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: FloatArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Float
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }

    fun reverse(array: BooleanArray?) {
        if (array == null) {
            return
        }
        var i = 0
        var j = array.size - 1
        var tmp: Boolean
        while (j > i) {
            tmp = array[j]
            array[j] = array[i]
            array[i] = tmp
            j--
            i++
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // copy
    ///////////////////////////////////////////////////////////////////////////
    /**
     *
     * Copies the specified array and handling
     * `null`.
     *
     *
     * The objects in the array are not cloned, thus there is no special
     * handling for multi-dimensional arrays.
     *
     *
     * This method returns `null` for a `null` input array.
     *
     * @param array the array to shallow clone, may be `null`
     * @return the cloned array, `null` if `null` input
     */
    fun <T> copy(array: Array<T>?): Array<T>? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: LongArray?): LongArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: IntArray?): IntArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: ShortArray?): ShortArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: CharArray?): CharArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: ByteArray?): ByteArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: DoubleArray?): DoubleArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: FloatArray?): FloatArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    fun copy(array: BooleanArray?): BooleanArray? {
        return if (array == null) null else subArray(array, 0, array.size)
    }

    private fun realCopy(array: Any?): Any? {
        return if (array == null) null else realSubArray(
            array,
            0,
            getLength(array)
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // subArray
    ///////////////////////////////////////////////////////////////////////////
    @Suppress("UNCHECKED_CAST")
    fun <T> subArray(
        array: Array<T>?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): Array<T>? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as Array<T>?
    }

    fun subArray(
        array: LongArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): LongArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as LongArray?
    }

    fun subArray(
        array: IntArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): IntArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as IntArray?
    }

    fun subArray(
        array: ShortArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): ShortArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as ShortArray?
    }

    fun subArray(
        array: CharArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): CharArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as CharArray?
    }

    fun subArray(
        array: ByteArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): ByteArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as ByteArray?
    }

    fun subArray(
        array: DoubleArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): DoubleArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as DoubleArray?
    }

    fun subArray(
        array: FloatArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): FloatArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as FloatArray?
    }

    fun subArray(
        array: BooleanArray?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): BooleanArray? {
        return realSubArray(array, startIndexInclusive, endIndexExclusive) as BooleanArray?
    }

    private fun realSubArray(
        array: Any?,
        startIndexInclusive: Int,
        endIndexExclusive: Int
    ): Any? {
        var startIndexInclusiveTemp = startIndexInclusive
        var endIndexExclusiveTemp = endIndexExclusive
        if (array == null) {
            return null
        }
        if (startIndexInclusiveTemp < 0) {
            startIndexInclusiveTemp = 0
        }
        val length = getLength(array)
        if (endIndexExclusiveTemp > length) {
            endIndexExclusiveTemp = length
        }
        val newSize = endIndexExclusiveTemp - startIndexInclusiveTemp
        val type = array.javaClass.componentType!!
        if (newSize <= 0) {
            return java.lang.reflect.Array.newInstance(type, 0)
        }
        val subArray = java.lang.reflect.Array.newInstance(type, newSize)
        System.arraycopy(array, startIndexInclusiveTemp, subArray, 0, newSize)
        return subArray
    }
    ///////////////////////////////////////////////////////////////////////////
    // add
    ///////////////////////////////////////////////////////////////////////////
    /**
     *
     * Copies the given array and adds the given element at the end of the new array.
     *
     *
     * The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     *
     *
     * If the input array is `null`, a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.realAdd(null, null)      = [null]
     * ArrayUtils.realAdd(null, "a")       = ["a"]
     * ArrayUtils.realAdd(["a"], null)     = ["a", null]
     * ArrayUtils.realAdd(["a"], "b")      = ["a", "b"]
     * ArrayUtils.realAdd(["a", "b"], "c") = ["a", "b", "c"]
    </pre> *
     *
     * @param array   the array to "realAdd" the element to, may be `null`
     * @param element the object to realAdd
     * @return A new array containing the existing elements plus the new element
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> add(array: Array<T>?, element: T?): Array<T> {
        val type = array?.javaClass ?: ((element as Class<*>?)?.javaClass ?: Any::class.java)
        return realAddOne(array, element, type) as Array<T>
    }

    fun add(array: BooleanArray?, element: Boolean): BooleanArray {
        return realAddOne(array, element, java.lang.Boolean.TYPE) as BooleanArray
    }

    fun add(array: ByteArray?, element: Byte): ByteArray {
        return realAddOne(array, element, java.lang.Byte.TYPE) as ByteArray
    }

    fun add(array: CharArray?, element: Char): CharArray {
        return realAddOne(array, element, Character.TYPE) as CharArray
    }

    fun add(array: DoubleArray?, element: Double): DoubleArray {
        return realAddOne(array, element, java.lang.Double.TYPE) as DoubleArray
    }

    fun add(array: FloatArray?, element: Float): FloatArray {
        return realAddOne(array, element, java.lang.Float.TYPE) as FloatArray
    }

    fun add(array: IntArray?, element: Int): IntArray {
        return realAddOne(array, element, Integer.TYPE) as IntArray
    }

    fun add(array: LongArray?, element: Long): LongArray {
        return realAddOne(array, element, java.lang.Long.TYPE) as LongArray
    }

    fun add(array: ShortArray?, element: Short): ShortArray {
        return realAddOne(array, element, java.lang.Short.TYPE) as ShortArray
    }

    private fun realAddOne(array: Any?, element: Any?, newArrayComponentType: Class<*>): Any {
        val newArray: Any
        var arrayLength = 0
        if (array != null) {
            arrayLength = getLength(array)
            newArray = java.lang.reflect.Array.newInstance(
                array.javaClass.componentType!!,
                arrayLength + 1
            )
            System.arraycopy(array, 0, newArray, 0, arrayLength)
        } else {
            newArray = java.lang.reflect.Array.newInstance(newArrayComponentType, 1)
        }
        java.lang.reflect.Array.set(newArray, arrayLength, element)
        return newArray
    }

    /**
     *
     * Adds all the elements of the given arrays into a new array.
     *
     * The new array contains all of the element of `array1` followed
     * by all of the elements `array2`. When an array is returned, it is always
     * a new array.
     *
     * <pre>
     * ArrayUtils.add(null, null)     = null
     * ArrayUtils.add(array1, null)   = copy of array1
     * ArrayUtils.add(null, array2)   = copy of array2
     * ArrayUtils.add([], [])         = []
     * ArrayUtils.add([null], [null]) = [null, null]
     * ArrayUtils.add(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
    </pre> *
     *
     * @param array1 the first array whose elements are added to the new array, may be `null`
     * @param array2 the second array whose elements are added to the new array, may be `null`
     * @return The new array, `null` if `null` array inputs.
     * The type of the new array is the type of the first array.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> add(array1: Array<T>?, array2: Array<T>?): Array<T>? {
        return realAddArr(array1, array2) as Array<T>?
    }

    fun add(array1: BooleanArray?, array2: BooleanArray?): BooleanArray? {
        return realAddArr(array1, array2) as BooleanArray?
    }

    fun add(array1: CharArray?, array2: CharArray?): CharArray? {
        return realAddArr(array1, array2) as CharArray?
    }

    fun add(array1: ByteArray?, array2: ByteArray?): ByteArray? {
        return realAddArr(array1, array2) as ByteArray?
    }

    fun add(array1: ShortArray?, array2: ShortArray?): ShortArray? {
        return realAddArr(array1, array2) as ShortArray?
    }

    fun add(array1: IntArray?, array2: IntArray?): IntArray? {
        return realAddArr(array1, array2) as IntArray?
    }

    fun add(array1: LongArray?, array2: LongArray?): LongArray? {
        return realAddArr(array1, array2) as LongArray?
    }

    fun add(array1: FloatArray?, array2: FloatArray?): FloatArray? {
        return realAddArr(array1, array2) as FloatArray?
    }

    fun add(array1: DoubleArray?, array2: DoubleArray?): DoubleArray? {
        return realAddArr(array1, array2) as DoubleArray?
    }

    private fun realAddArr(array1: Any?, array2: Any?): Any? {
        if (array1 == null && array2 == null) return null
        if (array1 == null) {
            return realCopy(array2)
        }
        if (array2 == null) {
            return realCopy(array1)
        }
        val len1 = getLength(array1)
        val len2 = getLength(array2)
        val joinedArray =
            java.lang.reflect.Array.newInstance(array1.javaClass.componentType!!, len1 + len2)
        System.arraycopy(array1, 0, joinedArray, 0, len1)
        System.arraycopy(array2, 0, joinedArray, len1, len2)
        return joinedArray
    }

    /**
     *
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     *
     *
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     *
     *
     * If the input array is `null`, a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0, null)      = [null]
     * ArrayUtils.add(null, 0, "a")       = ["a"]
     * ArrayUtils.add(["a"], 1, null)     = ["a", null]
     * ArrayUtils.add(["a"], 1, "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], 3, "c") = ["a", "b", "c"]
    </pre> *
     *
     * @param array1 the array to realAdd the element to, may be `null`
     * @param index  the position of the new object
     * @param array2 the array to realAdd
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> add(array1: Array<T>?, index: Int, array2: Array<T>?): Array<T?>? {
        val clss: Class<*> = when {
            array1 != null -> {
                array1.javaClass.componentType!!
            }
            array2 != null -> {
                array2.javaClass.componentType!!
            }
            else -> {
                return arrayOf<Any?>(null) as Array<T?>
            }
        }
        return realAddArr(array1, index, array2, clss) as Array<T?>?
    }

    fun add(array1: BooleanArray?, index: Int, array2: BooleanArray?): BooleanArray? {
        return realAddArr(array1, index, array2, java.lang.Boolean.TYPE) as BooleanArray?
    }

    fun add(array1: CharArray?, index: Int, array2: CharArray?): CharArray? {
        return realAddArr(array1, index, array2, Character.TYPE) as CharArray?
    }

    fun add(array1: ByteArray?, index: Int, array2: ByteArray?): ByteArray? {
        return realAddArr(array1, index, array2, java.lang.Byte.TYPE) as ByteArray?
    }

    fun add(array1: ShortArray?, index: Int, array2: ShortArray?): ShortArray? {
        return realAddArr(array1, index, array2, java.lang.Short.TYPE) as ShortArray?
    }

    fun add(array1: IntArray?, index: Int, array2: IntArray?): IntArray? {
        return realAddArr(array1, index, array2, Integer.TYPE) as IntArray?
    }

    fun add(array1: LongArray?, index: Int, array2: LongArray?): LongArray? {
        return realAddArr(array1, index, array2, java.lang.Long.TYPE) as LongArray?
    }

    fun add(array1: FloatArray?, index: Int, array2: FloatArray?): FloatArray? {
        return realAddArr(array1, index, array2, java.lang.Float.TYPE) as FloatArray?
    }

    fun add(array1: DoubleArray?, index: Int, array2: DoubleArray?): DoubleArray? {
        return realAddArr(array1, index, array2, java.lang.Double.TYPE) as DoubleArray?
    }

    private fun realAddArr(array1: Any?, index: Int, array2: Any?, clss: Class<*>): Any? {
        if (array1 == null && array2 == null) return null
        val len1 = getLength(array1)
        val len2 = getLength(array2)
        if (len1 == 0) {
            if (index != 0) {
                throw IndexOutOfBoundsException("Index: $index, array1 Length: 0")
            }
            return realCopy(array2)
        }
        if (len2 == 0) {
            return realCopy(array1)
        }
        if (index > len1 || index < 0) {
            throw IndexOutOfBoundsException("Index: $index, array1 Length: $len1")
        }
        val joinedArray =
            java.lang.reflect.Array.newInstance(array1!!.javaClass.componentType!!, len1 + len2)
        when (index) {
            len1 -> {
                System.arraycopy(array1, 0, joinedArray, 0, len1)
                System.arraycopy(array2!!, 0, joinedArray, len1, len2)
            }
            0 -> {
                System.arraycopy(array2!!, 0, joinedArray, 0, len2)
                System.arraycopy(array1, 0, joinedArray, len2, len1)
            }
            else -> {
                System.arraycopy(array1, 0, joinedArray, 0, index)
                System.arraycopy(array2!!, 0, joinedArray, index, len2)
                System.arraycopy(array1, index, joinedArray, index + len2, len1 - index)
            }
        }
        return joinedArray
    }

    /**
     *
     * Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     *
     *
     * This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     *
     *
     * If the input array is `null`, a new one element array is returned
     * whose component type is the same as the element.
     *
     * <pre>
     * ArrayUtils.add(null, 0, null)      = [null]
     * ArrayUtils.add(null, 0, "a")       = ["a"]
     * ArrayUtils.add(["a"], 1, null)     = ["a", null]
     * ArrayUtils.add(["a"], 1, "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], 3, "c") = ["a", "b", "c"]
    </pre> *
     *
     * @param array   the array to realAdd the element to, may be `null`
     * @param index   the position of the new object
     * @param element the object to realAdd
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> add(array: Array<T>?, index: Int, element: T?): Array<T?> {
        val clss: Class<*> = if (array != null) {
            array.javaClass.componentType!!
        } else (element as Class<*>?)?.javaClass ?: return arrayOf<Any?>(null) as Array<T?>
        return realAdd(array, index, element, clss) as Array<T?>
    }

    fun add(array: BooleanArray?, index: Int, element: Boolean): BooleanArray {
        return realAdd(array, index, element, java.lang.Boolean.TYPE) as BooleanArray
    }

    fun add(array: CharArray?, index: Int, element: Char): CharArray {
        return realAdd(array, index, element, Character.TYPE) as CharArray
    }

    fun add(array: ByteArray?, index: Int, element: Byte): ByteArray {
        return realAdd(array, index, element, java.lang.Byte.TYPE) as ByteArray
    }

    fun add(array: ShortArray?, index: Int, element: Short): ShortArray {
        return realAdd(array, index, element, java.lang.Short.TYPE) as ShortArray
    }

    fun add(array: IntArray?, index: Int, element: Int): IntArray {
        return realAdd(array, index, element, Integer.TYPE) as IntArray
    }

    fun add(array: LongArray?, index: Int, element: Long): LongArray {
        return realAdd(array, index, element, java.lang.Long.TYPE) as LongArray
    }

    fun add(array: FloatArray?, index: Int, element: Float): FloatArray {
        return realAdd(array, index, element, java.lang.Float.TYPE) as FloatArray
    }

    fun add(array: DoubleArray?, index: Int, element: Double): DoubleArray {
        return realAdd(array, index, element, java.lang.Double.TYPE) as DoubleArray
    }

    private fun realAdd(array: Any?, index: Int, element: Any?, clss: Class<*>): Any {
        if (array == null) {
            if (index != 0) {
                throw IndexOutOfBoundsException("Index: $index, Length: 0")
            }
            val joinedArray = java.lang.reflect.Array.newInstance(clss, 1)
            java.lang.reflect.Array.set(joinedArray, 0, element)
            return joinedArray
        }
        val length = java.lang.reflect.Array.getLength(array)
        if (index > length || index < 0) {
            throw IndexOutOfBoundsException("Index: $index, Length: $length")
        }
        val result = java.lang.reflect.Array.newInstance(clss, length + 1)
        System.arraycopy(array, 0, result, 0, index)
        java.lang.reflect.Array.set(result, index, element)
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index)
        }
        return result
    }
    ///////////////////////////////////////////////////////////////////////////
    // remove
    ///////////////////////////////////////////////////////////////////////////
    /**
     *
     * Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).
     *
     *
     * This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.
     *
     *
     * If the input array is `null`, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     *
     * <pre>
     * ArrayUtils.remove(["a"], 0)           = []
     * ArrayUtils.remove(["a", "b"], 0)      = ["b"]
     * ArrayUtils.remove(["a", "b"], 1)      = ["a"]
     * ArrayUtils.remove(["a", "b", "c"], 1) = ["a", "c"]
    </pre> *
     *
     * @param array the array to remove the element from, may not be `null`
     * @param index the position of the element to be removed
     * @return A new array containing the existing elements except the element
     * at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is `null`.
     * @since 2.1
     */
    fun remove(array: Array<Any?>, index: Int): Array<Any?> {
        return remove(array as Any, index) as Array<Any?>
    }

    /**
     *
     * Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.
     *
     *
     * This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.
     *
     * <pre>
     * ArrayUtils.removeElement(null, "a")            = null
     * ArrayUtils.removeElement([], "a")              = []
     * ArrayUtils.removeElement(["a"], "b")           = ["a"]
     * ArrayUtils.removeElement(["a", "b"], "a")      = ["b"]
     * ArrayUtils.removeElement(["a", "b", "a"], "a") = ["b", "a"]
    </pre> *
     *
     * @param array   the array to remove the element from, may be `null`
     * @param element the element to be removed
     * @return A new array containing the existing elements except the first
     * occurrence of the specified element.
     * @since 2.1
     */
    fun removeElement(array: Array<Any?>, element: Any?): Array<Any?>? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    fun remove(array: BooleanArray, index: Int): BooleanArray {
        return remove(array as Any, index) as BooleanArray
    }

    fun removeElement(array: BooleanArray, element: Boolean): BooleanArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    fun remove(array: ByteArray, index: Int): ByteArray {
        return remove(array as Any, index) as ByteArray
    }

    fun removeElement(array: ByteArray, element: Byte): ByteArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    fun remove(array: CharArray, index: Int): CharArray {
        return remove(array as Any, index) as CharArray
    }

    fun removeElement(array: CharArray, element: Char): CharArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    fun remove(array: DoubleArray, index: Int): DoubleArray {
        return remove(array as Any, index) as DoubleArray
    }

    fun removeElement(array: DoubleArray, element: Double): DoubleArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    fun remove(array: FloatArray, index: Int): FloatArray {
        return remove(array as Any, index) as FloatArray
    }

    fun removeElement(array: FloatArray, element: Float): FloatArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    fun remove(array: IntArray, index: Int): IntArray {
        return remove(array as Any, index) as IntArray
    }

    fun removeElement(array: IntArray, element: Int): IntArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(
            array,
            index
        )
    }

    fun remove(array: LongArray, index: Int): LongArray {
        return remove(array as Any, index) as LongArray
    }

    fun removeElement(array: LongArray, element: Long): LongArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(
            array,
            index
        )
    }

    fun remove(array: ShortArray, index: Int): ShortArray {
        return remove(array as Any, index) as ShortArray
    }

    fun removeElement(array: ShortArray, element: Short): ShortArray? {
        val index = indexOf(array, element)
        return if (index == INDEX_NOT_FOUND) {
            copy(array)
        } else remove(array, index)
    }

    private fun remove(array: Any, index: Int): Any {
        val length = getLength(array)
        if (index < 0 || index >= length) {
            throw IndexOutOfBoundsException("Index: $index, Length: $length")
        }
        val result =
            java.lang.reflect.Array.newInstance(array.javaClass.componentType, length - 1)
        System.arraycopy(array, 0, result, 0, index)
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1)
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // object indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: Array<Any?>?, objectToFind: Any?, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        if (objectToFind == null) {
            for (i in startIndexTemp until array.size) {
                if (array[i] == null) {
                    return i
                }
            }
        } else {
            for (i in startIndexTemp until array.size) {
                if (objectToFind == array[i]) {
                    return i
                }
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: Array<Any?>?,
        objectToFind: Any?,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        if (objectToFind == null) {
            for (i in startIndexTemp downTo 0) {
                if (array[i] == null) {
                    return i
                }
            }
        } else {
            for (i in startIndexTemp downTo 0) {
                if (objectToFind == array[i]) {
                    return i
                }
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: Array<Any?>?, objectToFind: Any?): Boolean {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // long indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: LongArray?, valueToFind: Long, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: LongArray?,
        valueToFind: Long,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: LongArray?, valueToFind: Long): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // int indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: IntArray?, valueToFind: Int, startIndex: Int = 0): Int {
        var startIndexTemp= startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(array: IntArray?, valueToFind: Int, startIndex: Int = Int.MAX_VALUE): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: IntArray?, valueToFind: Int): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // short indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: ShortArray?, valueToFind: Short, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: ShortArray?,
        valueToFind: Short,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: ShortArray?, valueToFind: Short): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // char indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: CharArray?, valueToFind: Char, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: CharArray?,
        valueToFind: Char,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: CharArray?, valueToFind: Char): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // byte indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: ByteArray?, valueToFind: Byte, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: ByteArray?,
        valueToFind: Byte,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (array == null) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: ByteArray?, valueToFind: Byte): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    fun indexOf(array: DoubleArray, valueToFind: Double, tolerance: Double): Int {
        return indexOf(array, valueToFind, 0, tolerance)
    }

    ///////////////////////////////////////////////////////////////////////////
    // double indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: DoubleArray, valueToFind: Double, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun indexOf(
        array: DoubleArray,
        valueToFind: Double,
        startIndex: Int,
        tolerance: Double
    ): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        val min = valueToFind - tolerance
        val max = valueToFind + tolerance
        for (i in startIndexTemp until array.size) {
            if (array[i] in min..max) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun lastIndexOf(array: DoubleArray, valueToFind: Double, tolerance: Double): Int {
        return lastIndexOf(array, valueToFind, Int.MAX_VALUE, tolerance)
    }

    @JvmOverloads
    fun lastIndexOf(
        array: DoubleArray,
        valueToFind: Double,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun lastIndexOf(
        array: DoubleArray,
        valueToFind: Double,
        startIndex: Int,
        tolerance: Double
    ): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        val min = valueToFind - tolerance
        val max = valueToFind + tolerance
        for (i in startIndexTemp downTo 0) {
            if (array[i] in min..max) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: DoubleArray, valueToFind: Double): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    fun contains(array: DoubleArray, valueToFind: Double, tolerance: Double): Boolean {
        return indexOf(array, valueToFind, 0, tolerance) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // float indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: FloatArray, valueToFind: Float, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: FloatArray,
        valueToFind: Float,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: FloatArray, valueToFind: Float): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // bool indexOf
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    fun indexOf(array: BooleanArray, valueToFind: Boolean, startIndex: Int = 0): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            startIndexTemp = 0
        }
        for (i in startIndexTemp until array.size) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    @JvmOverloads
    fun lastIndexOf(
        array: BooleanArray,
        valueToFind: Boolean,
        startIndex: Int = Int.MAX_VALUE
    ): Int {
        var startIndexTemp = startIndex
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND
        }
        if (startIndexTemp < 0) {
            return INDEX_NOT_FOUND
        } else if (startIndexTemp >= array.size) {
            startIndexTemp = array.size - 1
        }
        for (i in startIndexTemp downTo 0) {
            if (valueToFind == array[i]) {
                return i
            }
        }
        return INDEX_NOT_FOUND
    }

    fun contains(array: BooleanArray, valueToFind: Boolean): Boolean {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND
    }

    ///////////////////////////////////////////////////////////////////////////
    // char converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Char>?): CharArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return CharArray(0)
        }
        val result = CharArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Char?>?, valueForNull: Char): CharArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return CharArray(0)
        }
        val result = CharArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: CharArray?): Array<Char?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Char>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // long converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Long>?): LongArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return LongArray(0)
        }
        val result = LongArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Long?>?, valueForNull: Long): LongArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return LongArray(0)
        }
        val result = LongArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: LongArray?): Array<Long?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Long>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // int converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Int>?): IntArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return IntArray(0)
        }
        val result = IntArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Int?>?, valueForNull: Int): IntArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return IntArray(0)
        }
        val result = IntArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: IntArray?): Array<Int?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Int>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // short converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Short>?): ShortArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return ShortArray(0)
        }
        val result = ShortArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Short?>?, valueForNull: Short): ShortArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return ShortArray(0)
        }
        val result = ShortArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: ShortArray?): Array<Short?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Short>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // byte converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Byte>?): ByteArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return ByteArray(0)
        }
        val result = ByteArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Byte?>?, valueForNull: Byte): ByteArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return ByteArray(0)
        }
        val result = ByteArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: ByteArray?): Array<Byte?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Byte>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // double converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Double>?): DoubleArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return DoubleArray(0)
        }
        val result = DoubleArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Double?>?, valueForNull: Double): DoubleArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return DoubleArray(0)
        }
        val result = DoubleArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: DoubleArray?): Array<Double?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Double>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // float converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Float>?): FloatArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return FloatArray(0)
        }
        val result = FloatArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Float?>?, valueForNull: Float): FloatArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return FloatArray(0)
        }
        val result = FloatArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: FloatArray?): Array<Float?>? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Float>(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    ///////////////////////////////////////////////////////////////////////////
    // boolean converters
    ///////////////////////////////////////////////////////////////////////////
    fun toPrimitive(array: Array<Boolean>?): BooleanArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return BooleanArray(0)
        }
        val result = BooleanArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    fun toPrimitive(array: Array<Boolean?>?, valueForNull: Boolean): BooleanArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return BooleanArray(0)
        }
        val result = BooleanArray(array.size)
        for (i in array.indices) {
            val b = array[i]
            result[i] = b ?: valueForNull
        }
        return result
    }

    fun toObject(array: BooleanArray?): Array<Boolean?>? {
        if (array == null) {
            return null
        } else if (array.size == 0) {
            return arrayOfNulls(0)
        }
        val result = arrayOfNulls<Boolean>(array.size)
        for (i in array.indices) {
            result[i] = if (array[i]) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE
        }
        return result
    }

    fun <T> asList(vararg array: T): List<T> {
        return if (array == null || array.size == 0) {
            emptyList()
        } else Arrays.asList(*array)
    }

    fun <T> asUnmodifiableList(vararg array: T): List<T> {
        return Collections.unmodifiableList(asList(*array))
    }

    fun <T> asArrayList(vararg array: T): List<T> {
        val list: MutableList<T> = ArrayList()
        if (array == null || array.size == 0) return list
        list.addAll(Arrays.asList(*array))
        return list
    }

    fun <T> asLinkedList(vararg array: T): List<T> {
        val list: MutableList<T> = LinkedList()
        if (array == null || array.size == 0) return list
        list.addAll(Arrays.asList(*array))
        return list
    }

    fun <T> sort(array: Array<T>?, c: Comparator<in T>?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array, c)
    }

    fun sort(array: ByteArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    fun sort(array: CharArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    fun sort(array: DoubleArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    fun sort(array: FloatArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    fun sort(array: IntArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    fun sort(array: LongArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    fun sort(array: ShortArray?) {
        if (array == null || array.size < 2) return
        Arrays.sort(array)
    }

    /**
     * Executes the given closure on each element in the array.
     *
     *
     * If the input array or closure is null, there is no change made.
     *
     * @param array   The array.
     * @param closure the closure to perform, may be null
     */
    @Suppress("UNCHECKED_CAST")
    fun <E> forAllDo(array: Any?, closure: Closure<E>?) {
        if (array == null || closure == null) return
        when (array) {
            is Array<*> -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, ele as E)
                    i++
                }
            }
            is BooleanArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(
                        i,
                        (if (ele) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE) as E
                    )
                    i++
                }
            }
            is ByteArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, java.lang.Byte.valueOf(ele) as E)
                    i++
                }
            }
            is CharArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, Character.valueOf(ele) as E)
                    i++
                }
            }
            is ShortArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, ele as E)
                    i++
                }
            }
            is IntArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, Integer.valueOf(ele) as E)
                    i++
                }
            }
            is LongArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, java.lang.Long.valueOf(ele) as E)
                    i++
                }
            }
            is FloatArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, java.lang.Float.valueOf(ele) as E)
                    i++
                }
            }
            is DoubleArray -> {
                var i = 0
                val length = array.size
                while (i < length) {
                    val ele = array[i]
                    closure.execute(i, java.lang.Double.valueOf(ele) as E)
                    i++
                }
            }
            else -> {
                throw IllegalArgumentException("Not an array: " + array.javaClass)
            }
        }
    }

    /**
     * Return the string of array.
     *
     * @param array The array.
     * @return the string of array
     */
    fun toString(array: Any?): String {
        if (array == null) return "null"
        when (array) {
            is Array<*> -> {
                return Arrays.deepToString(array as Array<*>?)
            }
            is BooleanArray -> {
                return Arrays.toString(array as BooleanArray?)
            }
            is ByteArray -> {
                return Arrays.toString(array as ByteArray?)
            }
            is CharArray -> {
                return Arrays.toString(array as CharArray?)
            }
            is DoubleArray -> {
                return Arrays.toString(array as DoubleArray?)
            }
            is FloatArray -> {
                return Arrays.toString(array as FloatArray?)
            }
            is IntArray -> {
                return Arrays.toString(array as IntArray?)
            }
            is LongArray -> {
                return Arrays.toString(array as LongArray?)
            }
            is ShortArray -> {
                return Arrays.toString(array as ShortArray?)
            }
            else -> throw IllegalArgumentException("Array has incompatible type: " + array.javaClass)
        }
    }

    interface Closure<E> {
        fun execute(index: Int, item: E)
    }
}