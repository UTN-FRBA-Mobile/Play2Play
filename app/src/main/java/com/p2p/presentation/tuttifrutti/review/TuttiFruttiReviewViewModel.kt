package com.p2p.presentation.tuttifrutti.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category
import java.text.Normalizer

class TuttiFruttiReviewViewModel :
    BaseViewModel<TuttiFruttiReviewEvents>() {

    lateinit var finishedRoundPointsInfo: List<FinishedRoundPointsInfo>

    fun onChangeRoundPoints(action: String, player: String, categoryIndex: Int) {
        val updatedFinishedRoundPoints = finishedRoundPointsInfo.toMutableList()

        if(action == "add") {
            updatedFinishedRoundPoints.find { it.player == player }!!.wordsPoints[categoryIndex] += 5

        } else {
            updatedFinishedRoundPoints.find { it.player == player }!!.wordsPoints[categoryIndex] -= 5
        }
        finishedRoundPointsInfo = updatedFinishedRoundPoints
    }

    /** Process the finishedRoundInfo list to take the base points for all the players */
    fun initializeBaseRoundPoints(actualRound: RoundInfo, finishedRoundInfos: List<FinishedRoundInfo>) {
        val roundInitialPoints = mutableListOf<FinishedRoundPointsInfo>()

        finishedRoundInfos.forEach {
            val pointsList = mutableListOf<Int>()
            it.categoriesWords.forEach { playerResponse ->
                val categoryWords = getCategoryWords(playerResponse.key, finishedRoundInfos)
                pointsList.add(getPointsForWord(playerResponse.value, actualRound.letter, categoryWords))
            }

            val playerPoints = FinishedRoundPointsInfo(it.player, pointsList, pointsList.sum())
            roundInitialPoints.add(playerPoints)
        }

        finishedRoundPointsInfo = roundInitialPoints
    }

    private fun getCategoryWords(category: Category, finishedRoundInfos: List<FinishedRoundInfo>) : List<String> =
        finishedRoundInfos.map{ Normalizer.normalize(it.categoriesWords[category].toString(), Normalizer.Form.NFD) }

    private fun getPointsForWord(word: String, roundLetter: Char, categoryWords: List<String>) : Int {
        val categoryWord = Normalizer.normalize(word, Normalizer.Form.NFD)
        if(categoryWord.startsWith(roundLetter, ignoreCase = true) && categoryWord.length > 2) {
            if(categoryWords.count{ it.equals(categoryWord, ignoreCase = true) } >= 2) {
                return 5
            }
            return 10
        }
        return 0
    }

    private fun calculateTotalRoundPoints() {
        finishedRoundPointsInfo.forEach { it.totalPoints = it.wordsPoints.sum() }
    }

    /** Next view to show when Continue button is pressed. */
    fun sendRoundPoints() {
        calculateTotalRoundPoints()
        dispatchSingleTimeEvent(FinishRoundReview)
    }
}
