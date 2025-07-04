package com.benefitj.spring;

import com.benefitj.core.CatchUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.*;
import java.lang.ref.SoftReference;

/**
 * JSON转化工具
 */
public class JsonUtils {

  private static final ThreadLocal<SoftReference<JsonMapper>> mapperCache =
      ThreadLocal.withInitial(() -> new SoftReference<>(new JsonMapper()));

  /**
   * 获取Mapper
   */
  public static JsonMapper getMapper() {
    JsonMapper mapper = mapperCache.get().get();
    if (mapper == null) {
      synchronized (JsonUtils.class) {
        mapper = new JsonMapper();
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
    return toJson(o, false);
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   * @return 返回转换后的json
   */
  public static String toJson(Object o, boolean pretty) {
    try {
      return pretty
          ? getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o)
          : getMapper().writeValueAsString(o);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   * @return 返回转换后的json
   */
  public static byte[] toJsonBytes(Object o) {
    return toJsonBytes(o, false);
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   * @return 返回转换后的json
   */
  public static byte[] toJsonBytes(Object o, boolean pretty) {
    try {
      return pretty
          ? getMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(o)
          : getMapper().writeValueAsBytes(o);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   */
  public static void toJson(Writer w, Object o) {
    toJson(w, o, false);
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   */
  public static void toJson(Writer w, Object o, boolean pretty) {
    try {
      if (pretty) {
        getMapper().writerWithDefaultPrettyPrinter().writeValue(w, o);
      } else {
        getMapper().writeValue(w, o);
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   */
  public static void toJson(OutputStream out, Object o) {
    toJson(out, o, false);
  }

  /**
   * 转换成JSON
   *
   * @param o 对象
   */
  public static void toJson(OutputStream out, Object o, boolean pretty) {
    try {
      if (pretty) {
        getMapper().writerWithDefaultPrettyPrinter().writeValue(out, o);
      } else {
        getMapper().writeValue(out, o);
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
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
      throw new IllegalStateException(CatchUtils.findRoot(e));
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
  public static <T> T fromJson(byte[] json, TypeReference<T> type) {
    try {
      return getMapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
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
      throw new IllegalStateException(CatchUtils.findRoot(e));
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
  public static <T> T fromJson(String json, TypeReference<T> type) {
    try {
      return getMapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
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
  public static <T> T fromJson(File json, Class<T> type) {
    try {
      return getMapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
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
  public static <T> T fromJson(File json, TypeReference<T> type) {
    try {
      return getMapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成对象
   *
   * @param r    JSON数据
   * @param type 对象类型
   * @param <T>  类型
   * @return 返回转换后的对象
   */
  public static <T> T fromJson(Reader r, Class<T> type) {
    try {
      return getMapper().readValue(r, type);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成对象
   *
   * @param in   JSON数据
   * @param type 对象类型
   * @param <T>  类型
   * @return 返回转换后的对象
   */
  public static <T> T fromJson(InputStream in, Class<T> type) {
    try {
      return getMapper().readValue(in, type);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

}
