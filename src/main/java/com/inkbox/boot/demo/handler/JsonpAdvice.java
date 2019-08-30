package com.inkbox.boot.demo.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkbox.boot.demo.controllers.IndexController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理controller返回值，对于有callback值的使用jsonp格式，其余不处理
 */
@RestControllerAdvice(basePackageClasses = IndexController.class)
public class JsonpAdvice implements ResponseBodyAdvice {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper mapper;

    //jquery默认是callback，其余jsonp库可能不一样
    private final String callBackKey = "callback";

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        logger.debug("返回的class={}", aClass);
        return true;
    }

    /**
     * 在此处对返回值进行处理，需要特别注意如果是非String类型，会被Json序列化，从而添加了双引号，解决办法见
     *
     * @param body               返回值
     * @param methodParameter    方法参数
     * @param mediaType          当前contentType，非String类型为json
     * @param aClass             convert的class
     * @param serverHttpRequest  request，暂时支持是ServletServerHttpRequest类型，其余类型将会原样返回
     * @param serverHttpResponse response
     * @return 如果body是String类型，加上方法头后返回，如果是其他类型，序列化后返回
     * @see com.inkbox.boot.demo.converter.Jackson2HttpMessageConverter
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        if (body == null)
            return null;
        //TODO 2019/8/30 如果返回String类型，media是plain，否则是json，将会经过json序列化，在下方返回纯字符串之后依然会被序列化，就会添上多余的双引号
        logger.debug("body={},request={},response={},media={}", body, serverHttpRequest, serverHttpResponse, mediaType.getSubtype());


        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

            String callback = request.getParameter(callBackKey);

            if (!StringUtils.isEmpty(callback)) {
                //使用了jsonp
                if (body instanceof String) {
                    return callback + "(\"" + body + "\")";
                } else {
                    try {
                        String res = mapper.writeValueAsString(body);
                        logger.debug("转化后的返回值={},{}", res, callback + "(" + res + ")");

                        return callback + "(" + res + ")";
                    } catch (JsonProcessingException e) {
                        logger.warn("【jsonp支持】数据body序列化失败", e);
                        return body;
                    }
                }
            }
        } else {
            logger.warn("【jsonp支持】不支持的request class  ={}", serverHttpRequest.getClass());
        }
        return body;
    }
}
