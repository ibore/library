//package me.ibore.dialog
//
//import android.graphics.drawable.Drawable
//import android.graphics.drawable.GradientDrawable
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
//import android.widget.ImageView
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
//import com.bumptech.glide.request.RequestOptions
//import me.ibore.R
//import me.ibore.builder.DialogBuilder
//import me.ibore.utils.UIUtils
//
//class DialogImageBody : DialogBuilder<DialogImageBody>() {
//
//    private var imageUrl: String? = null
//    private var placeholderRes: Int = 0
//    private var placeholder: Drawable? = null
//    private var errorRes: Int = 0
//    private var error: Drawable? = null
//
//    override fun builder(target: DialogView): View {
//        val bodyView = LinearLayout(target.requireContext())
//        val imageView = ImageView(target.requireContext())
//        val drawable = GradientDrawable()
//        drawable.cornerRadii = getCornerRadii(target.requireContext(), topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius)
//        drawable.setColor(ContextCompat.getColor(target.requireContext(), bgColor))
//        imageView.background = drawable
//
//        val imageLayoutParams = LinearLayout.LayoutParams(width, height)
//        imageLayoutParams.gravity = Gravity.CENTER
//        bodyView.addView(imageView, imageLayoutParams)
//
//        val tempPlaceholder: Drawable? = placeholder ?: if (placeholderRes != 0)
//            ContextCompat.getDrawable(target.requireContext(), placeholderRes) else null
//
//        val tempError: Drawable? = error ?: if (errorRes != 0)
//            ContextCompat.getDrawable(target.requireContext(), errorRes) else null
//
//        Glide.with(target).load(imageUrl).apply(RequestOptions().transform(GranularRoundedCorners(
//                dp2px(target.requireContext(), topStartRadius).toFloat(),
//                dp2px(target.requireContext(), topEndRadius).toFloat(),
//                dp2px(target.requireContext(), bottomEndRadius).toFloat(),
//                dp2px(target.requireContext(), bottomStartRadius).toFloat())))
//                .placeholder(tempPlaceholder)
//                .error(tempError)
//                .into(imageView)
//
//        val closeView = ImageView(target.requireContext())
//        closeView.setImageResource(R.drawable.dialog_image_close)
//        val closeLayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//        closeLayoutParams.gravity = Gravity.CENTER
//        bodyView.addView(closeView, closeLayoutParams)
//        return bodyView
//    }
//
//
//}