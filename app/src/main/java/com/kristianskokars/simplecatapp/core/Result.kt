package com.kristianskokars.simplecatapp.core

/**
 * Type for representing a Success without returning data.
 */
typealias Success = Unit

/**
 * Result class for encapsulating operation success or errors, allowing exceptions to be
 * handled explicitly from the method signature.
 *
 * The [E] type represents the [Err] data, the [S] type represents the [Ok] data. One can use
 * the [Success] typealias if there is no specific data to be returned.
 *
 * It is functionally similar to Arrow's Either class.
 * @param E The error data type.
 * @param S The success data type.
 */
sealed class Result<out E, out S> {
    /**
     * Gets the [Err] error value or null if there isn't an error.
     */
    fun failure() = when (this) {
        is Err -> value
        else -> null
    }

    /**
     * Handles the result from a Response type, allowing to handle both the Right and Left.
     */
    inline fun <T> handle(onSuccess: (S) -> T, onError: (E) -> T): T = when (this) {
        is Err -> onError(value)
        is Ok -> onSuccess(value)
    }
}

/**
 * A [E] response, indicating an error.
 *
 * @param E Error type in a [Result].
 * @property value Error data in a [Result].
 */
class Err<out E>(val value: E) : Result<E, Nothing>()

/**
 * A [S] response, indicating success.
 *
 * @param S Success type in a [Result].
 * @property value Success data in a [Result].
 */
class Ok<out S>(val value: S) : Result<Nothing, S>()

/**
 * Default [Ok] response with a [Success] data type.
 */
fun Ok() = Ok(Success)
