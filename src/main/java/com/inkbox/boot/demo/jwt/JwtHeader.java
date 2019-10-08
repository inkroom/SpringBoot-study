package com.inkbox.boot.demo.jwt;


import com.alibaba.fastjson.JSONObject;

import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JwtHeader {

    private String alg;

    private String typ = "JWT";


    public JwtHeader() {
    }

    /**
     * @param header 已解密的header，json对象
     */
    public JwtHeader(String header) {

        this.alg = JSONObject.parseObject(header).getString("alg");

    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getTyp() {
        return typ;
    }

    public String toString() {
        return Base64.getUrlEncoder().encodeToString(("{\"alg\":\"" + getAlg() + "\",\"typ\":\"" + getTyp() + "\"}").getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtHeader header = (JwtHeader) o;
        return alg.equals(header.alg) &&
                typ.equals(header.typ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alg, typ);
    }
}
