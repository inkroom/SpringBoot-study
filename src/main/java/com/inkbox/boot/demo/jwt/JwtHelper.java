package com.inkbox.boot.demo.jwt;

import com.inkbox.boot.demo.jwt.encrypt.EncryptHelper;
import com.inkbox.boot.demo.jwt.encrypt.SHA256;
import jdk.internal.org.objectweb.asm.util.CheckAnnotationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.function.BiConsumer;

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

        //构造json字符串
        String header = "{\"alg\":" + dto.getHeader().getAlg() + "\",\"typ\":\"" + dto.getHeader().getTyp() + "\"}";

        StringBuilder bodyBuilder = new StringBuilder("{");
        dto.getBody().getData().forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                bodyBuilder.append("\"").append(s).append("\":\"").append(o.toString()).append("\",");
            }
        });

        //移除最后一个，
        bodyBuilder = bodyBuilder.length() > 1 ? bodyBuilder.deleteCharAt(bodyBuilder.length() - 1) : bodyBuilder;
        bodyBuilder.append("}");


        //Base64URL
        header = new String(Base64.getUrlEncoder().encode(header.getBytes()));
        String body = new String(Base64.getUrlEncoder().encode(bodyBuilder.toString().getBytes()));

        //sign
        dto.setSign(helper.encrypt(header + "." + body, secret));


        return dto;


    }

    /**
     * 校验jwt是否可用
     *
     * @param dto
     * @return 如果签名被篡改，或者过期时间已到则返回null，否则返回实例
     */
    public JwtDto valid(String dto, String secret) {

    }


}
