package blbl.cat3399.feature.player

import android.graphics.Rect
import android.graphics.RectF
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import blbl.cat3399.core.image.ImageLoader
import kotlin.math.abs

internal fun PlayerActivity.initCommentImageViewer() {
    binding.commentImageViewer.visibility = View.GONE
    ImageLoader.loadInto(binding.ivCommentImage, null)

    binding.ivCommentImagePrev.setOnClickListener {
        if (!isCommentImageViewerVisible()) return@setOnClickListener
        commentImageViewerPrev()
    }
    binding.ivCommentImageNext.setOnClickListener {
        if (!isCommentImageViewerVisible()) return@setOnClickListener
        commentImageViewerNext()
    }

    var downX = 0f
    var downY = 0f
    var downInsideImage = false
    var touchPassthroughToChildren = false

    binding.commentImageViewer.setOnTouchListener { _, event ->
        if (!isCommentImageViewerVisible()) return@setOnTouchListener false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Allow the hint icons to handle their own click events.
                val downInsidePrev = isTouchInsideView(binding.ivCommentImagePrev, event)
                val downInsideNext = isTouchInsideView(binding.ivCommentImageNext, event)
                if (downInsidePrev || downInsideNext) {
                    touchPassthroughToChildren = true
                    return@setOnTouchListener false
                }
                touchPassthroughToChildren = false

                downX = event.rawX
                downY = event.rawY
                downInsideImage = isTouchInsideImageContent(binding.ivCommentImage, event)
                true
            }

            MotionEvent.ACTION_UP -> {
                if (touchPassthroughToChildren) {
                    touchPassthroughToChildren = false
                    return@setOnTouchListener false
                }
                val dx = event.rawX - downX
                val dy = event.rawY - downY
                val absDx = abs(dx)
                val absDy = abs(dy)
                val swipeThresholdPx = binding.commentImageViewer.resources.displayMetrics.density * 48f

                if (commentImageViewerUrls.size > 1 && absDx > swipeThresholdPx && absDx > absDy) {
                    if (dx < 0) {
                        commentImageViewerNext()
                    } else {
                        commentImageViewerPrev()
                    }
                    return@setOnTouchListener true
                }

                val upInsideImage = isTouchInsideImageContent(binding.ivCommentImage, event)
                if (!downInsideImage && !upInsideImage) {
                    closeCommentImageViewer()
                }
                true
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchPassthroughToChildren) return@setOnTouchListener false
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                if (touchPassthroughToChildren) {
                    touchPassthroughToChildren = false
                    return@setOnTouchListener false
                }
                touchPassthroughToChildren = false
                true
            }

            else -> true
        }
    }
}

internal fun PlayerActivity.isCommentImageViewerVisible(): Boolean = binding.commentImageViewer.visibility == View.VISIBLE

internal fun PlayerActivity.openCommentImageViewer(urls: List<String>, startIndex: Int = 0) {
    val safeUrls = urls.map { it.trim() }.filter { it.isNotBlank() }
    if (safeUrls.isEmpty()) return

    commentImageViewerUrls = safeUrls
    commentImageViewerIndex = startIndex.coerceIn(0, safeUrls.lastIndex)
    commentImageViewerFocusReturn.capture(currentFocus)

    binding.commentImageViewer.visibility = View.VISIBLE
    binding.commentImageViewer.bringToFront()
    binding.commentImageViewer.invalidate()
    binding.commentImageViewer.requestLayout()
    binding.commentImageViewer.requestFocus()
    renderCommentImageViewer()
}

internal fun PlayerActivity.closeCommentImageViewer(restoreFocus: Boolean = true) {
    if (!isCommentImageViewerVisible()) return

    binding.commentImageViewer.visibility = View.GONE
    ImageLoader.loadInto(binding.ivCommentImage, null)
    commentImageViewerUrls = emptyList()
    commentImageViewerIndex = 0

    if (!restoreFocus) {
        commentImageViewerFocusReturn.clear()
        return
    }

    val fallback =
        when {
            isCommentThreadVisible() -> binding.recyclerCommentThread
            isCommentsPanelVisible() -> binding.recyclerComments
            else -> binding.btnComments
        }
    commentImageViewerFocusReturn.restoreAndClear(fallback = fallback, postOnFail = false)
}

internal fun PlayerActivity.dispatchCommentImageViewerKey(event: KeyEvent): Boolean {
    if (!isCommentImageViewerVisible()) return false

    val keyCode = event.keyCode
    if (event.action == KeyEvent.ACTION_DOWN) {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                closeCommentImageViewer()
                return true
            }

            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_SETTINGS,
            KeyEvent.KEYCODE_INFO,
            KeyEvent.KEYCODE_GUIDE,
            -> return true

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                commentImageViewerPrev()
                return true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                commentImageViewerNext()
                return true
            }

            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            -> return true
        }
    }

    if (event.action == KeyEvent.ACTION_UP) {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_SETTINGS,
            KeyEvent.KEYCODE_INFO,
            KeyEvent.KEYCODE_GUIDE,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            -> return true
        }
    }

    return false
}

private fun PlayerActivity.renderCommentImageViewer() {
    val urls = commentImageViewerUrls
    if (urls.isEmpty()) {
        closeCommentImageViewer()
        return
    }

    val idx = commentImageViewerIndex.coerceIn(0, urls.lastIndex)
    commentImageViewerIndex = idx
    ImageLoader.loadInto(binding.ivCommentImage, urls[idx])

    binding.ivCommentImagePrev.visibility =
        if (urls.size > 1 && idx > 0) View.VISIBLE else View.GONE
    binding.ivCommentImageNext.visibility =
        if (urls.size > 1 && idx < urls.lastIndex) View.VISIBLE else View.GONE
}

private fun PlayerActivity.commentImageViewerPrev() {
    if (commentImageViewerUrls.size <= 1) return
    if (commentImageViewerIndex <= 0) return
    commentImageViewerIndex -= 1
    renderCommentImageViewer()
}

private fun PlayerActivity.commentImageViewerNext() {
    if (commentImageViewerUrls.size <= 1) return
    if (commentImageViewerIndex >= commentImageViewerUrls.lastIndex) return
    commentImageViewerIndex += 1
    renderCommentImageViewer()
}

private fun isTouchInsideView(target: View, event: MotionEvent): Boolean {
    val rect = Rect()
    val visible = target.getGlobalVisibleRect(rect)
    if (!visible) return false
    return rect.contains(event.rawX.toInt(), event.rawY.toInt())
}

private fun isTouchInsideImageContent(target: ImageView, event: MotionEvent): Boolean {
    if (target.visibility != View.VISIBLE) return false

    val globalRect = Rect()
    val visible = target.getGlobalVisibleRect(globalRect)
    if (!visible) return false
    if (!globalRect.contains(event.rawX.toInt(), event.rawY.toInt())) return false

    val drawable = target.drawable ?: return false
    val dw = drawable.intrinsicWidth
    val dh = drawable.intrinsicHeight
    if (dw <= 0 || dh <= 0) return false

    val loc = IntArray(2)
    target.getLocationOnScreen(loc)
    val localX = event.rawX - loc[0]
    val localY = event.rawY - loc[1]
    if (localX < 0f || localY < 0f || localX > target.width.toFloat() || localY > target.height.toFloat()) {
        return false
    }

    val content =
        RectF(
            0f,
            0f,
            dw.toFloat(),
            dh.toFloat(),
        )
    target.imageMatrix.mapRect(content)

    return content.contains(localX, localY)
}
