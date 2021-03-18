package com.benefitj.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.ref.SoftReference;

/**
 * JSON转化工具
 */
public class JsonUtils {

  private static final ThreadLocal<SoftReference<ObjectMapper>> mapperCache =
      ThreadLocal.withInitial(() -> new SoftReference<>(new ObjectMapper()));

  /**
   * 获取Mapper
   */
  public static ObjectMapper getMapper() {
    ObjectMapper mapper = mapperCache.get().get();
    if (mapper == null) {
      synchronized (JsonUtils.class) {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapperCache.set(new SoftReference<>(mapper));
      }
    }
    return mapper;
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   * @return 返回转换后的json
   */
  public static String toJson(Object o) {
    try {
      return getMapper().writeValueAsString(o);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   * @return 返回转换后的json
   */
  public static byte[] toJsonBytes(Object o) {
    try {
      return getMapper().writeValueAsBytes(o);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 转换成对象
   *
   * @param json JSON数据
   * @param type 对象类型
   * @param <T>  类型
   * @return 返回转换后的对象
   */
  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return getMapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 转换成对象
   *
   * @param json JSON数据
   * @param type 对象类型
   * @param <T>  类型
   * @return 返回转换后的对象
   */
  public static <T> T fromJson(byte[] json, Class<T> type) {
    try {
      return getMapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
