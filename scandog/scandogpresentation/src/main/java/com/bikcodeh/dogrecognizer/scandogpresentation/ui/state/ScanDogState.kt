package com.bikcodeh.dogrecognizer.scandogpresentation.ui.state

import androidx.annotation.StringRes
import com.bikcodeh.dogrecognizer.core.model.Dog

sealed class Effect {
    data class NavigateToDetail(val dog: Dog) : Effect()
    data class ShowSnackBar(@StringRes val resId: Int) : Effect()
    data class IsLoading(val loading: Boolean) : Effect()
}

