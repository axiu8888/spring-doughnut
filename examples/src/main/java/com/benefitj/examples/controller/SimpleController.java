package com.benefitj.examples.controller;

import com.alibaba.fastjson.JSON;
import com.benefitj.event.EventBusPoster;
import com.benefitj.event.RawEvent;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.AopIgnore;
import com.benefitj.spring.aop.AopWebPointCut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AopWebPointCut
@RestController
@RequestMapping("/simple")
public class SimpleController {

  @Autowired
  private EventBusPoster poster;

  @GetMapping
  public ResponseEntity<?> get(String id) {
    poster.postSync(RawEvent.of(id));
    System.err.println("请求信息: " + JSON.toJSONString(ServletUtils.getRequestInfo()));
    return ResponseEntity.ok("id ==>: " + id);
  }

  @AopIgnore
  @GetMapping("/notPrint")
  public ResponseEntity<?> notPrint(String id) {
    return ResponseEntity.ok("id ==>: " + id);
  }

}
