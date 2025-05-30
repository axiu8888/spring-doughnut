package com.benefitj.examples.controller;


import com.benefitj.core.EventLoop;
import com.benefitj.spring.aop.web.AopWebPointCut;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


@AopWebPointCut
@RestController
@RequestMapping("/sse")
public class SSEController implements InitializingBean, DisposableBean {

  // 保存所有连接的客户端
  private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
  private final AtomicReference<ScheduledFuture<?>> taskRef = new AtomicReference<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    taskRef.set(EventLoop.asyncIOFixedRate(() -> {
      //响应
      sendEventToAllClients("ping", "pong...");
    }, 1, 1, TimeUnit.SECONDS));
  }

  @Override
  public void destroy() throws Exception {
    EventLoop.cancel(taskRef.getAndSet(null));
  }

  // 客户端连接端点
  @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(@ApiParam("请求ID") String id) {
    final SseEmitter emitter = new SseEmitter(0L); // 0表示不超时
    // 添加新的emitter到列表
    emitters.add(emitter);
    // 移除完成或超时的emitter
    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    emitter.onError((e) -> emitters.remove(emitter));
    return emitter;
  }

  // 发送消息给所有客户端
  public void sendEventToAllClients(String eventName, Object data) {
    for (SseEmitter emitter : emitters) {
      try {
        emitter.send(
            SseEmitter.event()
                .name(eventName) // 事件名称
                .data(data) // 事件数据
        );
      } catch (IOException e) {
        emitter.completeWithError(e);
        emitters.remove(emitter);
      }
    }
  }

}

