package com.inkbox.boot.demo.jwt.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 implements EncryptHelper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public String encrypt(String value) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {


            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(value.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return encodestr;
    }

    public String encrypt(String value, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] result = sha256_HMAC.doFinal(value.getBytes());
            return DatatypeConverter.printHexBinary(result);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
