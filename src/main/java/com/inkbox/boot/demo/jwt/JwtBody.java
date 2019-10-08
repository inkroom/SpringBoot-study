package com.inkbox.boot.demo.jwt;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jwt有效载荷
 */
public class JwtBody {

    private Logger logger = LoggerFactory.getLogger(getClass());

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


    public JwtBody(String body) {

        this();

        logger.debug("body={}", body);


        JSONObject jsonObject = JSONObject.parseObject(body);

//自定义的key
        jsonObject.forEach((s, o) -> data.put(s, o));

        setIss(jsonObject.getString("iss"));
        setExp(jsonObject.getLong("exp"));
        setSub(jsonObject.getString("sub"));
        setAud(jsonObject.getString("aud"));
        setNbf(jsonObject.getLong("nbf"));
        setIat(jsonObject.getLong("iat"));
        setJti(jsonObject.getString("jti"));



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

    @Override
    public String toString() {
        StringBuilder bodyBuilder = new StringBuilder("{");
        StringBuilder finalBodyBuilder = bodyBuilder;
        getData().forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                finalBodyBuilder.append("\"").append(s).append("\":\"").append(o.toString()).append("\",");
            }
        });

        //移除最后一个，
        if (bodyBuilder.length() > 1) {
            bodyBuilder.deleteCharAt(bodyBuilder.length() - 1);
        }
        bodyBuilder.append("}");

        return Base64.getUrlEncoder().encodeToString(bodyBuilder.toString().getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtBody body = (JwtBody) o;
        return exp == body.exp &&
                nbf == body.nbf &&
                iat == body.iat &&
                Objects.equals(logger, body.logger) &&
                iss.equals(body.iss) &&
                sub.equals(body.sub) &&
                aud.equals(body.aud) &&
                jti.equals(body.jti) &&
                data.equals(body.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logger, iss, exp, sub, aud, nbf, iat, jti, data);
    }
}
