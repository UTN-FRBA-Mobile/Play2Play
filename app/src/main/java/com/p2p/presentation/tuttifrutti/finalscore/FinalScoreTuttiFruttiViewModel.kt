package com.p2p.presentation.tuttifrutti.finalscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class FinalScoreTuttiFruttiViewModel:
    BaseViewModel<TuttiFruttiFinalScoreEvent>() {

    private val _finalScores = MutableLiveData(listOf<TuttiFruttiFinalScore>()
        .plus(TuttiFruttiFinalScore("pepe", 54))
        .plus(TuttiFruttiFinalScore("moni", 53))
        .plus(TuttiFruttiFinalScore("dardo", 50))) //TODO REMOVE MOCKS
    val finalScores: LiveData<List<TuttiFruttiFinalScore>> = _finalScores

    fun exit() {
        dispatchSingleTimeEvent(EndTuttiFruttiGame)
    }
}