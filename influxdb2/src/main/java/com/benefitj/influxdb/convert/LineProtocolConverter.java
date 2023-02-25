package com.benefitj.influxdb.convert;

/**
 * 行协议转换器
 *
 * @param <T>
 */
public class LineProtocolConverter<T> extends AbstractConverter<T, String> {

  public LineProtocolConverter(Class<T> type) {
    super(type);
  }

  @Override
  public String convert(T record) {
    return convert(this, record).lineProtocol();
  }

}
