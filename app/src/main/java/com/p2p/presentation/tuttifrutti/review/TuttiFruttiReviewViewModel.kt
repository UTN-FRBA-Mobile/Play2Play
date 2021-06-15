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

    /** List with the finished round values */
    private val _finishedRoundInfo = MutableLiveData(listOf<FinishedRoundInfo>())
    val finishedRoundInfo: LiveData<List<FinishedRoundInfo>> = _finishedRoundInfo

    /** List with the finished round review points */
    private val _finishedRoundPointsInfo = MutableLiveData(listOf<FinishedRoundPointsInfo>())
    val finishedRoundPointsInfo: LiveData<List<FinishedRoundPointsInfo>> = _finishedRoundPointsInfo

    // TODO: This values are for mocking the obtained finishedRoundInfo, delete it when the communication is done.
    private val firstPlayer = FinishedRoundInfo("Lisa",
        mapOf("Nombres" to "Adela", "Animales" to "Aguila", "Comidas" to "Alfajor") as LinkedHashMap<Category, String>, true)
    private val secondPlayer = FinishedRoundInfo("Homero",
        mapOf("Nombres" to "Oso", "Animales" to "A", "Comidas" to "") as LinkedHashMap<Category, String>, false)
    private val thirdPlayer = FinishedRoundInfo("Bart",
        mapOf("Nombres" to "Ambar", "Animales" to "Anguila", "Comidas" to "Alfajor") as LinkedHashMap<Category, String>, false)

    init {
        _finishedRoundInfo.value = listOf(firstPlayer, secondPlayer, thirdPlayer)
    }

    fun onChangeRoundPoints(action: String, player: String, categoryIndex: Int) {
        val updatedFinishedRoundPoints = finishedRoundPointsInfo.value!!.toMutableList()

        if(action == "add") {
            updatedFinishedRoundPoints.find { it.player == player }!!.wordsPoints[categoryIndex] += 5

        } else {
            updatedFinishedRoundPoints.find { it.player == player }!!.wordsPoints[categoryIndex] -= 5
        }
        _finishedRoundPointsInfo.value = updatedFinishedRoundPoints
    }

    /** Process the finishedRoundInfo list to take the base points for all the players */
    fun initializeBaseRoundPoints(actualRound: RoundInfo) {
        val roundInitialPoints = mutableListOf<FinishedRoundPointsInfo>()

        finishedRoundInfo.value!!.forEach {
            val pointsList = mutableListOf<Int>()
            it.categoriesWords.forEach { playerResponse ->
                val categoryWords = getCategoryWords(playerResponse.key)
                // TODO: Stop hardcoding the letter for testing, it should be actualRound.letter
                pointsList.add(getPointsForWord(playerResponse.value, 'A', categoryWords))
            }

            val playerPoints = FinishedRoundPointsInfo(it.player, pointsList, pointsList.sum())
            roundInitialPoints.add(playerPoints)
        }

        _finishedRoundPointsInfo.value = roundInitialPoints
    }

    private fun getCategoryWords(category: Category) : List<String> =
        finishedRoundInfo.value!!.map{ Normalizer.normalize(it.categoriesWords[category].toString(), Normalizer.Form.NFD) }

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
        _finishedRoundPointsInfo.value!!.forEach { it.totalPoints = it.wordsPoints.sum() }
    }

    /** Next view to show when Continue button is pressed. */
    fun sendRoundPoints() {
        calculateTotalRoundPoints()
        // TODO: send a finalized round revision event with the finalizedRoundPointsInfo.
        // The server saves this and accumulates these points. Then we continue to the next round,
        // or we finish the game and go to view the final points
    }
}
