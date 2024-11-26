package com.ping.support.jwt

import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.support.jwt.JwtConst.TOKEN_ISSUER
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import io.jsonwebtoken.*
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${jwt.secret_key}") private val base64Secret: String,
    @Value("\${jwt.access-token-validity-in-seconds}") accessTokenValiditySeconds: Long,
    @Value("\${jwt.refresh-token-validity-in-seconds}") refreshTokenValiditySeconds: Long
) {

    private val accessTokenTime = Duration.ofSeconds(accessTokenValiditySeconds)
    private val refreshTokenTime = Duration.ofSeconds(refreshTokenValiditySeconds)
    private lateinit var secretKey: SecretKey

    @PostConstruct
    fun initKey() {
        val keyBytes = Decoders.BASE64.decode(base64Secret)
        secretKey = Keys.hmacShaKeyFor(keyBytes)
    }

    fun issue(memberId: Long): String {
        val newAccessToken = createAccessToken(memberId)
        val newRefreshToken = createRefreshToken(memberId)

        saveTokensToRedis(memberId.toString(), newAccessToken, newRefreshToken)

        return newAccessToken
    }

    fun reissue(request: HttpServletRequest): String {
        val accessToken = resolveToken(request)

        val claims = parseClaims(accessToken)

        val jti = claims.id
        val memberId = claims.subject

        if (isBlacklisted(jti)) {
            throw CustomException(ExceptionContent.TOKEN_INVALID)
        }

        val refreshToken = redisTemplate.opsForValue()[memberId]
            ?: throw CustomException(ExceptionContent.REFRESH_TOKEN_EXPIRED)

        val existingAccessToken = redisTemplate.opsForValue()[refreshToken]
        if (existingAccessToken.isNullOrEmpty() || existingAccessToken != accessToken) {
            addBlacklistToken(jti)
            throw CustomException(ExceptionContent.TOKEN_INVALID)
        }

        redisTemplate.opsForSet().add("jti:blacklist", jti)

        val newAccessToken = createAccessToken(memberId.toLong())

        redisTemplate.opsForValue().set(
            refreshToken,
            newAccessToken,
            refreshTokenTime,
        )

        return newAccessToken
    }

    fun validateToken(request: HttpServletRequest): Boolean {
        val token = resolveToken(request)
        runCatching {
            val claims = parseClaims(token)
            val jti = claims.id

            if (isBlacklisted(jti)) {
                throw CustomException(ExceptionContent.TOKEN_BLACKLISTED)
            }

            val refreshToken = redisTemplate.opsForValue()[claims.subject]
                ?: throw CustomException(ExceptionContent.REFRESH_TOKEN_EXPIRED)

            val existingAccessToken = redisTemplate.opsForValue()[refreshToken]
            if (existingAccessToken.isNullOrEmpty() || existingAccessToken != token) {
                addBlacklistToken(jti)
                throw CustomException(ExceptionContent.TOKEN_INVALID)
            }
            return true
        }.getOrElse { exception ->
            return handleTokenExceptions(exception)
        }
    }

    private fun createAccessToken(memberId: Long): String {
        val claims = Jwts.claims().subject(memberId.toString())
        val jti = UUID.randomUUID().toString().substring(0, 16) + memberId.toString()
        val now = Date()

        return Jwts.builder()
            .id(jti)
            .issuer(TOKEN_ISSUER)
            .claims(claims.build())
            .issuedAt(now)
            .expiration(Date(now.time + accessTokenTime.toMillis()))
            .signWith(secretKey)
            .compact()
    }

    private fun createRefreshToken(memberId: Long): String {
        val claims = Jwts.claims().subject(memberId.toString())
        val now = Date()
        return Jwts.builder()
            .issuer(TOKEN_ISSUER)
            .claims(claims.build())
            .issuedAt(now)
            .expiration(Date(now.time + refreshTokenTime.toMillis()))
            .signWith(secretKey)
            .compact()
    }

    private fun saveTokensToRedis(memberId: String, accessToken: String, refreshToken: String) {
        redisTemplate.opsForValue().set(memberId, refreshToken, refreshTokenTime)
        redisTemplate.opsForValue().set(refreshToken, accessToken, refreshTokenTime)
    }

    private fun resolveToken(request: HttpServletRequest): String {
        val jwtToken = request.getHeader("Authorization") ?: throw CustomException(ExceptionContent.TOKEN_MISSING)

        return if (jwtToken.startsWith("Bearer ")) {
            jwtToken.substring(7)
        } else {
            throw CustomException(ExceptionContent.TOKEN_INVALID)
        }
    }

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .requireIssuer(TOKEN_ISSUER)
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .body
        } catch (ex: ExpiredJwtException) {
            ex.claims
        } catch (ex: JwtException) {
            throw CustomException(ExceptionContent.TOKEN_INVALID)
        }
    }

    private fun handleTokenExceptions(exception: Throwable): Boolean {
        return when (exception) {
            is ExpiredJwtException -> throw CustomException(ExceptionContent.TOKEN_EXPIRED)
            is UnsupportedJwtException, is MalformedJwtException -> throw CustomException(ExceptionContent.TOKEN_INVALID)
            else -> false
        }
    }

    private fun isBlacklisted(jti: String): Boolean {
        return redisTemplate.opsForSet().isMember("jti:blacklist", jti) == true
    }

    private fun addBlacklistToken(jti: String) {
        redisTemplate.opsForSet().add("jti:blacklist", jti)
    }
}