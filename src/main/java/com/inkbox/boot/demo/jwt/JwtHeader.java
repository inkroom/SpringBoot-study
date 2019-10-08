package com.inkbox.boot.demo.jwt;


public class JwtHeader {

    private String alg;

    private String typ = "JWT";

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getTyp() {
        return typ;
    }
}
