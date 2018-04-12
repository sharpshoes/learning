package org.casper.learning.jwt;

import com.auth0.jwt.JWT;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;


import java.io.UnsupportedEncodingException;


/**
 * @author Casper Yang
 * @Title:
 * @Description:
 * @date 2018/4/1217:34
 */
public class TokenService {

    private static final String secret = "XJIMOOWEREWSD";
    private static final String issuer = "nx";
    private static final long timeout = 10 * 60 * 1000;

    public static final TokenService INSTANCE = new TokenService();

    private TokenService() {

    }

    public String generate(AccessInfo accessInfo) throws UnsupportedEncodingException {

        return JWT.create()
                .withIssuer(issuer)
                .withClaim("issueAt", System.currentTimeMillis())
                .withClaim("expiredAt", System.currentTimeMillis() + timeout)
                .withClaim("uid", accessInfo.getUid())
                .sign(Algorithm.HMAC256(secret));

    }

    public AccessInfo check(String token) throws TokenTimeOutException, TokenNotValidException {

        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);

            long expiredAt = decodedJWT.getClaim("expiredAt").asLong();
            if (expiredAt >= System.currentTimeMillis()) {
                throw new TokenTimeOutException();
            }

            AccessInfo accessInfo = new AccessInfo();
            int uid = decodedJWT.getClaim("uid").asInt();
            accessInfo.setUid(uid);
            return accessInfo;

        } catch (UnsupportedEncodingException e) {
            throw new TokenNotValidException();
        } catch (SignatureVerificationException exception) {
            throw new TokenNotValidException();
        }
    }

    public static class TokenTimeOutException extends Exception {

    }

    public static class TokenNotValidException extends Exception {

    }

    public static void main(String args[]) throws UnsupportedEncodingException {
        AccessInfo accessInfo = new AccessInfo();
        accessInfo.setUid(123);
        String token = TokenService.INSTANCE.generate(accessInfo);
    }
}
