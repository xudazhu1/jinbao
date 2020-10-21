package com.hnjbkc.jinbao.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * jackson配置 转null 为 ""
 * @author xudaz
 * @date 2019/2/21
 */
@Configuration
public class JacksonConfig {


    /**
     * 使jackson不序列化懒加载对象
     * @return bean
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = converter.getObjectMapper();
        mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString("");
            }
        });
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        mapper.registerModule(hibernate5Module);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return converter;
    }


    @Bean
    public JPAQueryFactory jpaQuery(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
