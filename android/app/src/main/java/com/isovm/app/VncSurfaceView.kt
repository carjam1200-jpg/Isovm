package com.isovm.app

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * Placeholder view for VNC framebuffer rendering. Integrate a VNC client and feed decoded frames into this view.
 */
class VncSurfaceView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    constructor(context: Context) : this(context, null)

    private var frameBitmap: android.graphics.Bitmap? = null

    fun updateFrame(bitmap: android.graphics.Bitmap) {
        frameBitmap = bitmap
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        frameBitmap?.let { bmp ->
            val dst = android.graphics.Rect(0, 0, width, height)
            canvas.drawBitmap(bmp, null, dst, null)
        }
    }
}
