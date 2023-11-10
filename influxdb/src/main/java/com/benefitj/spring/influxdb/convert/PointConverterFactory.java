package com.benefitj.spring.influxdb.convert;


import com.benefitj.spring.influxdb.dto.Point;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Point 转换器工厂
 */
public class PointConverterFactory extends AbstractConverterFactory<Point> {

  public static final PointConverterFactory INSTANCE = new PointConverterFactory();

  /**
   * converter cache
   */
  private final Map<Class<?>, PointConverter<?>> converters = new ConcurrentHashMap<>();

  public PointConverterFactory() {
  }

  @SuppressWarnings("all")
  @Override
  public <T> PointConverter<T> getConverter(final Class<T> type) {
    return (PointConverter) converters.computeIfAbsent(type, this::parse);
  }

  protected PointConverter<?> parse(Class<?> type) {
    PointConverter<?> converter = new PointConverter<>(type);
    parse(converter);
    return converter;
  }

}
