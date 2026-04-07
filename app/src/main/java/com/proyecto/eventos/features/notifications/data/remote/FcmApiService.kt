package com.proyecto.eventos.features.notifications.data.remote

import android.content.Context
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmApiService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val projectId = "sweeper-tickets"
    private val fcmUrl = "https://fcm.googleapis.com/v1/projects/$projectId/messages:send"
    private val scope = "https://www.googleapis.com/auth/firebase.messaging"

    private fun getAccessToken(): String {
        // Leer el Service Account JSON desde assets
        val json = context.assets
            .open("sweeper-tickets-firebase-adminsdk-fbsvc-4fab9e4105.json")
            .bufferedReader()
            .use { it.readText() }

        val serviceAccount = JSONObject(json)
        val clientEmail = serviceAccount.getString("client_email")
        val privateKeyStr = serviceAccount.getString("private_key")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .trim()

        // Construir la clave privada RSA
        val keyBytes = Base64.getDecoder().decode(privateKeyStr)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val privateKey = KeyFactory
            .getInstance("RSA")
            .generatePrivate(keySpec) as RSAPrivateKey

        // Crear JWT para obtener el access token
        val now = Date()
        val expiry = Date(now.time + 3600 * 1000)

        val claims = JWTClaimsSet.Builder()
            .issuer(clientEmail)
            .audience("https://oauth2.googleapis.com/token")
            .issueTime(now)
            .expirationTime(expiry)
            .claim("scope", scope)
            .build()

        val signedJWT = SignedJWT(
            JWSHeader(JWSAlgorithm.RS256),
            claims
        )
        signedJWT.sign(RSASSASigner(privateKey))
        val jwtToken = signedJWT.serialize()

        // Intercambiar JWT por access token
        val tokenUrl = URL("https://oauth2.googleapis.com/token")
        val connection = tokenUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        val body = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwtToken"
        connection.outputStream.write(body.toByteArray())

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val responseJson = JSONObject(response)
        return responseJson.getString("access_token")
    }

    suspend fun enviarNotificacion(
        token: String,
        titulo: String,
        cuerpo: String,
        eventoId: String = "",
        nombreEvento: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessToken()

            val mensaje = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", token)
                    put("notification", JSONObject().apply {
                        put("title", titulo)
                        put("body", cuerpo)
                    })
                    put("data", JSONObject().apply {
                        put("titulo", titulo)
                        put("cuerpo", cuerpo)
                        put("eventoId", eventoId)
                        put("nombreEvento", nombreEvento)
                    })
                    put("android", JSONObject().apply {
                        put("priority", "high")
                        put("notification", JSONObject().apply {
                            put("channel_id", "sweeptickets_fcm")
                            put("sound", "default")
                        })
                    })
                })
            }

            val url = URL(fcmUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $accessToken")

            connection.outputStream.write(mensaje.toString().toByteArray())

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                Result.success(Unit)
            } else {
                val error = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Result.failure(Exception("FCM error $responseCode: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}