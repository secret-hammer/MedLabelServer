package com.vipa.medlabel.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // 生成密钥
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // 将密钥转换为字符串
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        // 输出密钥
        System.out.println(encodedKey);
    }
}