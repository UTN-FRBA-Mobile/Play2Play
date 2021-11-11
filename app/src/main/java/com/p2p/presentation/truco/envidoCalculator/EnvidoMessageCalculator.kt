package com.p2p.presentation.truco.envidoCalculator

import com.p2p.R
import com.p2p.model.truco.TeamPlayer
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoAction.ShowEnvidoPoints

object EnvidoMessageCalculator {

    fun envidoMessagesFor2(
        playersWithPoints: List<Pair<TeamPlayer, Int>>
    ): List<Pair<TeamPlayer, TrucoAction?>> {
        val (firstPlayer, firstPlayerScore) = playersWithPoints.first()
        val (secondPlayer, secondPlayerScore) = playersWithPoints.last()
        return listOf(
            firstPlayer to showScore(firstPlayerScore),
            secondPlayer to
                    if (secondPlayerScore > firstPlayerScore) showAreBetter(secondPlayerScore)
                    else showAreGood(secondPlayerScore)
        )
    }

    /***
     * - The first player always shows their points.
     * - The second player shows their points only if these are greater than first player points.
     *   Otherwise they say "Yours are good".
     * - The third player shows their points in case the second or the fourth win.
     *   In the case the first player win they say nothing.
     *   Otherwise they say "Yours are good.
     * - The fourth player shows their points in case the first or the third win.
     *   In the case the second player win they say nothing.
     *   Otherwise they say "Yours are good.
     */
    fun envidoMessagesFor4(
        playersWithPoints: List<Pair<TeamPlayer, Int>>
    ): List<Pair<TeamPlayer, TrucoAction?>> {
        val (firstPlayer, firstPlayerScore) = playersWithPoints.first()
        val (secondPlayer, secondPlayerScore) = playersWithPoints[1]
        val (thirdPlayer, thirdPlayerScore) = playersWithPoints[2]
        val (fourthPlayer, fourthPlayerScore) = playersWithPoints[3]
        val messages = mutableListOf<Pair<TeamPlayer, TrucoAction?>>(
            firstPlayer to showScore(firstPlayerScore),
            secondPlayer to getFinalMessageForPlayer(firstPlayerScore, secondPlayerScore)
        )
        val thirdPlayerMessage = thirdPlayer to getThirdPlayerMessage(
            firstPlayerScore,
            secondPlayerScore,
            thirdPlayerScore,
            fourthPlayerScore
        )
        val fourthPlayerMessage = fourthPlayer to getFourthPlayerMessage(
            firstPlayerScore,
            secondPlayerScore,
            thirdPlayerScore,
            fourthPlayerScore
        )

        val shouldFourthPlayerCallFirst =
            shouldForthPlayerCallFirst(firstPlayerScore, secondPlayerScore, fourthPlayerScore)
        messages.add(if (shouldFourthPlayerCallFirst) fourthPlayerMessage else thirdPlayerMessage)
        messages.add(if (shouldFourthPlayerCallFirst) thirdPlayerMessage else fourthPlayerMessage)
        return messages
    }

    private fun getThirdPlayerMessage(
        firstPlayerScore: Int,
        secondPlayerScore: Int,
        thirdPlayerScore: Int,
        fourthPlayerScore: Int
    ): TrucoAction? = when {
        secondPlayerScore > firstPlayerScore ->
            getFinalMessageForPlayer(secondPlayerScore, thirdPlayerScore)
        fourthPlayerScore > firstPlayerScore ->
            getFinalMessageForPlayer(fourthPlayerScore, thirdPlayerScore)
        else -> showNothing()
    }

    private fun getFourthPlayerMessage(
        firstPlayerScore: Int,
        secondPlayerScore: Int,
        thirdPlayerScore: Int,
        fourthPlayerScore: Int
    ): TrucoAction? = when {
        secondPlayerScore <= firstPlayerScore ->
            getFinalMessageForPlayer(firstPlayerScore, fourthPlayerScore)
        thirdPlayerScore > secondPlayerScore ->
            getFinalMessageForPlayer(thirdPlayerScore, fourthPlayerScore)
        else -> showNothing()
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

    @Suppress("ConvertTwoComparisonsToRangeCheck") // It's more clear this way.
    private fun shouldForthPlayerCallFirst(
        firstPlayerScore: Int,
        secondPlayerScore: Int,
        fourthPlayerScore: Int
    ) = firstPlayerScore >= secondPlayerScore && fourthPlayerScore > firstPlayerScore
}