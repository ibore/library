//package me.ibore.recycler.sticky
//
//import android.content.Context
//import android.graphics.Color
//import android.graphics.Typeface
//import android.util.AttributeSet
//import android.view.Gravity.CENTER_VERTICAL
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.annotation.Nullable
//import androidx.appcompat.widget.AppCompatTextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import me.ibore.R
//import me.ibore.holder.ViewHolder
//import me.ibore.recycler.adapter.ICommonAdapter
//import me.ibore.recycler.adapter.RecyclerAdapter
//import me.ibore.recycler.holder.RecyclerHolder
//import me.ibore.utils.UIUtils
//
//class StickyView @JvmOverloads constructor(context: Context, @Nullable private val attrs: AttributeSet? = null, private val defStyleAttr: Int = 0) :
//        FrameLayout(context, attrs, defStyleAttr) {
//
//    private var viewHolder: ViewHolder? = null
//    private var recyclerView: RecyclerView? = null
//    private var stickyId: Int
//    private var mLastPosition = -1
//    private var textSize: Float
//    private var textColor: Int
//    private var textBold: Boolean
//    private var textHeight: Int = 0
//    private var textBackground: Int
//    private var textGravity: Int
//    private var paddingVertical: Int
//    private var paddingHorizontal: Int
//
//    init {
//        val ta = context.obtainStyledAttributes(attrs, R.styleable.StickyView, defStyleAttr, 0)
//        stickyId = ta.getResourceId(R.styleable.StickyView_svStickyId, 0)
//        textSize = ta.getDimension(R.styleable.StickyView_svTextSize, 12F)
//        textColor = ta.getColor(R.styleable.StickyView_svTextColor, Color.DKGRAY)
//        textBold = ta.getBoolean(R.styleable.StickyView_svTextBold, false)
//        textHeight = ta.getDimensionPixelOffset(R.styleable.StickyView_svTextHeight, dp2px(context, 32F))
//        textBackground = ta.getResourceId(R.styleable.StickyView_svTextBackground, 0)
//        textGravity = ta.getInteger(R.styleable.StickyView_svTextGravity, 0)
//        paddingVertical = ta.getDimensionPixelOffset(R.styleable.StickyView_svPaddingVertical, 0)
//        paddingHorizontal = ta.getDimensionPixelOffset(R.styleable.StickyView_svPaddingHorizontal, 0)
//        ta.recycle()
//
//    }
//
//    private fun onBindStickyView(adapter: Adapter<*>, position: Int) {
//        if (mLastPosition != position) {
//            mLastPosition = position
//            for (index in position downTo 0) {
//                if (adapter.getData(index) is StickyModel) {
//                    val stickyModel = adapter.getData(index) as StickyModel
//                    if (stickyId == 0) (viewHolder!!.parent() as TextView).text = stickyModel.title
//                    else adapter.onBindStickyView(viewHolder!!, stickyModel)
//                    break
//                }
//            }
//
//        }
//    }
//
//    fun setAdapter(adapter: Adapter<*>, layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)) {
//        removeAllViews()
//        adapter.setParameter(stickyId, textSize, textColor, textBold, textHeight, textBackground, textGravity, paddingVertical, paddingHorizontal)
//        val stickyView = adapter.onCreateStickyView(this, stickyId)
//        viewHolder = ViewHolder.create(stickyView)
//        recyclerView = RecyclerView(context, attrs, defStyleAttr)
//        recyclerView!!.overScrollMode = OVER_SCROLL_NEVER
//        recyclerView!!.layoutManager = layoutManager
//        addView(recyclerView, 0)
//        addView(stickyView, 1)
//        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val view = recyclerView.findChildViewUnder(0F, stickyView.measuredHeight + 1F)
//                        ?: return
//                val position = recyclerView.getChildLayoutPosition(view)
//                if (position < 0) return
//                if (adapter.getData(position) is StickyModel) {
//                    if (view.top > 0 && view.top <= stickyView.measuredHeight) {
//                        onBindStickyView(adapter, position - 1)
//                        stickyView.translationY = view.top - stickyView.measuredHeight.toFloat()
//                    } else {
//                        onBindStickyView(adapter, position - 1)
//                        stickyView.translationY = 0f
//                    }
//                } else {
//                    onBindStickyView(adapter, position)
//                    stickyView.translationY = 0f
//                }
//            }
//        })
//        recyclerView!!.adapter = adapter
//    }
//
//    abstract class Adapter<D> : RecyclerAdapter<Any>(), ICommonAdapter<D> {
//
//        companion object {
//            const val STICKY = Int.MAX_VALUE - 1
//        }
//
//        internal fun setParameter(stickyId: Int, textSize: Float, textColor: Int, textBold: Boolean,
//                                  textHeight: Int, textBackground: Int, textGravity: Int, paddingVertical: Int, paddingHorizontal: Int) {
//            this.stickyId = stickyId
//            this.textSize = textSize
//            this.textColor = textColor
//            this.textBold = textBold
//            this.textHeight = textHeight
//            this.textBackground = textBackground
//            this.textGravity = textGravity
//            this.paddingVertical = paddingVertical
//            this.paddingHorizontal = paddingHorizontal
//        }
//
//        protected open var stickyId = 0
//        protected open var textSize: Float = 0F
//        protected open var textColor: Int = 0
//        protected open var textBold: Boolean = true
//        protected open var textHeight: Int = 0
//        protected open var textBackground: Int = 0
//        protected open var textGravity: Int = 0
//        protected open var paddingVertical: Int = 0
//        protected open var paddingHorizontal: Int = 0
//
//        override fun onCreateRecyclerHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
//            return if (viewType == STICKY) RecyclerHolder.create(onCreateStickyView(parent, stickyId))
//            else RecyclerHolder.create(parent, getLayoutId())
//        }
//
//        override fun onBindRecyclerHolder(holder: RecyclerHolder, data: Any, dataPosition: Int, viewType: Int) {
//            if (viewType == STICKY) {
//                if (stickyId == 0) (holder.itemView as AppCompatTextView).text = (data as StickyModel).title
//                else onBindStickyView(holder, data as StickyModel)
//            } else {
//                onBindView(holder, holder, data as D, dataPosition)
//            }
//        }
//
//        open fun onCreateStickyView(parent: ViewGroup, stickyId: Int): View {
//            return if (stickyId == 0) {
//                val stickyView = AppCompatTextView(parent.context)
//                stickyView.textSize = textSize
//                stickyView.setTextColor(textColor)
//                stickyView.typeface = if (textBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
//                stickyView.setBackgroundResource(textBackground)
//                stickyView.gravity = CENTER_VERTICAL + textGravity
//                stickyView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
//                stickyView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, textHeight)
//                stickyView
//            } else {
//                LayoutInflater.from(parent.context).inflate(stickyId, parent, false)
//            }
//        }
//
//        open fun onBindStickyView(holder: RecyclerHolder, data: StickyModel) {
//
//        }
//
//        override fun getDataItemType(data: Any, dataPosition: Int): Int {
//            return if (data is StickyModel) STICKY
//            else super.getDataItemType(data, dataPosition)
//        }
//
//        override fun getGridSpanSize(spanCount: Int, data: Any, dataPosition: Int): Int {
//            return if (data is StickyModel) spanCount else 1
//        }
//
//    }
//
//    data class StickyModel(val title: CharSequence)
//}