package com.inkbox.boot.demo.jwt;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * jwt结构
 */
public class JwtDto {


    private JwtHeader header;

    private JwtBody body;

    private String sign;

    public JwtDto() {
    }

    /**
     * 构造wt
     *
     * @param header json
     * @param body   json
     * @param sign   签名
     */
    public JwtDto(String header, String body, String sign) {
        this.sign = sign;
        this.header = new JwtHeader(header);
        this.body = new JwtBody(body);
    }


    public JwtHeader getHeader() {
        return header;
    }

    public void setHeader(JwtHeader header) {
        this.header = header;
    }

    public JwtBody getBody() {
        return body;
    }

    public void setBody(JwtBody body) {
        this.body = body;
    }

    void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtDto dto = (JwtDto) o;
        return header.equals(dto.header) &&
                body.equals(dto.body) &&
                sign.equals(dto.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, body, sign);
    }

    public String toString() {

        return header.toString() + "." + body.toString() + "." + sign;
    }
}
