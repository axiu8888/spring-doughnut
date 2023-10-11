package com.benefitj.mybatisplus.controller;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.IdUtils;
import com.benefitj.mybatisplus.controller.vo.HttpResult;
import com.benefitj.mybatisplus.controller.vo.QuartzJobArgVo;
import com.benefitj.mybatisplus.entity.SysJob;
import com.benefitj.mybatisplus.quartz.CronJob;
import com.benefitj.mybatisplus.quartz.SimpleJob;
import com.benefitj.mybatisplus.service.SysJobService;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.quartz.JobType;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.quartz.WorkerType;
import com.benefitj.spring.quartz.worker.QuartzWorkerInvoker;
import com.benefitj.spring.quartz.worker.QuartzWorkerManager;
import com.benefitj.spring.security.jwt.token.JwtTokenManager;
import com.benefitj.spring.security.url.UrlPermitted;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@UrlPermitted // 允许访问
@Api(tags = {"Quartz调度接口"}, description = "操作Quartz的调度任务")
@AopWebPointCut
@RestController
@RequestMapping("/quartz")
@Slf4j
public class QuartzController {

  @Autowired
  QuartzWorkerManager manager;

  @Autowired
  SysJobService quartzService;

  @ApiOperation("获取触发器类型")
  @GetMapping("/triggerTypes")
  public HttpResult<TriggerType[]> triggerTypes() {
    return HttpResult.success(TriggerType.values());
  }

  @ApiOperation("获取Job类型")
  @GetMapping("/jobTypes")
  public HttpResult<JobType[]> jobTypes() {
    return HttpResult.success(JobType.values());
  }

  @ApiOperation("获取Worker类型")
  @GetMapping("/workerTypes")
  public HttpResult<WorkerType[]> workerTypes() {
    return HttpResult.success(WorkerType.values());
  }

  @ApiOperation("获取发布的QuartzWorker")
  @GetMapping("/quartzWorkers")
  public HttpResult<List<JSONObject>> getQuartzJobWorkers() {
    List<JSONObject> list = manager.values()
        .stream()
        .map(qji -> new JSONObject() {{
          put("rawName", qji.getName());
          put("methodName", qji.getMethod().getName());
          put("name", qji.getAnnotation().name());
          put("description", qji.getAnnotation().description());
          put("parameters", qji.getArgDescriptors().stream()
              .map(ad -> QuartzJobArgVo.builder()
                  .name(ad.getName())
                  .type(ad.getType())
                  .position(ad.getPosition())
                  .description(ad.getDescription())
                  .build())
              .collect(Collectors.toList()));
        }})
        .collect(Collectors.toList());
    return HttpResult.success(list);
  }

  @ApiOperation("创建SIMPLE类型的调度")
  @PostMapping("/simpleQuartzJob")
  public HttpResult<SysJob> simpleQuartzWorker(@RequestBody SimpleJob body) {
    QuartzWorkerInvoker invoker = manager.get(body.getWorker());
    if (invoker == null) {
      return HttpResult.failure("无法发现[ " + body.getWorker() + " ] quartz worker");
    }
    return HttpResult.success(quartzService.create(SysJob.builder()
        .triggerType(TriggerType.SIMPLE)
        .workerType(WorkerType.QUARTZ_WORKER)
        .jobName(invoker.getName() + "_@_" + IdUtils.uuid(8))
        .worker(invoker.getName())
        .simpleInterval(body.getSimpleInterval())
        .simpleRepeatCount(body.getSimpleRepeatCount())
        .description(StringUtils.getIfBlank(body.getDescription(), invoker::getDescription))
        .jobData(body.getJobData())
        .startAt(body.getStartAt())
        .endAt(body.getEndAt())
        .orgId(StringUtils.getIfBlank(body.getOrgId(), JwtTokenManager::currentOrgId))
        .ownerId(StringUtils.getIfBlank(body.getOwnerId(), JwtTokenManager::currentUserId))
        .ownerType(StringUtils.getIfBlank(body.getOwnerType(), () -> "user"))
        .build()));
  }

  @ApiOperation("创建CRON类型的调度")
  @PostMapping("/cronQuartzJob")
  public HttpResult<SysJob> cronQuartzWorker(@RequestBody CronJob body) {
    QuartzWorkerInvoker invoker = manager.get(body.getWorker());
    if (invoker == null) {
      return HttpResult.failure("无法发现[ " + body.getWorker() + " ] quartz worker");
    }
    return HttpResult.success(quartzService.create(SysJob.builder()
        .triggerType(TriggerType.CRON)
        .workerType(WorkerType.QUARTZ_WORKER)
        .jobName(invoker.getName() + "_@_" + IdUtils.uuid(8))
        .worker(invoker.getName())
        .cronExpression(body.getCronExpression())
        .description(StringUtils.getIfBlank(body.getDescription(), invoker::getDescription))
        .jobData(body.getJobData())
        .startAt(body.getStartAt())
        .endAt(body.getEndAt())
        .orgId(StringUtils.getIfBlank(body.getOrgId(), JwtTokenManager::currentOrgId))
        .ownerId(StringUtils.getIfBlank(body.getOwnerId(), JwtTokenManager::currentUserId))
        .ownerType(StringUtils.getIfBlank(body.getOwnerType(), () -> "user"))
        .build()));
  }

  @ApiOperation("获取调度任务")
  @GetMapping
  public HttpResult<SysJob> get(@ApiParam("调度任务的ID") String id) {
    return HttpResult.success(quartzService.get(id));
  }

  @ApiOperation("添加任务调度")
  @PostMapping
  public HttpResult<SysJob> create(@ApiParam("调度数据") SysJob job) {
    return HttpResult.success(quartzService.create(job));
  }

  @ApiOperation("更新任务调度")
  @PutMapping
  public HttpResult<Boolean> update(@ApiParam("任务调度数据") @RequestBody SysJob job) {
    if (StringUtils.isBlank(job.getId())) {
      return HttpResult.failure("任务调度任务的ID不能为空");
    }
    return HttpResult.success(quartzService.update(job));
  }

  @ApiOperation("删除任务调度")
  @DeleteMapping
  public HttpResult<Integer> delete(@ApiParam("任务调度ID") String id) {
    return HttpResult.success(quartzService.delete(id));
  }

  @ApiOperation("改变任务调度的状态")
  @PatchMapping("/active")
  public HttpResult<Boolean> changeActive(@ApiParam("任务调度ID") String id,
                                          @ApiParam("是否强制") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.failure("调度任务的ID不能为空");
    }
    return HttpResult.success(quartzService.changeActive(id, active));
  }

  @ApiOperation("获取任务调度列表分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysJob>> page(@ApiParam("分页参数") @PageBody PageRequest<SysJob> page) {
    PageInfo<SysJob> pageList = quartzService.getPage(page);
    return HttpResult.success(pageList);
  }

  @ApiOperation("获取机构的任务调度列表")
  @GetMapping("/list")
  public HttpResult<List<SysJob>> list(@ApiParam("条件") @QueryBody SysJob condition) {
    condition.setOrgId(StringUtils.getIfBlank(condition.getOrgId(), JwtTokenManager::currentOrgId));
    return HttpResult.success(quartzService.getList(condition, null, null));
  }

}
