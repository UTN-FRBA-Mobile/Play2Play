package com.p2p.presentation.tuttifrutti.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.tuttifrutti.create.categories.Category
import java.text.Normalizer

class TuttiFruttiReviewViewModel :
    BaseViewModel<TuttiFruttiReviewEvents>() {

    private var actualRound: RoundInfo? = null
    private var finishedRoundInfos : List<FinishedRoundInfo>? = null

    /** List with the finished round review points */
    private val _finishedRoundPointsInfo = MutableLiveData(listOf<FinishedRoundPointsInfo>())
    val finishedRoundPointsInfo: LiveData<List<FinishedRoundPointsInfo>> = _finishedRoundPointsInfo

    fun setInitialActualRound(setActualRound: RoundInfo) {
        actualRound = setActualRound
        finishedRoundInfos?.let { initializeBaseRoundPoints(setActualRound, it) }
    }

    fun setInitialFinishedRoundInfos(finishedRoundInfos: List<FinishedRoundInfo>) {
        this.finishedRoundInfos = finishedRoundInfos
        actualRound?.let { initializeBaseRoundPoints(it, finishedRoundInfos) }
    }

    fun onAddRoundPoints(player: String, categoryIndex: Int) {
        val updatedFinishedRoundPoints = finishedRoundPointsInfo.requireValue().toMutableList()
        val elementToUpdate = updatedFinishedRoundPoints.first { it.player == player }
        val wordsPoints = elementToUpdate.wordsPoints.toMutableList()

        wordsPoints[categoryIndex] += 5

        updatedFinishedRoundPoints[updatedFinishedRoundPoints.indexOf(elementToUpdate)] =
            elementToUpdate.copy(wordsPoints = wordsPoints)

        _finishedRoundPointsInfo.value = updatedFinishedRoundPoints
    }

    fun onSubstractRoundPoints(player: String, categoryIndex: Int) {
        val updatedFinishedRoundPoints = finishedRoundPointsInfo.requireValue().toMutableList()
        val elementToUpdate = updatedFinishedRoundPoints.first { it.player == player }
        val wordsPoints = elementToUpdate.wordsPoints.toMutableList()

        wordsPoints[categoryIndex] -= 5

        updatedFinishedRoundPoints[updatedFinishedRoundPoints.indexOf(elementToUpdate)] =
            elementToUpdate.copy(wordsPoints = wordsPoints)

        _finishedRoundPointsInfo.value = updatedFinishedRoundPoints
    }

    /** Process the finishedRoundInfo list to take the base points for all the players */
    private fun initializeBaseRoundPoints(actualRound: RoundInfo, finishedRoundInfos: List<FinishedRoundInfo>) {
        val roundInitialPoints = finishedRoundInfos.map {
            val pointsList = it.categoriesWords.map { playerResponse ->
                val categoryWords = getCategoryWords(playerResponse.key, finishedRoundInfos)
                getPointsForWord(playerResponse.value, actualRound.letter, categoryWords)
            }.toMutableList()

            FinishedRoundPointsInfo(it.player, pointsList, pointsList.sum())
        }

        _finishedRoundPointsInfo.value = roundInitialPoints
    }

    private fun getCategoryWords(category: Category, finishedRoundInfos: List< FinishedRoundInfo>) : List<String> =
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
        finishedRoundPointsInfo.requireValue().forEach { it.totalPoints = it.wordsPoints.sum() }
    }

    /** Next view to show when Continue button is pressed. */
    fun sendRoundPoints() {
        calculateTotalRoundPoints()
        dispatchSingleTimeEvent(FinishRoundReview(finishedRoundPointsInfo.requireValue()))
    }
}
