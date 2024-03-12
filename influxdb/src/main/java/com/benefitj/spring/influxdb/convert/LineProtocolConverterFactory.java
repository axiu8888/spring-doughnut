package com.benefitj.spring.influxdb.convert;

import com.benefitj.core.SingletonSupplier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LineProtocol Converter Factory
 */
public class LineProtocolConverterFactory extends AbstractConverterFactory<String> {

  static final SingletonSupplier<LineProtocolConverterFactory> singleton = SingletonSupplier.of(LineProtocolConverterFactory::new);

  public static LineProtocolConverterFactory get() {
    return singleton.get();
  }

  /**
   * converter cache
   */
  private final Map<Class<?>, LineProtocolConverter<?>> converters = new ConcurrentHashMap<>();

  public LineProtocolConverterFactory() {
  }

  @SuppressWarnings("all")
  @Override
  public <T> LineProtocolConverter<T> getConverter(Class<T> type) {
    return (LineProtocolConverter<T>) converters.computeIfAbsent(type, this::parse);
  }

  protected LineProtocolConverter<?> parse(Class<?> type) {
    LineProtocolConverter<?> converter = new LineProtocolConverter<>(type);
    parse(converter);
    return converter;
  }
}
