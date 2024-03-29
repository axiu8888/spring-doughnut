package com.benefitj.mybatisplus.controller;

import com.benefitj.mybatisplus.controller.vo.HttpResult;
import com.benefitj.mybatisplus.entity.SysUser;
import com.benefitj.mybatisplus.service.SysUserService;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"用户"}, description = "用户的操作")
@AopWebPointCut
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private SysUserService userService;

  @ApiOperation("用户列表分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysUser>> page(@PageBody("p.") PageRequest<SysUser> page) {
    return HttpResult.success(userService.getPage(page));
  }

  @ApiOperation("获取机构的菜单列表")
  @GetMapping("/list")
  public HttpResult<List<SysUser>> list(@PageBody("p.") PageRequest<SysUser> page) {
    return HttpResult.success(userService.getList(page.getCondition(), page.getStartTime(), page.getEndTime()));
  }

}
