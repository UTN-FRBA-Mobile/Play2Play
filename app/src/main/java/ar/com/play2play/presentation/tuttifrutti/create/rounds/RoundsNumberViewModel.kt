package ar.com.play2play.presentation.tuttifrutti.create.rounds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.presentation.base.BaseViewModel

class RoundsNumberViewModel : BaseViewModel<Unit>() {

    /** The number of rounds to play. */
    private val _roundsNumber = MutableLiveData<Int>()
    val roundsNumber: LiveData<Int> = _roundsNumber

    init {
        _roundsNumber.value = DEFAULT_ROUNDS_NUMBER
    }

    fun increase() {
        _roundsNumber.value = (number() + 1).coerceAtMost(MAXIMUM_ROUND_NUMBER)
    }

    fun decrease() {
        _roundsNumber.value = (number() - 1).coerceAtLeast(1)
    }

    private fun number(): Int = _roundsNumber.value ?: DEFAULT_ROUNDS_NUMBER

    companion object {
        const val MAXIMUM_ROUND_NUMBER = 25
        const val DEFAULT_ROUNDS_NUMBER = 5
    }
}
