package com.benefitj.examples.controller;


import com.benefitj.aop.AopWebPointCut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AopWebPointCut
@RestController
@RequestMapping("/simple")
public class SimpleController {

  @GetMapping
  public ResponseEntity<?> get(String id) {
    return ResponseEntity.ok("id ==>: " + id);
  }

}
