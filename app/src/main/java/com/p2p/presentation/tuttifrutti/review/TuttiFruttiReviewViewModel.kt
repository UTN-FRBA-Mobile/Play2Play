package com.p2p.presentation.tuttifrutti.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.tuttifrutti.review.TuttiFruttiReviewEvents

class TuttiFruttiReviewViewModel :
    BaseViewModel<TuttiFruttiReviewEvents>() {

    /** Whether the continue button is enabled or not. */
    private val _continueButtonEnabled = MutableLiveData<Boolean>()
    val continueButtonEnabled: LiveData<Boolean> = _continueButtonEnabled
}