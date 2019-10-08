package com.inkbox.boot.demo.jwt;


import java.util.HashMap;
import java.util.Map;

/**
 * Jwt有效载荷
 */
public class JwtBody {


    private String iss;//发行人
    private long exp;//到期时间，时间戳
    private String sub;
    private String aud;//用户
    private long nbf;//在此之前不可用，时间戳
    private long iat;//发布时间，时间戳
    private String jti;//id

    private Map<String, Object> data;//额外载荷

    public JwtBody() {
        data = new HashMap<>();
    }

    Map<String, Object> getData() {
        return data;
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
        data.put("iss", this.iss);
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
        data.put("exp", this.exp);
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
        data.put("sub", this.sub);
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
        data.put("aud", this.aud);
    }

    public long getNbf() {
        return nbf;
    }

    public void setNbf(long nbf) {
        this.nbf = nbf;
        data.put("nbf", this.nbf);
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
        data.put("iat", this.iat);
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
        data.put("jti", this.jti);
    }
}
