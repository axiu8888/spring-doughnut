package com.benefitj.system.controller;


import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.system.service.SysAccountService;
import com.benefitj.system.service.UserAuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账号
 */
@Api(tags = {"账号"}, description = "对账号的各种操作")
@AopWebPointCut
@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired
  private SysAccountService accountService;
  @Autowired
  private UserAuthenticationService userAuthenticationService;

  @ApiOperation("获取账号")
  @GetMapping
  public HttpResult<?> get(@ApiParam("账号ID") String id) {
    if (StringUtils.isBlank(id)) {
      id = JwtTokenManager.currentUserId();
    }
    SysAccountEntity user = accountService.getByUserId(id);
    return HttpResult.succeed(user);
  }

  @ApiOperation("添加账号")
  @PostMapping
  public HttpResult<?> create(@RequestBody SysAccountEntity account) {
    accountService.save(account);
    return HttpResult.succeed(account);
  }

  @ApiOperation("删除账号")
  @DeleteMapping
  public HttpResult<?> delete(@ApiParam("账号ID") String id, @ApiParam("是否强制") Boolean force) {
    int count = userAuthenticationService.deleteAccount(id, force);
    return HttpResult.succeed(count);
  }

  @ApiOperation("改变账号的状态")
  @PatchMapping("/active")
  public HttpResult<?> changeActive(@ApiParam("账号ID") String id, @ApiParam("状态") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("账号ID不能为空");
    }
    Boolean result = accountService.changeActive(id, active);
    return HttpResult.succeed(result);
  }

  //@AopIgnore
  @ApiOperation("修改密码")
  @PostMapping("/changePassword")
  public HttpResult<?> changePassword(@ApiParam("用户ID") String userId,
                                      @ApiParam("旧密码") String oldPassword,
                                      @ApiParam("新密码") String newPassword) {
    if (StringUtils.isAnyBlank(userId, oldPassword, newPassword)) {
      return HttpResult.fail("用户ID和密码都不能为空");
    }
    boolean result = accountService.changePassword(userId, oldPassword, newPassword);
    return HttpResult.succeed(result);
  }

}
