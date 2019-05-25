package de.frejasundalexchat.dritterchat.view

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView


class RoundedCornersImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    init {
        this.clipToOutline = true
        this.outlineProvider = ClipOutlineProvider()
    }
}

class ClipOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        val radius = view.context.toPx(10f)
        outline.setRoundRect(0, 0, view.width, view.height, radius)
    }
}

fun Context.toPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    )
}