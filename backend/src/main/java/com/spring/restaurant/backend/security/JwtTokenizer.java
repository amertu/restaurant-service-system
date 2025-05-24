package com.spring.restaurant.backend.security;

import com.spring.restaurant.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenizer {

    private final SecurityProperties props;
    private final SecretKey signingKey;

    public JwtTokenizer(SecurityProperties props) {
        this.props = props;
        this.signingKey = Keys.hmacShaKeyFor(
            props.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Mint a signed JWS with standard claims and roles.
     *
     * @param username login name (sub)
     * @param roles    Spring-style role names (“ADMIN,” “USER,” …)
     * @return compact JWT string
     */
    public String getAuthToken(String username, List<String> roles) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
            .header()
            .type(props.getJwtType())
            .and()
            .issuer(props.getJwtIssuer())
            .subject(username)
            .audience().add(props.getJwtAudience()).and()
            .issuedAt(new Date(now))
            .expiration(new Date(now + props.getJwtExpirationTime()))
            .claim("rol", roles)
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Parse and verify a JWS.  Returns true on success or throws on any problem.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .requireIssuer(props.getJwtIssuer())
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT expired", e);
        } catch (UnsupportedJwtException | MalformedJwtException |
                 SecurityException | IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT", e);
        }
    }
}
