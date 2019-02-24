package com.knowledgespike.todolist

import arrow.core.Either
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

interface IOAuthClient {
    fun getClientCredential(
        tokenEndpoint: String,
        clientId: String,
        clientSecret: String,
        scopes: List<String>
    ): Either<Int, TokenResponse>

    fun callApi(apiEndpoint: String, tokenType: String, token: String): Either<Int, String>
}

class OAuthClient : IOAuthClient {
    override fun getClientCredential(
        tokenEndpoint: String,
        clientId: String,
        clientSecret: String,
        scopes: List<String>
    ): Either<Int, TokenResponse> {
        val (_, _, result) = tokenEndpoint.httpPost(
            listOf(
                "grant_type" to "client_credentials",
                "scope" to scopes.joinToString(" ")
            )
        ).authenticate(clientId, clientSecret)
            .responseString()

        return when (result) {
            is Result.Success -> {
                val parser = Parser.default()
                val json = parser.parse(StringBuilder(result.value)) as JsonObject

                Either.Right(
                    TokenResponse(
                        json["access_token"].toString(),
                        json["expires_in"].toString().toInt(),
                        json["token_type"].toString()
                    )
                )
            }
            is Result.Failure -> {
                Either.Left(result.error.response.statusCode)
            }
        }
    }

    override fun callApi(apiEndpoint: String, tokenType: String, token: String): Either<Int, String> {
        val (_, _, result) = apiEndpoint.httpGet().header(Pair("Authorization", "$tokenType $token")).responseString()

        return when (result) {
            is Result.Success -> {
                Either.Right(result.value)
            }
            is Result.Failure -> {
                return Either.Left(result.error.response.statusCode)
            }
        }
    }
}

class TokenResponse(val token: String, val expiresIn: Int, val tokenType: String)

