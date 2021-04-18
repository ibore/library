package me.ibore.image.loader

import android.widget.ImageView

object ImageLoader {

    fun with() {

    }

    class Builder {

        var url : String? = null
        var view: ImageView? = null
        var placeHolder : Int = -1
        var errorHodler : Int = -1


    }


}