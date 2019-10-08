package com.inkbox.boot.demo;


import com.inkbox.boot.demo.jwt.JwtBody;
import com.inkbox.boot.demo.jwt.JwtDto;
import com.inkbox.boot.demo.jwt.JwtHeader;
import com.inkbox.boot.demo.jwt.JwtHelper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试jwt生成
 *
 * @author 墨盒
 * @date 2019/10/8
 */
public class JwtTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws Exception {

        //构造jwt
        JwtDto dto = new JwtDto();
        JwtHeader header = new JwtHeader();
        header.setAlg("HS256");

        dto.setHeader(header);

        JwtBody body = new JwtBody();
        body.setJti("jti");
        body.setIat(33344);
        body.setNbf(30005);
        body.setAud("aud");
        body.setSub("sub");
        body.setExp(4444222);
        body.setIss("iss");

        body.put("id", "id");

        body.put("count", 3223L);


        dto.setBody(body);

        JwtHelper helper = new JwtHelper();


        logger.debug("value={}", helper.sign(dto, "5333553"));

        JwtDto newDto = helper.valid("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhdWQiLCJzdWIiOiJzdWIiLCJuYmYiOiIzMDAwNSIsImlzcyI6ImlzcyIsImNvdW50IjoiMzIyMyIsImlkIjoiaWQiLCJleHAiOiI0NDQ0MjIyIiwiaWF0IjoiMzMzNDQiLCJqdGkiOiJqdGkifQ==.0E2A1C70313144518F9262B8569D482BF17CD29632B18EECA01733B6D42267AC", "5333553");
        logger.debug("dto length = {},newDto length={}", dto.toString().length(), newDto.toString().length());
        Assert.assertEquals(dto.toString(), newDto.toString());

        Assert.assertNull(helper.valid("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhdWQiLCJzdWIiOiJzdWIiLCJuYmYiOiIzMDAwNSIsImlzcyI6ImlzcyIsImNvdW50IjoiMzIyMyIsImlkIjoiaWQiLCJleHAiOiI0NDQ0MjIyIiwiaWF0IjoiMzMzNDQiLCJqdGkiOiJqdGkifQ==.0E2A1C70313144518F9262B8569D482BF17CD29632B18EECA01733B6D42267AC", "444"));

    }


}
