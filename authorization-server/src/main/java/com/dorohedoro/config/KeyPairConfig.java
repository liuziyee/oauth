package com.dorohedoro.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
@RequiredArgsConstructor
public class KeyPairConfig {

    private final AppProperties appProperties;
    
    @Bean
    public KeyPair keyPair() {
        String keyStore = appProperties.getJwks().getKeyStore();
        String passPhrase = appProperties.getJwks().getPassPhrase();
        String alias = appProperties.getJwks().getAlias();
        
        ClassPathResource keyStoreFile = new ClassPathResource(keyStore);
        KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(keyStoreFile, passPhrase.toCharArray());
        return keyFactory.getKeyPair(alias); // 获取公钥私钥对
    }
}
