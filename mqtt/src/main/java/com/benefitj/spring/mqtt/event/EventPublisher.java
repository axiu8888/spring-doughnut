package com.benefitj.spring.mqtt.event;

import com.benefitj.core.ReflectUtils;
import com.benefitj.mqtt.MqttTopic;
import com.benefitj.spring.mqtt.publisher.IMqttPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class EventPublisher {

  /**
   * MQTT消息发布者
   */
  @Autowired
  private IMqttPublisher publisher;
  /**
   * 事件描述符
   */
  private final Map<Class<?>, EventDescriptor> descriptors = new ConcurrentHashMap<>();
  /**
   * 事件转换器
   */
  private final Map<Class<?>, EventConverter> converters = new ConcurrentHashMap<>();
  /**
   * 默认的转换器
   */
  private EventConverter defaultConverter = new JsonEventConverter();

  public EventPublisher() {
  }

  /**
   * 发布消息
   *
   * @param msg 消息
   */
  public void publish(Object msg) {
    EventDescriptor descriptor = findDescriptor(msg.getClass());
    byte[] payload = descriptor.convert(msg);
    List<String> topics = descriptor.getTopics(msg);
    for (String topic : topics) {
      getPublisher().publish(topic, payload);
    }
  }

  protected EventDescriptor findDescriptor(Class<?> type) {
    EventDescriptor descriptor = getDescriptors().get(type);
    if (descriptor != null) {
      return descriptor;
    }
    return this.getDescriptors().computeIfAbsent(type, this::resolve);
  }

  /**
   * 解析事件
   *
   * @param type 类型
   * @return 返回解析的时间描述符
   */
  protected EventDescriptor resolve(Class<?> type) {
    EventTopic eventTopic = getTopicEvent(type);
    if (eventTopic == null) {
      throw new IllegalStateException("不支持的事件类型: " + type.getName());
    }
    EventDescriptor descriptor = new EventDescriptor(type);
    descriptor.setEventTopic(eventTopic);
    for (String pattern : eventTopic.value()) {
      List<Splicer> splicers = SplicerParser.parse(pattern);
      for (Splicer splicer : splicers) {
        if (splicer.isPlaceholder()) {
          Field field = ReflectUtils.getField(type, f -> {
            if (f.isAnnotationPresent(TopicPlaceholder.class)) {
              TopicPlaceholder flag = f.getAnnotation(TopicPlaceholder.class);
              return flag.name().equals(splicer.getName());
            }
            return f.getName().equals(splicer.getName());
          });
          if (field == null) {
            throw new IllegalStateException("无法发现 " + splicer.getName() + " 的字段");
          }
          splicer.setTopicPlaceholder(field.getAnnotation(TopicPlaceholder.class));
          splicer.setField(field);
        }
      }
      descriptor.getSplicers().put(pattern, splicers);
    }

    // mqtt topic
    descriptor.getTopics()
        .addAll(descriptor.getSplicers()
            .values()
            .stream()
            .map(splicers -> splicerToTopic(splicers))
            .collect(Collectors.toList()));

    EventConverter converter = getConverters()
        .getOrDefault(eventTopic.converter(), getDefaultConverter());
    descriptor.setConverter(converter);

    return descriptor;
  }

  protected MqttTopic splicerToTopic(List<Splicer> splicers) {
    String pattern = splicers.stream()
        .map(splicer -> splicer.isPlaceholder() ? "+" : splicer.getSegment())
        .collect(Collectors.joining(""));

    String[] splits = pattern.split("/");
    for (String split : splits) {
      if (split.contains("+") && !"+".equals(split)) {
        throw new IllegalStateException("不支持的 topic [" + pattern + "]，[" + split + "]的占位符需使用/分割");
      }
    }

    return MqttTopic.get(pattern);
  }

  protected EventTopic getTopicEvent(Class<?> type) {
    while (type != Object.class) {
      if (ReflectUtils.isAnnotationPresent(type, EventTopic.class)) {
        return type.getAnnotation(EventTopic.class);
      }
      type = type.getSuperclass();
    }
    return null;
  }

  public IMqttPublisher getPublisher() {
    return publisher;
  }

  public void setPublisher(IMqttPublisher publisher) {
    this.publisher = publisher;
  }

  public Map<Class<?>, EventDescriptor> getDescriptors() {
    return descriptors;
  }

  public Map<Class<?>, EventConverter> getConverters() {
    return converters;
  }

  public EventConverter getDefaultConverter() {
    return defaultConverter;
  }

  public void setDefaultConverter(EventConverter defaultConverter) {
    this.defaultConverter = defaultConverter;
  }

  @Autowired(required = false)
  public void addConverter(List<EventConverter> converters) {
    for (EventConverter converter : converters) {
      getConverters().put(converter.getConverterType(), converter);
    }
  }

}
