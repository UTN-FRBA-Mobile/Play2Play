package ar.com.play2play.presentation.truco.finalscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.model.truco.FinalResult
import ar.com.play2play.presentation.base.BaseViewModel

class FinalScoreTrucoViewModel : BaseViewModel<TrucoFinalScoreEvent>() {

    private val _finalResult = MutableLiveData<FinalResult>()
    val finalResult: LiveData<FinalResult> = _finalResult

    private var ourScore: Int? = null
    private var theirScore: Int? = null

    fun setOurScore(score: Int) {
        ourScore = score
        theirScore?.let { isWinner(score, it) }
    }

    fun setTheirScore(score: Int) {
        theirScore = score
        ourScore?.let { isWinner(it, score) }
    }

    private fun isWinner(ourScore: Int, theirScore: Int) {
        _finalResult.value = FinalResult(
            isWinner = ourScore > theirScore,
            ourScore = ourScore,
            theirScore = theirScore
        )
    }

    fun exit() {
        dispatchSingleTimeEvent(EndTrucoGame)
    }
}