package com.p2p.presentation.truco.finalscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.model.truco.FinalResult
import com.p2p.presentation.base.BaseViewModel

class FinalScoreTrucoViewModel: BaseViewModel<TrucoFinalScoreEvent>() {
    private val _finalResult = MutableLiveData<FinalResult>()
    val finalResult: LiveData<FinalResult> = _finalResult

    private var setFinalResult : FinalResult = FinalResult(null, null, null)

    fun setOurScore(score: Int) {
        setFinalResult.ourScore = score
        setFinalResult.theirScore?.let { isWinner(score, it) }
    }

    fun setTheirScore(score: Int) {
        setFinalResult.theirScore = score
        setFinalResult.ourScore?.let { isWinner(it, score) }
    }

    fun isWinner(ourScore: Int, theirScore: Int) {
        setFinalResult.isWinner = ourScore > theirScore
        _finalResult.value = setFinalResult
    }

    fun exit() {
        dispatchSingleTimeEvent(EndTrucoGame)
    }
}