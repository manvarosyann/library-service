package org.example.authservice.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class ServiceTokenIssuer {

    public String issueFor(String subjectService, List<String> audienceServices, long ttlSeconds) throws JOSEException {
        var now = Instant.now();
        var claims = new JWTClaimsSet.Builder()
                .subject(subjectService)
                .issuer("auth-service")
                .audience(audienceServices)
                .claim("roles", List.of("Service"))
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(ttlSeconds)))
                .build();

        var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner(JwtKeys.hmacKey().getEncoded()));
        return jwt.serialize();
    }
}
