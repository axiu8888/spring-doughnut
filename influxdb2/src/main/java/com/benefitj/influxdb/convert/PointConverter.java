package com.benefitj.influxdb.convert;


import com.benefitj.influxdb.dto.Point;

/**
 * Point 转换器
 *
 * @param <T>
 */
public class PointConverter<T> extends AbstractConverter<T, Point> {

  public PointConverter(Class<T> type) {
    super(type);
  }

  @Override
  public Point convert(T record) {
    return convert(this, record);
  }

}
