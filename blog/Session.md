
## 实现分布式session

> 实现基于redis的分布式session

---


### 原理

基于HttpRequestWapper，对request获取的Session实现类进行替换，即提供一个从redis获取数据的Session实现类

### 依赖

引入**spring-boot-starter-data-redis**、**spring-session-data-redis**；
引入`kryo`作为序列化方案

```xml
    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

     <dependency>
        <groupId>com.esotericsoftware</groupId>
        <artifactId>kryo</artifactId>
        <version>4.0.0</version>
    </dependency>

    <dependency>
        <groupId>de.javakaffee</groupId>
        <artifactId>kryo-serializers</artifactId>
        <version>0.41</version>
    </dependency>

```

### 修改配置

```properties

spring.redis.host=127.0.0.1
spring.redis.database=2

```

ps: 如果有需要还可以修改对应的连接池配置，或者更换默认的`lettuce`框架


### 编写序列化类

该类用于数据的序列化和反序列化，本样例基于`kryo`实现，该框架具有体积小、速度快等优势。

此步骤可以省略，不提供具体实现Spring默认使用Jdk序列化方案

---- 

序列化需要实现`org.springframework.data.redis.serializer.RedisSerializer`类

该类可以用于不同的存储方案

demo如下

```java

public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    private static final Logger logger = LoggerFactory.getLogger(KryoRedisSerializer.class);

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);

    private Class<T> clazz;

    public KryoRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return EMPTY_BYTE_ARRAY;
        }

        logger.debug("序列化{}，{}", t.getClass(), t);

        Kryo kryo = kryos.get();
        kryo.setRegistrationRequired(false);//关闭注册行为，避免相同类无法强转
        kryo.setReferences(false);
//        kryo.register(clazz);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, t);
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return EMPTY_BYTE_ARRAY;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }

        Kryo kryo = kryos.get();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
//        kryo.register(clazz);

        logger.debug("反序列化");
        try (Input input = new Input(bytes)) {
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}

```

**ps: 需要注意的是，虽然该类提供了一个泛型对象，但是实际运用中并没有什么用；在反序列化时并不能知道应该返回一个什么类型的对象；因此多数序列化框架都是采取的在序列化结果中存储该对象的实际类型；**

再ps：由于`kryo`序列化后为二进制，因此对于List、Map等可能带有泛型，且存储对象并非同一个子类的情况还需要进行测试

### 注入Spring容器

Spring Session通过` org.springframework.session.data.redis.RedisOperationsSessionRepository ` 实现Session的替换，以及数据的序列化

仔细查看该类可知，该类有两个较为重要的属性

- `RedisSerializer<Object> defaultSerializer`
- `RedisOperations<Object, Object> sessionRedisOperations`

--- 
defaultSerializer初始化为`JdkSerializationRedisSerializer`

用于`onMessage`方法，该方法可能为Redis值过期事件响应，负责对传递过来的数据做session删除和过期操作

不太明白为什么不从`sessionRedisOperations`中获取序列化实例，这点有待研究

--- 

sessionRedisOperations在构造方法中传入

由于Spring Bean注入顺序的原因，该值为Spring redis starter自动创建的`RedisTemplate`实例，该实例中使用了Jdk序列化方案，需要修改，但是暂时没找到办法注入自己创建的实例

故采取折中方案，即获取`RedisOperationsSessionRepository`实例，手动修改里面的`RedisTemplate`里的·`RedisSerializer`

--- 

最终配置类如下

```java
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisOperationsSessionRepository repository) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<Object> serializer = new KryoRedisSerializer<>(Object.class);
        // redis value使用的序列化器
        template.setValueSerializer(serializer);
        // redis key使用的序列化器
        template.setKeySerializer(new StringRedisSerializer());

        repository.setDefaultSerializer(serializer);
        //由于RedisOperationsSessionRepository 要先构造，且不提供方法修改属性，只能采取这种这种的方法
        RedisOperations<Object, Object> sessionRedisOperations = repository.getSessionRedisOperations();
        if (sessionRedisOperations instanceof  RedisTemplate){
            RedisTemplate<Object,Object> redisTemplate = ((RedisTemplate<Object, Object>) sessionRedisOperations);
            redisTemplate.setValueSerializer(serializer);
            redisTemplate.setHashValueSerializer(serializer);
        }

        template.afterPropertiesSet();
        return template;
    }
}
```


ps： 更多配置信息亦可通过`RedisOperationsSessionRepository`修改，如session有效实现，cookie name值等等



### todo

- 寻找更合适的注入`Serializer`方式


### 代码

具体实现可查阅[github](https://github.com/inkroom/SpringBoot-study/commit/5dafbfe8e24ecb9df001b9ae3554a5c5d216d477)

