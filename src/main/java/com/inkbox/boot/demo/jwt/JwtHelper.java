package com.inkbox.boot.demo.jwt;

import com.alibaba.fastjson.JSONObject;
import com.inkbox.boot.demo.jwt.encrypt.EncryptHelper;
import com.inkbox.boot.demo.jwt.encrypt.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 负责解析，构建jwt
 */
public class JwtHelper {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public JwtDto sign(JwtDto dto, String secret) {

        EncryptHelper helper = null;
        //获取算法
        if (!dto.getHeader().getAlg().equals("HS256")) {
            throw new IllegalArgumentException("不支持的算法");
        }
        helper = new SHA256();
//        //构造json字符串
//        String header = "{\"alg\":" + dto.getHeader().getAlg() + "\",\"typ\":\"" + dto.getHeader().getTyp() + "\"}";
//
//        StringBuilder bodyBuilder = new StringBuilder("{");
//        dto.getBody().getData().forEach(new BiConsumer<String, Object>() {
//            @Override
//            public void accept(String s, Object o) {
//                bodyBuilder.append("\"").append(s).append("\":\"").append(o.toString()).append("\",");
//            }
//        });
//
//        //移除最后一个，
//        bodyBuilder = bodyBuilder.length() > 1 ? bodyBuilder.deleteCharAt(bodyBuilder.length() - 1) : bodyBuilder;
//        bodyBuilder.append("}");
//
//
//        //Base64URL
//        header = new String(Base64.getUrlEncoder().encode(header.getBytes()));
//        String body = new String(Base64.getUrlEncoder().encode(bodyBuilder.toString().getBytes()));

        //sign
        dto.setSign(helper.encrypt(dto.getHeader().toString() + "." + dto.getBody().toString(), secret));
        return dto;
    }

    /**
     * 校验jwt是否可用
     *
     * @param dto
     * @return 如果签名被篡改，或者过期时间已到则返回null，否则返回实例
     */
    public JwtDto valid(String dto, String secret) {
        String[] values = dto.split("\\.");
        if (values.length != 3) return null;

        String header = values[0];
        String body = values[1];
        String sign = values[2];


        //获取算法
        EncryptHelper helper = null;
        //获取算法
        if (!"HS256".equals(JSONObject.parseObject(new String(Base64.getUrlDecoder().decode(header))).getString("alg"))) {
            throw new IllegalArgumentException("不支持的算法");
        }
        helper = new SHA256();
        //判断是否被篡改
        if (sign.equals(helper.encrypt(header + "." + body, secret))) {
//构造jwt
            return new JwtDto(new String(Base64.getUrlDecoder().decode(header)), new String(Base64.getUrlDecoder().decode(body)), sign);
        }
        return null;
    }

    /**
     * 获取hash算法
     *
     * @param header jwt header json对象
     * @return
     */
    private String getMethod(String header) {

        Pattern pattern = Pattern.compile("\"alg\":\"([^\"]\")");
        Matcher matcher = pattern.matcher(header);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
