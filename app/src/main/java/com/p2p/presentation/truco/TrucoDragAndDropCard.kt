package com.p2p.presentation.truco

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.p2p.R

@SuppressLint("ClickableViewAccessibility")
class TrucoDragAndDropCard(
    val cardView: View,
    listener: Listener,
) {

    var droppingView: View? = null

    private lateinit var initialPosition: Pair<Float, Float>
    private lateinit var initialCoordinates: Pair<Float, Float>
    private var initialRotation: Float = 0f
    private val cardFinalRotation by lazy {
        cardView.context.resources.getInteger(R.integer.truco_card_final_rotation).toFloat()
    }
    private val cardFinalScale by lazy {
        TypedValue()
            .apply { cardView.context.resources.getValue(R.integer.truco_card_final_scale, this, true) }
            .float
    }

    init {
        cardView.setOnTouchListener { view: View, event: MotionEvent ->
            val currentDroppingView = droppingView
            when {
                currentDroppingView == null -> false
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
                    view.x = initialCoordinates.first + event.rawX - initialPosition.first
                    view.y = initialCoordinates.second + event.rawY - initialPosition.second
                    val initialDistance = currentDroppingView.y - initialCoordinates.second
                    val currentDistance = currentDroppingView.y - view.y
                    val distanceWithDroppingPlaceMultiplicator = (currentDistance / initialDistance)
                        .coerceAtLeast(0f)
                        .coerceAtMost(1f)
                    view.rotationX = cardFinalRotation * (1f - distanceWithDroppingPlaceMultiplicator)
                    val scale = when {
                        view.y > initialCoordinates.second -> 1f
                        view.y > currentDroppingView.y ->
                            cardFinalScale + (1f - cardFinalScale) * distanceWithDroppingPlaceMultiplicator
                        else -> MIN_CARD_SCALE + (cardFinalScale - MIN_CARD_SCALE) * view.y / currentDroppingView.y
                    }
                    view.scaleX = scale
                    view.scaleY = scale
                    listener.onMove(this)
                    true
                }
                event.actionMasked == MotionEvent.ACTION_UP -> {
                    val isInDroppingView = currentDroppingView.x <= event.rawX &&
                            event.rawX <= currentDroppingView.x + currentDroppingView.width &&
                            currentDroppingView.y <= event.rawY &&
                            event.rawY <= currentDroppingView.y + currentDroppingView.height
                    if (isInDroppingView) {
                        view.animate()
                            .x(currentDroppingView.x)
                            .y(currentDroppingView.y)
                            .rotation(currentDroppingView.rotation)
                            .scaleX(cardFinalScale)
                            .scaleY(cardFinalScale)
                            .rotationX(cardFinalRotation)
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
                    listener.onDrop(this@TrucoDragAndDropCard, isInDroppingView)
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