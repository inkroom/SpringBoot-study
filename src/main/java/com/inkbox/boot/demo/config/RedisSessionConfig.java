package com.inkbox.boot.demo.config;

import com.inkbox.boot.demo.handler.KryoRedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
//@AutoConfigureOrder(1)
//@AutoConfigureBefore(RedisHttpSessionConfiguration.class)
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

//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer(RedisOperationsSessionRepository repository) {
//        RedisSerializer<Object> serializer = new KryoRedisSerializer<>(Object.class);
//        repository.setDefaultSerializer(serializer);
//
////        repository.getSessionRedisOperations().
//        return serializer;
////        return new KryoRedisSerializer<>(Object.class);
//
//    }


}
