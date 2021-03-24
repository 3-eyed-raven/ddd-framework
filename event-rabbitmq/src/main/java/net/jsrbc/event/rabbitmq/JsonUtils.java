package net.jsrbc.event.rabbitmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class JsonUtils {

    /**
     * 将对象转为Json字符串
     * @param object  对象
     * @return        Json字符串
     */
    public static String toJson(Object object) {
        try {
            return ObjectMapperHolder.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json转为对象
     * @param jsonStr   Json字符串
     * @param clazz     对象类型
     * @return          对象
     */
    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        try {
            return ObjectMapperHolder.objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json转为集合
     * @param jsonStr        json字符串
     * @param elementType    集合元素类型
     * @return               集合
     */
    public static <E> List<E> toList(String jsonStr, Class<E> elementType) {
        try {
            return ObjectMapperHolder.objectMapper.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, elementType));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json转为Map
     * @param jsonStr     Json字符串
     * @param keyType     键类型
     * @param valueType   值类型
     * @return            LinkedHashMap，有序的
     */
    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> keyType, Class<V> valueType) {
        try {
            return ObjectMapperHolder.objectMapper.readValue(jsonStr, TypeFactory.defaultInstance().constructMapType(LinkedHashMap.class, keyType, valueType));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** 持有ObjectMapper对象，利用JVM进行懒加载 */
    private static class ObjectMapperHolder {

        private final static ObjectMapper objectMapper = new ObjectMapper();

        static {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                    .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                    .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                    .registerModule(new JavaTimeModule())
                    .registerModule(new ParameterNamesModule()); // 可以通过构造器参数识别字段，但是要打开编译参数--parameters
        }
    }

    private JsonUtils() {}
}
