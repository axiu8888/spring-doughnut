package com.benefitj.system.controller;


import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.system.service.SysAccountService;
import com.benefitj.system.service.UserAuthenticationService;
import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "账号ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    if (StringUtils.isBlank(id)) {
      id = JwtTokenManager.currentUserId();
    }
    SysAccountEntity user = accountService.getByUserId(id);
    return HttpResult.succeed(user);
  }

  @ApiOperation("添加账号")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "account", value = "账号数据", dataType = "String", dataTypeClass = String.class),
  })
  @PostMapping
  public HttpResult<?> create(SysAccountEntity account) {
    accountService.save(account);
    return HttpResult.succeed(account);
  }

  @ApiOperation("删除账号")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "账号ID", dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "force", value = "是否强制", dataType = "Boolean", dataTypeClass = Boolean.class),
  })
  @DeleteMapping
  public HttpResult<?> delete(String id, Boolean force) {
    int count = userAuthenticationService.deleteAccount(id, force);
    return HttpResult.succeed(count);
  }

  @ApiOperation("改变账号的状态")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "账号ID", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "状态", dataType = "Boolean", paramType = "form", dataTypeClass = Boolean.class),
  })
  @PatchMapping("/active")
  public HttpResult<?> changeActive(String id, Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("账号ID不能为空");
    }
    Boolean result = accountService.changeActive(id, active);
    return HttpResult.succeed(result);
  }

  //@AopIgnore
  @ApiOperation("修改密码")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "oldPassword", value = "旧密码", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "String", paramType = "form", dataTypeClass = String.class),
  })
  @PostMapping("/changePassword")
  public HttpResult<?> changePassword(String userId, String oldPassword, String newPassword) {
    if (StringUtils.isAnyBlank(userId, oldPassword, newPassword)) {
      return HttpResult.fail("用户ID和密码都不能为空");
    }
    boolean result = accountService.changePassword(userId, oldPassword, newPassword);
    return HttpResult.succeed(result);
  }

}
