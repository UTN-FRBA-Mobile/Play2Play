package com.p2p.presentation.home.games

import com.p2p.BaseTest
import com.p2p.R
import com.p2p.data.UserSession
import com.p2p.presentation.base.BaseViewModel
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GamesViewModelTest : BaseTest() {

    @MockK
    lateinit var userSession: UserSession

    private lateinit var gamesViewModel: GamesViewModel

    @Before
    fun setup() {
        gamesViewModel = GamesViewModel(userSession)
    }

    @Test
    fun `when create then load games`() {

        // THEN
        assertThat(gamesViewModel.games.value, `is`(Game.values().toList()))
    }

    @Test
    fun `when select a game then enable create button`() {

        // WHEN
        gamesViewModel.selectGame(mockk())

        // THEN
        assertThat(gamesViewModel.createButtonEnabled.value, `is`(true))
    }

    @Test
    fun `when select no game then disable create button`() {

        // WHEN
        gamesViewModel.selectGame(null)

        // THEN
        assertThat(gamesViewModel.createButtonEnabled.value, `is`(false))
    }

    @Test
    fun `given no name when create a game then dispatch an error message`() {

        // WHEN
        gamesViewModel.createGame(null)

        // THEN
        assertThat(gamesViewModel.message.value, `is`(BaseViewModel.MessageData(
            textRes = R.string.games_name_error,
            type = BaseViewModel.MessageData.Type.ERROR
        )))
    }

    @Test
    fun `given no name when join a game then dispatch an error message`() {

        // WHEN
        gamesViewModel.createGame(null)

        // THEN
        assertThat(gamesViewModel.message.value, `is`(BaseViewModel.MessageData(
            textRes = R.string.games_name_error,
            type = BaseViewModel.MessageData.Type.ERROR
        )))
    }
}
