package com.benefitj.spring.influxdb.convert;


import com.benefitj.core.SingletonSupplier;
import com.benefitj.spring.influxdb.dto.Point;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Point 转换器工厂
 */
public class PointConverterFactory extends AbstractConverterFactory<Point> {

  static final SingletonSupplier<PointConverterFactory> singleton = SingletonSupplier.of(PointConverterFactory::new);

  public static PointConverterFactory get() {
    return singleton.get();
  }

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
