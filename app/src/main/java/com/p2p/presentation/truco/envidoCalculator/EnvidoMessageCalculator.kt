package com.p2p.presentation.truco.envidoCalculator

import com.p2p.R
import com.p2p.model.truco.PlayerTeam
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoAction.ShowEnvidoPoints

object EnvidoMessageCalculator {

    fun envidoMessagesFor2(playersWithPoints: List<Pair<PlayerTeam, Int>>): Map<PlayerTeam, TrucoAction?> {
        val (firstPlayer, firstPlayerScore) = playersWithPoints.first()
        val (secondPlayer, secondPlayerScore) = playersWithPoints.last()
        return mapOf(
            firstPlayer to showScore(firstPlayerScore),
            secondPlayer to
                    if (secondPlayerScore > firstPlayerScore) showAreBetter(secondPlayerScore)
                    else showAreGood(secondPlayerScore)
        )
    }

    /***
     * Logic is like this:
     * 1 -> muestra su puntaje
    // 2 -> Si supera dice su puntaje (a) || o "Son buenas" (b)
    //      (a) 3 -> Si supera dice su puntaje (c) || o "Son buenas" (d)
    //          (c) 4 -> Si supera dice su puntaje (e) || o "Son buenas" (f)
    //          (d) 4 -> No dice nada (g)
    //      (b) 4 -> Si supera dice su puntaje (h) || o "Son buenas" (i)
    //          (h) 3 -> Si supera dice su puntaje (e) || o "Son buenas" (f)
    //      (i) 3 -> No dice nada (g)
     */
    fun envidoMessagesFor4(playersWithPoints: List<Pair<PlayerTeam, Int>>): Map<PlayerTeam, TrucoAction?> {
        val (firstPlayer, firstPlayerScore) = playersWithPoints.first()
        val (secondPlayer, secondPlayerScore) = playersWithPoints[1]
        val (thirdPlayer, thirdPlayerScore) = playersWithPoints[2]
        val (fourthPlayer, fourthPlayerScore) = playersWithPoints[3]
        return mapOf(
            firstPlayer to showScore(firstPlayerScore),
            secondPlayer to getFinalMessageForPlayer(firstPlayerScore, secondPlayerScore),
            thirdPlayer to getThirdPlayerMessage(firstPlayerScore, secondPlayerScore, thirdPlayerScore, fourthPlayerScore),
            fourthPlayer to getFourthPlayerMessage(firstPlayerScore, secondPlayerScore, thirdPlayerScore, fourthPlayerScore)
        )
    }
    private fun getThirdPlayerMessage(firstPlayerScore: Int, secondPlayerScore: Int,
                                      thirdPlayerScore: Int, fourthPlayerScore: Int): TrucoAction? =
        if(secondPlayerScore > firstPlayerScore){
            getFinalMessageForPlayer(secondPlayerScore, thirdPlayerScore)
        }else{
            if(fourthPlayerScore > firstPlayerScore){
                getFinalMessageForPlayer(fourthPlayerScore, thirdPlayerScore)
            }else{
                showNothing()
            }
        }
    private fun getFourthPlayerMessage(firstPlayerScore: Int, secondPlayerScore: Int,
                                       thirdPlayerScore: Int, fourthPlayerScore: Int): TrucoAction? =
        if(secondPlayerScore > firstPlayerScore){
            if(thirdPlayerScore > secondPlayerScore){
                getFinalMessageForPlayer(thirdPlayerScore, fourthPlayerScore)
            }else showNothing()
        } else{
            getFinalMessageForPlayer(firstPlayerScore, fourthPlayerScore)
        }


    private fun getFinalMessageForPlayer(lastBestScore: Int, playerScore: Int): TrucoAction =
        if (playerScore > lastBestScore) showScore(playerScore)
        else showAreGood(playerScore)

    private fun showAreBetter(points: Int) =
        ShowEnvidoPoints(points, R.string.truco_answer_envido_are_better)

    private fun showAreGood(points: Int) =
        ShowEnvidoPoints(points, R.string.truco_answer_envido_are_good)

    private fun showScore(points: Int) = ShowEnvidoPoints(points)
    private fun showNothing() = null

}