package com.github.kentyeh.context;

import org.apache.commons.codec.digest.Crypt;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Kent Yeh
 */
public class Sha512PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        String salt = KeyGenerators.string().generateKey();
        return Crypt.crypt(Utf8.encode(rawPassword.toString()), "$6$" + salt);//長度應該開>=106
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || !encodedPassword.startsWith("$6$")) {
            return false;
        }
        int pos = encodedPassword.lastIndexOf("$");
        String prefix = encodedPassword.substring(0, pos);
        String encodeRawPass = Crypt.crypt(Utf8.encode(rawPassword.toString()), prefix);
        return encodedPassword.equals(encodeRawPass);
    }
}
