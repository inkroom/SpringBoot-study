package com.inkbox.boot.demo.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

@Component
public class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof String) {
            //绕开实际上返回的String类型，不序列化
            Charset charset = this.getDefaultCharset();
            StreamUtils.copy((String) object, charset, outputMessage.getBody());
        } else {
            super.writeInternal(object, type, outputMessage);
        }
    }
}
