package com.appointment.outside.interceptor;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

import java.io.IOException;

/**
 * SpringMvc @responseBody 返回json串null值处理为""
 * Created by lyc on 2017/9/6.
 */
public class ObjectMappingCustomer extends ObjectMapper {

    public ObjectMappingCustomer()
    {
        super();
        // 允许单引号
//        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        // 字段和值都加引号
//        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 数字也加引号
//        this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
//        this.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
        // 空值处理为空串
        DefaultSerializerProvider sp = (DefaultSerializerProvider) this.getSerializerProvider();
        sp.setNullValueSerializer(new JsonSerializer<Object>(){

            @Override
            public void serialize(Object value, JsonGenerator jg,
                                  SerializerProvider sp) throws IOException,
                    JsonProcessingException {
                jg.writeString("");
            }

        });

    }
}
