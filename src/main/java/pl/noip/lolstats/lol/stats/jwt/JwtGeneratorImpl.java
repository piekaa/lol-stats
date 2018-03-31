package pl.noip.lolstats.lol.stats.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.noip.lolstats.lol.stats.time.TimeService;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtGeneratorImpl implements JwtGenerator {

    @Value("${jwt.secret}")
    private String secret;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private TimeService timeService;

    public JwtGeneratorImpl(TimeService timeService) {
        this.timeService = timeService;
    }

    @Override
    public String generate() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = timeService.getMillisSinceEpoch();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = secret.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey)
//                .setExpiration(new Date(now.getTime() + 60 * 60 * 1000));
                .setExpiration(new Date(now.getTime()));


        return builder.compact();
    }
}
