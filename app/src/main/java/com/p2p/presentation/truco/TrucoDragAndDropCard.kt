package com.p2p.presentation.truco

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

@SuppressLint("ClickableViewAccessibility")
class TrucoDragAndDropCard(
    val cardView: View,
    listener: Listener,
) {

    var droppingView: View? = null

    private lateinit var initialPosition: Pair<Float, Float>
    private lateinit var initialCoordinates: Pair<Float, Float>
    private var initialRotation: Float = 0f

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
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .rotation(0f)
                        .start()
                    listener.onTouch(this)
                    true
                }
                event.actionMasked == MotionEvent.ACTION_MOVE -> {
                    view.x = initialCoordinates.first + event.rawX - initialPosition.first
                    view.y = initialCoordinates.second + event.rawY - initialPosition.second
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
                            .start()
                        cardView.setOnTouchListener(null)
                    } else {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
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
}