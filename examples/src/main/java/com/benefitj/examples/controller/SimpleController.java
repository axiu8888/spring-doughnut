package com.benefitj.examples.controller;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.Utils;
import com.benefitj.core.IOUtils;
import com.benefitj.event.EventBusPoster;
import com.benefitj.event.RawEvent;
import com.benefitj.examples.vo.IdEvent;
import com.benefitj.examples.vo.MultipartForm;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.AopIgnore;
import com.benefitj.spring.aop.ratelimiter.AopRateLimiter;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.eventbus.event.NameEvent;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.PageBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@Api(tags = "测试demo")
@AopWebPointCut
@RestController
@RequestMapping("/simple")
public class SimpleController {

  @Autowired
  private EventBusPoster poster;

  @ApiOperation("限流: 5")
  @AopRateLimiter(qps = 5)
  @GetMapping("rateLimiter")
  public ResponseEntity<?> testRateLimiter(String id) {
    return ResponseEntity.ok("rateLimiter ==>: " + id + "\r\n" + "ip: " + ServletUtils.getIp() + "\n" + JSON.toJSONString(ServletUtils.getHeaderMap()));
  }

  @ApiOperation("获取ID: 测试EventBus")
  @GetMapping
  public ResponseEntity<?> get(String id) {
    poster.postSync(RawEvent.of(id));
    poster.post(new IdEvent(id));
    poster.post(NameEvent.of("id", id + " by name"));
    poster.post(NameEvent.of("id22", id)); // 不可达消息
    System.err.println("请求信息: " + JSON.toJSONString(ServletUtils.getRequestInfo()));
    return ResponseEntity.ok("id ==>: " + id);
  }

  @AopIgnore
  @ApiOperation("忽略EventBus")
  @GetMapping("/notPrint")
  public ResponseEntity<?> notPrint(String id) {
    return ResponseEntity.ok("id ==>: " + id);
  }


  @ApiOperation("GET表单")
  @GetMapping("/form")
  public ResponseEntity<?> form(/*@GetBody*/ String id1, MultipartForm form, String id2) {
    System.err.println("id1 ==>: " + id1);
    System.err.println("id2 ==>: " + id2);
    return ResponseEntity.ok("form ==>: " + JSON.toJSONString(form));
  }

  @ApiOperation("GET Page转换")
  @GetMapping("/page")
  public ResponseEntity<?> page(String id1, @PageBody PageRequest<MultipartForm> form, String id2) {
    System.err.println("id1 ==>: " + id1);
    System.err.println("id2 ==>: " + id2);
    return ResponseEntity.ok("form ==>: " + JSON.toJSONString(form));
  }

  @ApiOperation("上传文件")
  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files) throws IOException {
    for (MultipartFile file : files) {
      log.info("上传文件: {}, {}MB", file.getOriginalFilename(), Utils.ofMB(file.getSize(), 2));
      file.transferTo(IOUtils.createFile("D:/tmp/", file.getOriginalFilename()));
    }
    return ResponseEntity.ok("上传成功");
  }


  @ApiOperation("下载文件")
  @GetMapping("/download")
  public void downloadFile(@RequestParam("filename") String filename) throws IOException {
    File file = new File("D:/tmp/", filename);
    HttpServletResponse response = ServletUtils.getResponse();
    if (file.exists() && file.isFile()) {
      ServletUtils.download(ServletUtils.getRequest(), response, file, filename);
    } else {
      if (file.isDirectory()) {
        response.sendError(400, "无法下载目录");
      } else {
        response.sendError(400, "文件不存在");
      }
      response.flushBuffer();
    }
  }

}
