package com.benefitj.scaffold.quartz;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.quartz.entity.SysJobTaskEntity;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.mvc.query.QueryRequest;
import com.benefitj.spring.quartz.JobType;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.quartz.WorkerType;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
  @GetMapping
  public HttpResult<?> get(@ApiParam("Cron调度任务的ID") String id) {
    return HttpResult.succeed(quartzService.getById(id));
  }

  @ApiOperation("添加任务调度")
  @PostMapping
  public HttpResult<?> create(SysJobTaskEntity task) {
    task = quartzService.create(task);
    return HttpResult.succeed(task);
  }

  @ApiOperation("更新任务调度")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysJobTaskEntity task) {
    if (StringUtils.isBlank(task.getId())) {
      return HttpResult.fail("任务调度任务的ID不能为空");
    }
    task = quartzService.updateTask(task);
    return HttpResult.succeed(task);
  }

  @ApiOperation("删除任务调度")
  @DeleteMapping
  public HttpResult<?> delete(@ApiParam("任务调度ID") String id) {
    int count = quartzService.delete(id);
    return HttpResult.succeed(count);
  }

  @ApiOperation("改变任务调度的状态")
  @PatchMapping("/active")
  public HttpResult<?> changeActive(@ApiParam("任务调度ID") String id, @ApiParam("状态") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("调度任务的ID不能为空");
    }
    Boolean result = quartzService.changeActive(id, active);
    return HttpResult.succeed(result);
  }

  @ApiOperation("获取任务调度列表分页")
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageRequest<SysJobTaskEntity> page) {
    PageInfo<SysJobTaskEntity> pageList = quartzService.getPage(page);
    return HttpResult.succeed(pageList);
  }

  @ApiOperation("获取机构的任务调度列表")
  @GetMapping("/list")
  public HttpResult<?> getJobTaskList(@QueryBody QueryRequest<SysJobTaskEntity> request) {
    SysJobTaskEntity condition = request.getCondition();
    condition.setOrgId(JwtTokenManager.currentOrgId());
    List<SysJobTaskEntity> all = quartzService.getList(condition, null, null);
    return HttpResult.succeed(all);
  }

}
