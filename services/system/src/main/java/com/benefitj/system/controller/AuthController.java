package com.benefitj.system.controller;

import com.benefitj.system.service.UserAuthenticationService;
import com.benefitj.scaffold.http.AuthTokenVo;
import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.aop.AopIgnore;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.security.url.UrlPermitted;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录
 */
@UrlPermitted
@AopWebPointCut
@Api(tags = {"用户认证"}, description = "注册/登录/刷新token")
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private UserAuthenticationService service;

  /**
   * 注册
   */
  @AopIgnore
  @ApiOperation("注册")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "orgId", value = "机构ID", required = true, dataType = "String", dataTypeClass = String.class)
  })
  @PostMapping("/register")
  public HttpResult<?> register(String username, String password, String orgId) {
    if (StringUtils.isAnyBlank(username, password)) {
      return HttpResult.fail("用户名或密码不能为空");
    }
    AuthTokenVo vo = service.register(username, password, orgId);
    return HttpResult.succeed(vo);
  }

  /**
   * 登录
   */
  @AopIgnore
  @ApiOperation("登录")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", dataTypeClass = String.class)
  })
  @PostMapping("/login")
  public HttpResult<?> login(String username, String password) {
    if (StringUtils.isAnyBlank(username, password)) {
      return HttpResult.fail("用户名或密码错误");
    }
    AuthTokenVo vo = service.login(username, password);
    return HttpResult.succeed(vo);
  }

  /**
   * 获取 token
   */
  @ApiOperation("刷新token")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "refresh", value = "刷新的token", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping("/token")
  public HttpResult<?> refreshToken(@RequestHeader("refresh") String refreshToken) {
    if (StringUtils.isBlank(refreshToken)) {
      return HttpResult.fail("token错误");
    }
    AuthTokenVo vo = service.getAccessToken(refreshToken);
    return HttpResult.succeed(vo);
  }


}
