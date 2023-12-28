package com.kristianskokars.simplecatapp.core

/**
 * Type for representing a Success without returning data.
 */
typealias Ok = Unit

/**
 * Response class for encapsulating operation success or errors, allowing exceptions to be
 * handled explicitly from the method signature.
 *
 * The [E] type represents the [Err] data, the [S] type represents the [Success] data. One can use
 * the [Ok] typealias if there is no specific data to be returned.
 *
 * It is functionally similar to Arrow's Either class, however this is more explicit on dealing
 * with Responses/Errors.
 * @param E The error data type.
 * @param S The success data type.
 */
sealed class Response<out E, out S> {
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
        is Success -> onSuccess(value)
    }
}

/**
 * A [E] response, indicating an error.
 *
 * @param E Error type in a [Response].
 * @property value Error data in a [Response].
 */
class Err<out E>(val value: E) : Response<E, Nothing>()

/**
 * A [S] response, indicating success.
 *
 * @param S Success type in a [Response].
 * @property value Success data in a [Response].
 */
class Success<out S>(val value: S) : Response<Nothing, S>()

/**
 * Default [Success] response with a [Ok] data type.
 */
fun Success() = Success(Ok)
