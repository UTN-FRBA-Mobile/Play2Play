package ar.com.play2play.presentation.truco

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

@SuppressLint("ClickableViewAccessibility")
class TrucoDragAndDropCard(
    val cardView: View,
    private val isAbleToDrag: Boolean,
    listener: Listener,
) {

    var droppingView: View? = null

    private var initialPosition: Pair<Float, Float>? = null
    private var initialCoordinates: Pair<Float, Float>? = null
    private var initialRotation: Float = 0f

    init {
        cardView.setOnTouchListener { view: View, event: MotionEvent ->
            when {
                !isAbleToDrag -> false
                event.action == MotionEvent.ACTION_DOWN -> {
                    initialPosition = event.rawX to event.rawY
                    initialCoordinates = view.x to view.y
                    initialRotation = view.rotation
                    view.animate()
                        .rotation(0f)
                        .start()
                    listener.onTouch(this)
                    true
                }
                event.actionMasked == MotionEvent.ACTION_MOVE -> {
                    val initialCoordinates = initialCoordinates ?: return@setOnTouchListener false
                    val initialPosition = initialPosition ?: return@setOnTouchListener false
                    view.x = initialCoordinates.first + event.rawX - initialPosition.first
                    view.y = initialCoordinates.second + event.rawY - initialPosition.second
                    droppingView?.let { currentDroppingView ->
                        val initialDistance = currentDroppingView.y - initialCoordinates.second
                        val currentDistance = currentDroppingView.y - view.y
                        val distanceWithDroppingPlaceMultiplicator =
                            (currentDistance / initialDistance)
                                .coerceAtLeast(0f)
                                .coerceAtMost(1f)
                        view.rotationX =
                            currentDroppingView.rotationX * (1f - distanceWithDroppingPlaceMultiplicator)
                        val scale = when {
                            view.y > initialCoordinates.second -> 1f
                            view.y > currentDroppingView.y ->
                                currentDroppingView.scaleX + (1f - currentDroppingView.scaleX) * distanceWithDroppingPlaceMultiplicator
                            else -> MIN_CARD_SCALE + (currentDroppingView.scaleX - MIN_CARD_SCALE) * view.y / currentDroppingView.y
                        }
                        view.scaleX = scale
                        view.scaleY = scale
                    }
                    listener.onMove(this)
                    true
                }
                event.actionMasked == MotionEvent.ACTION_UP -> {
                    initialCoordinates = null
                    initialPosition = null
                    val currentDroppingView = droppingView
                    val isInDroppingView = currentDroppingView?.let {
                        currentDroppingView.x <= event.rawX &&
                                event.rawX <= currentDroppingView.x + currentDroppingView.width &&
                                currentDroppingView.y <= event.rawY &&
                                event.rawY <= currentDroppingView.y + currentDroppingView.height
                    }
                    if (isInDroppingView == true) {
                        view.animate()
                            .x(currentDroppingView.x)
                            .y(currentDroppingView.y)
                            .rotation(currentDroppingView.rotation)
                            .scaleX(currentDroppingView.scaleX)
                            .scaleY(currentDroppingView.scaleX)
                            .rotationX(currentDroppingView.rotationX)
                            .start()
                        cardView.setOnTouchListener(null)
                    } else {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .rotationX(0f)
                            .rotation(initialRotation)
                            .start()
                    }
                    listener.onDrop(this@TrucoDragAndDropCard, isInDroppingView ?: false)
                    true
                }
                else -> false
            }
        }
    }

    interface Listener {

        fun onTouch(dragAndDropCard: TrucoDragAndDropCard)

        fun onMove(dragAndDropCard: TrucoDragAndDropCard)

        fun onDrop(dragAndDropCard: TrucoDragAndDropCard, isInDroppingView: Boolean)
    }

    companion object {

        private const val MIN_CARD_SCALE = 0.4f
    }
}