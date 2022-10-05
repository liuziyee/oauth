package com.dorohedoro;

import com.dorohedoro.util.TotpUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TotpUtilTest {
    
    @Autowired
    private TotpUtil totpUtil;
    
    @Test
    public void generateTotp() throws InvalidKeyException, InterruptedException {
        Instant now = Instant.now();
        Instant nowPlusTimeStep = now.plus(totpUtil.getTimeStep()); // 下一时间窗口

        Key key = totpUtil.generateKey();
        String firstTotp = totpUtil.generateTotp(key, now);

        TimeUnit.SECONDS.sleep(5);
        String secondTotp = totpUtil.generateTotp(key, Instant.now());
        assertEquals(firstTotp, secondTotp, "同一时间窗口生成的验证码相同");

        String thirdTotp = totpUtil.generateTotp(key, nowPlusTimeStep);
        assertNotEquals(firstTotp, thirdTotp, "不同时间窗口生成的验证码不同");

        Key newKey = totpUtil.generateKey();
        String fourthTotp = totpUtil.generateTotp(newKey, Instant.now());
        assertNotEquals(firstTotp, fourthTotp, "同一时间窗口不同密钥生成的验证码不同");
    }
}
