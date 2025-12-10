package com.benefitj.examples.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.Utils;
import com.benefitj.event.EventBusPoster;
import com.benefitj.event.RawEvent;
import com.benefitj.examples.vo.IdEvent;
import com.benefitj.examples.vo.MultipartForm;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.log.HttpLoggingIgnore;
import com.benefitj.spring.aop.ratelimiter.AopRateLimiter;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.eventbus.event.NameEvent;
import com.benefitj.spring.mvc.jsonbody.JsonBodyRequest;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "测试demo")
@AopWebPointCut
@RestController
@RequestMapping("/simple")
public class SimpleController {

  @Autowired
  EventBusPoster poster;

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  @ApiOperation("限流: 5")
  @AopRateLimiter(qps = 5, timeout = 10, timeoutUnit = TimeUnit.SECONDS)
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

  @HttpLoggingIgnore
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

  @ApiOperation("测试POST")
  @PostMapping("/testPost")
  public JSONObject testPost(HttpServletRequest request) throws IOException {
    ServletInputStream in = request.getInputStream();
    String bodyStr = IOUtils.readFully(in).toString(StandardCharsets.UTF_8);
    return JSON.parseObject(bodyStr);
  }

  @ApiOperation("测试POST2")
  @PostMapping("/testPost2")
  public JSONObject testPost2(@RequestBody JSONObject body) {
    return body;
  }

  @ApiOperation("测试 redis")
  @PostMapping("/testRedis")
  public JSONObject testRedis(HttpServletRequest request, @RequestBody JSONObject body) {
    String key = body.getString("key");
    String value = body.getString("value");
    redisTemplate.opsForValue()
        .set(key, value);
    return new JSONObject() {{
      put("code", 200);
      put("msg", "success");
    }};
  }

  @JsonBodyRequest
  @ApiOperation("测试electron流上传")
  @PostMapping("/uploadStream")
  public JSONObject testUploadStream(HttpServletRequest request,
                                     @ApiParam("文件名") @RequestParam String filename) throws IOException {
    File dest = IOUtils.createFile("D:/cache/.tmp/" + filename);
    try {
      ServletInputStream in = request.getInputStream();
      IOUtils.write(in, dest);
      return new JSONObject() {{
        put("code", 200);
        put("msg", "success");
        put("data", new JSONObject(){{
          put("path", dest.getAbsolutePath().replace("\\", "/"));
          put("size", dest.length());
        }});
      }};
    } finally {
      if (dest.length() <= 0) {
        IOUtils.delete(dest);
      }
    }
  }

  @ApiOperation("测试 write")
  @PostMapping(value = "/write", consumes = {"application/octet-stream;charset=UTF-8"})
  public void write(HttpServletRequest request) throws IOException {
    ServletInputStream in = request.getInputStream();
    IOUtils.write(in, IOUtils.createFile("D:/cache/.tmp/znsx/temp", IdUtils.uuid() +".line"));
  }

//  @ApiOperation("测试JsonParams")
//  @PostMapping(value = "/testJsonParams")
//  public void testJsonParams(HttpServletRequest request, HttpServletResponse response) throws IOException {
//    String str = IOUtils.readFully(request.getInputStream()).toString(StandardCharsets.UTF_8);
//    JSONObject jsonBody = JsonParamFilter.get().getJsonBody();
//    //request.getAttribute(JsonParamFilter.KEY_JSONBODY);
//    ServletUtils.write(response, 200, JsonUtils.toJsonBytes(new LinkedHashMap(){{
//      put("raw", str);
//      put("jsonBody", jsonBody);
//    }}));
//  }

}
