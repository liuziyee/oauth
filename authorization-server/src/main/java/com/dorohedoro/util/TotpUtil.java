package com.dorohedoro.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TotpUtil {
    private static final Long TIME_STEP = 1 * 60L; // 时间步数(可以理解为时间窗口)
    private static final Integer PASSWORD_LENGTH = 6;
    private KeyGenerator keyGenerator;
    private TimeBasedOneTimePasswordGenerator totp;

    {
        totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP), PASSWORD_LENGTH);
        try {
            keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {}
        keyGenerator.init(512);
    }

    public String generateTotp(Key key, Instant time) throws InvalidKeyException {
        String pattern = "%0" + PASSWORD_LENGTH + "d";
        return String.format(pattern, totp.generateOneTimePassword(key, time));
    }

    public Optional<String> generateTotp(String encodedKey) {
        try {
            return Optional.of(generateTotp(decodeKey(encodedKey), Instant.now()));
        } catch (InvalidKeyException e) {
            return Optional.empty();
        }
    }

    public boolean validateTotp(Key key, String code) throws InvalidKeyException {
        return generateTotp(key, Instant.now()).equals(code);
    }

    public Key generateKey() {
        return keyGenerator.generateKey();
    }

    public String encodeKey() {
        return Base64.getEncoder().encodeToString(generateKey().getEncoded());
    }

    public Key decodeKey(String encodedKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(encodedKey), totp.getAlgorithm());
    }
    
    public Duration getTimeStep() {
        return totp.getTimeStep();
    }
}
