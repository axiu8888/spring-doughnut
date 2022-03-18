package com.benefitj.system.controller;

import com.benefitj.scaffold.http.AuthTokenVo;
import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.aop.AopIgnore;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.security.url.UrlPermitted;
import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.system.service.UserAuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
  @ApiOperation(value = "注册", notes = "注册新用户")
  @PostMapping("/register")
  public HttpResult<AuthTokenVo<SysAccountEntity>> register(@ApiParam("用户名") String username,
                                                            @ApiParam("密码") String password,
                                                            @ApiParam("机构ID") String orgId) {
    if (StringUtils.isAnyBlank(username, password)) {
      return HttpResult.fail("用户名或密码不能为空");
    }
    return HttpResult.succeed(service.register(username, password, orgId));
  }

  /**
   * 登录
   */
  @AopIgnore
  @ApiOperation(value = "登录", notes = "用户登录")
  @PostMapping("/login")
  public HttpResult<AuthTokenVo> login(@ApiParam("用户名") String username, @ApiParam("密码") String password) {
    if (StringUtils.isAnyBlank(username, password)) {
      return HttpResult.fail("用户名或密码错误");
    }
    return HttpResult.succeed(service.login(username, password));
  }

  /**
   * 获取 token
   */
  @ApiOperation(value = "刷新token", notes = "通过refreshToken获取新的token")
  @GetMapping("/token")
  public HttpResult<AuthTokenVo> refreshToken(@ApiParam("刷新的token") @RequestHeader("refresh") String refreshToken) {
    if (StringUtils.isBlank(refreshToken)) {
      return HttpResult.fail("token错误");
    }
    return HttpResult.succeed(service.getAccessToken(refreshToken));
  }


}
