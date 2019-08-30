package com.inkbox.boot.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * 解决返回字符串时再次被json转换，最后多出双引号问题
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter converter = mappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(new LinkedList<MediaType>() {{
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON_UTF8);
        }});
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(converter);
    }
}