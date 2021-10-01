package com.p2p.presentation.truco.finalscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.p2p.presentation.base.BaseViewModel

class FinalScoreTrucoViewModel: BaseViewModel<TrucoFinalScoreEvent>() {
    private val _isWinner = MutableLiveData(false)
    val isWinner: LiveData<Boolean> = _isWinner

    private var ourScore: Int? = null
    private var theirScore : Int? = null

    fun setOurScore(score: Int) {
        ourScore = score
        theirScore?.let { isWinner(score, it) }
    }

    fun setTheirScore(score: Int) {
        theirScore = score
        ourScore?.let { isWinner(it, score) }
    }

    fun isWinner(ourScore: Int, theirScore: Int) {
        _isWinner.value = ourScore > theirScore
    }

    fun exit() {
        dispatchSingleTimeEvent(EndTrucoGame)
    }
}