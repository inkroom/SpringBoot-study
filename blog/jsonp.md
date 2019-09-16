
## 实现jsonp跨域通信

> 实现基于jsonp的跨域通信方案


### 原理

> 浏览器对非同源ajax请求有限制，不允许发送跨域请求  
> 目前跨域解决方案有两种   
> - cros配置
> - jsonp请求
>  
> cros为新规范，通过一个head请求询问服务器是否允许跨域，若不允许则被拦截   
> jsonp则为利用浏览器不限制js脚本的同源性，通过动态创建script请求，服务器传递回一个js函数调用语法，浏览器端按照js函数正常调用回调函数
>
 
 
###  实现思路

首先确定服务器端应该如何返回数据

一次正确的jsonp请求，服务器端应该返回如下格式数据

```javascript

jQuery39948237({key:3})

```

其中，`jQuery39948237`为浏览器端要执行的函数名，该函数由ajax库动态创建，并将函数名作为一个请求参数和该次请求的其余参数一并发送，服务器端无需对此参数做过多处理

`{key:3}`为此次请求返回的数据，作为函数参数传递

--- 
其次，服务器端如何处理？

为了兼容jsonp和cros方案，服务器端应该在请求带有函数名参数时返回函数调用，否则正常返回json数据即可

--- 

最后，为了减少代码的侵入，不应该将上述流程放入一个Controller正常逻辑中，应该考虑使用aop实现


### 实现

#### 前端

前端本次使用jquery库~~(本来想用axios库的，但是axios不支持jsonp)~~

代码如下

```javascript

   $.ajax({
        url:'http://localhost:8999/boot/dto',
        dataType:"jsonp",
        success:(response)=>{
            this.messages.push(response);
        }
    })

```

Jquery默认jsonp函数名参数name为**callback**

#### 后端

本次采用aop实现

具体思路为: 给Controller添加后切点，判断request是否有函数名参数，如果有则修改返回的数据，没有则不做处理

而aop又有两种方案

- 常规aop，自己定义切点
- `ResponseBodyAdvice`，Spring提供的可直接用于数据返回的工具类

本次使用第二种方案

----

首先是Controller的接口实现

```java
@RequestMapping("dto")
public Position dto() {
    return new Position(239, 43);
}
```

返回一个复杂类型，Spring会自动对其做json序列化操作

---

然后的`ResponseBodyAdvice`实现

该类全路径为：`org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice`

```java

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
        // 如果返回String类型，media是plain，否则是json，将会经过json序列化，在下方返回纯字符串之后依然会被序列化，就会添上多余的双引号
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
```

使用`@RestControllerAdvice`指明切点


### bug

经过此步骤，理论上即可实现jsonp调用了。

然而实际测试发现，由于Spring json序列化策略的问题，如果返回jsonp字符串，json序列化之后，将会添上一对引号，如下

应该返回

```javascript
Jquery332({"x":239,"y":43})
```
实际返回
```javascript
"Jquery332({\"x\":239,\"y\":43})"

```

导致浏览器端无法正常运行函数

--- 

经多方查找资料后得知

由于在`ResponseBodyAdvice`中修改了实际的返回值类型为`String`，而字符串类型经过`Jackson`序列化后就会加上引号

解决办法为：修改默认的json序列化`MessageConverter`处理逻辑，对于实际是`String`的不做处理

代码如下

```java
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

```

### todo

暂时不明白为什么需要两个类搭配使用

### 代码

具体实现可查阅[github](https://github.com/inkroom/SpringBoot-study/commit/3eb6e25ecc905d8528c0d1efe11ccb818070727e)

