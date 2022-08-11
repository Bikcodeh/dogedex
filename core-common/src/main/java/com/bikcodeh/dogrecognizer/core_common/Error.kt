package com.bikcodeh.dogrecognizer.core_common

import androidx.annotation.StringRes
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

private const val HTTP_UNAUTHORIZED = 401

sealed class Error {
    class Server(@StringRes val messageResId: Int) : Error()
    object Connectivity : Error()
    data class Unknown(val message: String) : Error()
}

fun Exception.toError(): Error = when (this) {
    is IOException,
    is UnknownHostException -> Error.Connectivity
    is HttpException -> {
        val error = when (code()) {
            HTTP_UNAUTHORIZED -> R.string.invalid_login
            else -> R.string.error_unknown
        }
        Error.Server(error)
    }
    else -> Error.Unknown(message ?: "")
}