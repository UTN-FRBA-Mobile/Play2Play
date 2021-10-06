package com.p2p.presentation.truco.create.points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class CreateTrucoPointsViewModel : BaseViewModel<Unit>() {

    /** The number of points to play. */
    private val _pointsNumber = MutableLiveData<Int>()
    val pointsNumber: LiveData<Int> = _pointsNumber

    private val _enableIncrease = MutableLiveData<Boolean>()
    val enableIncrease: LiveData<Boolean> = _enableIncrease

    private val _enableDecrease = MutableLiveData<Boolean>()
    val enableDecrease: LiveData<Boolean> = _enableDecrease

    init {
        _pointsNumber.value = DEFAULT_POINTS_NUMBER
    }

    fun increase() {
        _pointsNumber.value = pointsNumber.value?.plus(15)
        _enableIncrease.value = true
        if (pointsNumber.value == MAXIMUM_POINTS_NUMBER) {
            _enableIncrease.value = false
        }
    }

    fun decrease() {
        _pointsNumber.value = pointsNumber.value?.minus(15)
        _enableDecrease.value = true
        if (pointsNumber.value == MINIMUM_POINTS_NUMBER) {
            _enableDecrease.value = false
        }
    }

    companion object {
        const val MAXIMUM_POINTS_NUMBER = 30
        const val MINIMUM_POINTS_NUMBER = 15
        const val DEFAULT_POINTS_NUMBER = 15
    }
}
