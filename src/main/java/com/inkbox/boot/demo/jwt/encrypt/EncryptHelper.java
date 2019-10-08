package com.inkbox.boot.demo.jwt.encrypt;

public interface EncryptHelper {

    String encrypt(String value);

    String encrypt(String value, String secret);

}
