package com.supplychain.service.provider.auth.support;

import com.supplychain.common.core.exception.BizException;
import com.supplychain.service.provider.auth.config.AppAuthSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AppPasswordHasher {

    private final AppAuthSecurityProperties properties;

    public boolean matches(String rawPassword, String salt, String expectedHash) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(salt) || !StringUtils.hasText(expectedHash)) {
            return false;
        }
        byte[] actualBytes = decodeBase64(encode(rawPassword, salt));
        byte[] expectedBytes = decodeBase64(expectedHash);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    public String encode(String rawPassword, String salt) {
        byte[] saltBytes = decodeBase64(salt);
        PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), saltBytes,
                properties.getPassword().getIterations(), properties.getPassword().getHashBytes() * 8);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (GeneralSecurityException exception) {
            throw new BizException("App 用户密码加密失败");
        } finally {
            spec.clearPassword();
        }
    }

    private byte[] decodeBase64(String value) {
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException exception) {
            throw new BizException("App 用户密码盐值或哈希格式非法");
        }
    }
}
