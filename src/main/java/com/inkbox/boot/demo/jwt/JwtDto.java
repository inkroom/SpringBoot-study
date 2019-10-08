package com.inkbox.boot.demo.jwt;

/**
 * jwt结构
 */
public class JwtDto {


    private JwtHeader header;


    private JwtBody body;


    private String sign;


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
}
