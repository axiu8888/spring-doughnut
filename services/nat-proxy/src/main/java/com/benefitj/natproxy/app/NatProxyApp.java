package com.benefitj.natproxy.app;


import com.benefitj.natproxy.udp.EnableUdpProxy;
import com.benefitj.spring.HttpResult;
import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableUdpProxy
@EnableSwaggerApi
@EnableAutoAopWebHandler
@SpringBootApplication
public class NatProxyApp {
  public static void main(String[] args) {
    SpringApplication.run(NatProxyApp.class, args);
  }

  @Api(tags = "NAT接口")
  @RestController
  @RequestMapping("/nat-proxy")
  public static class NatApiController {

    @ApiOperation("创建TCP代理")
    @GetMapping("/tcp")
    public HttpResult<?> queryTcp(@ApiParam("端口") Integer port) {
      return HttpResult.success("");
    }

  }


}
