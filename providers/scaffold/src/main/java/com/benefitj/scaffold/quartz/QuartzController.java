package com.benefitj.scaffold.quartz;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.quartz.entity.SysJobTaskEntity;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.get.GetBody;
import com.benefitj.spring.mvc.page.PageBody;
import com.benefitj.spring.mvc.page.PageableRequest;
import com.benefitj.spring.quartz.JobType;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.quartz.WorkerType;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Quartz的调度任务
 */
@AopWebPointCut
@Api(tags = {"调度任务"}, description = "Quartz的调度任务")
@RestController
@RequestMapping("/quartz")
public class QuartzController {

  @Autowired
  private QuartzJobTaskService quartzService;

  @ApiOperation("获取触发器类型")
  @GetMapping("/triggerType")
  public HttpResult<?> getTriggerType() {
    return HttpResult.succeed(TriggerType.values());
  }

  @ApiOperation("获取Job类型")
  @GetMapping("/jobType")
  public HttpResult<?> getJobType() {
    return HttpResult.succeed(JobType.values());
  }

  @ApiOperation("获取Worker类型")
  @GetMapping("/workerType")
  public HttpResult<?> getWorkerType() {
    return HttpResult.succeed(WorkerType.values());
  }

  @ApiOperation("获取Cron调度任务")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "Cron调度任务的ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    SysJobTaskEntity task = quartzService.getById(id);
    return HttpResult.succeed(task);
  }

  @ApiOperation("添加任务调度")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "task", dataTypeClass = SysJobTaskEntity.class),
  })
  @PostMapping
  public HttpResult<?> create(SysJobTaskEntity task) {
    task = quartzService.create(task);
    return HttpResult.succeed(task);
  }

  @ApiOperation("更新任务调度")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "task", value = "任务调度数据", dataTypeClass = SysJobTaskEntity.class),
  })
  @PutMapping
  public HttpResult<?> update(@RequestBody SysJobTaskEntity task) {
    if (StringUtils.isBlank(task.getId())) {
      return HttpResult.fail("任务调度任务的ID不能为空");
    }
    task = quartzService.updateTask(task);
    return HttpResult.succeed(task);
  }

  @ApiOperation("删除任务调度")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "任务调度ID", dataType = "String", dataTypeClass = String.class),
  })
  @DeleteMapping
  public HttpResult<?> delete(String id) {
    int count = quartzService.delete(id);
    return HttpResult.succeed(count);
  }

  @ApiOperation("改变任务调度的状态")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "任务调度ID", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "状态", dataType = "Boolean", paramType = "form", dataTypeClass = Boolean.class),
  })
  @PatchMapping("/active")
  public HttpResult<?> changeActive(String id, Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("调度任务的ID不能为空");
    }
    Boolean result = quartzService.changeActive(id, active);
    return HttpResult.succeed(result);
  }

  @ApiOperation("获取任务调度列表分页")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "page", value = "分页参数", dataType = "RequestPage", dataTypeClass = PageableRequest.class),
  })
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageableRequest<SysJobTaskEntity> page) {
    PageInfo<SysJobTaskEntity> pageList = quartzService.getPage(page);
    return HttpResult.succeed(pageList);
  }

  @ApiOperation("获取机构的任务调度列表")
  @ApiImplicitParams({})
  @GetMapping("/list")
  public HttpResult<?> getJobTaskList(@GetBody SysJobTaskEntity condition) {
    condition.setOrgId(JwtTokenManager.currentOrgId());
    List<SysJobTaskEntity> all = quartzService.getList(condition, null, null);
    return HttpResult.succeed(all);
  }

}
