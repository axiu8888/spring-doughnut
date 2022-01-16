package com.benefitj.system.controller;

import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.file.IUserFileManager;
import com.benefitj.system.controller.vo.FileItem;
import com.benefitj.scaffold.file.SystemFileManager;
import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.BreakPointTransmissionHelper;
import com.benefitj.spring.aop.web.AopWebPointCut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件操作
 */
@AopWebPointCut
@Api(tags = {"文件操作"}, description = "上传/下载 文件")
@RestController
@RequestMapping("/files")
public class FileController {

  @Autowired
  private SystemFileManager systemFileManager;

  @ApiOperation("文件上传")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "path", value = "文件路径"),
      @ApiImplicitParam(name = "files", value = "文件数组", required = true, dataTypeClass = String.class),
  })
  @PostMapping
  public HttpResult<?> upload(HttpServletRequest request, MultipartFile[] files, String path) throws IOException {
    if (files == null || files.length == 0) {
      return HttpResult.fail("上传文件不能为空!");
    }
    IUserFileManager ufm = systemFileManager.currentUser();
    File dir = ufm.getDirectory(StringUtils.isNotBlank(path) ? path : "/", true);
    for (MultipartFile file : files) {
      String filename = file.getOriginalFilename();
      filename = StringUtils.isNotBlank(filename) ? filename : IdUtils.nextId(8) + "__" + file.getName();
      BreakPointTransmissionHelper.upload(request, file, new File(dir, filename));
    }
    return HttpResult.succeed("上传成功!");
  }

  @ApiOperation("文件下载")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "path", value = "路径"),
      @ApiImplicitParam(name = "filename", value = "文件名称", dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public void download(HttpServletRequest request,
                       HttpServletResponse response,
                       String path,
                       String filename) throws IOException {
    IUserFileManager ufm = systemFileManager.currentUser();
    File file = ufm.getFile(path, filename);
    if (!file.exists()) {
      response.encodeRedirectURL("/error/404.html");
      return;
    }
    BreakPointTransmissionHelper.download(request, response, file, filename);
  }

  @ApiOperation("文件列表")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "path", value = "文件的全路径", required = true, dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "multiLevel", value = "多层级", required = false, defaultValue = "false", dataType = "String", dataTypeClass = Boolean.class),
  })
  @GetMapping("/list")
  public HttpResult<?> list(String path, Boolean multiLevel) {
    IUserFileManager ufm = systemFileManager.currentUser();
    File dir = StringUtils.isNotBlank(path) ? ufm.getDirectory(path, false) : ufm.getUserRoot();
    if (dir.exists()) {
      File[] files = dir.listFiles();
      if (files != null) {
        return HttpResult.succeed(Arrays.stream(files)
            .flatMap(f -> flatDirMap(f, multiLevel))
            .map(file -> FileItem.of(file, ufm.getUserRootPath()))
            .collect(Collectors.toList()));
      }
    }
    return HttpResult.succeed(Collections.emptyList());
  }

  private Stream<File> flatDirMap(File file, Boolean multiLevel) {
    if (Boolean.TRUE.equals(multiLevel) && file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        return Stream.concat(Stream.of(file), Stream.of(files).flatMap(f -> flatDirMap(f, multiLevel)));
      }
    }
    return Stream.of(file);
  }

  @ApiOperation("文件删除")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "path", value = "文件路径", dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "files", value = "文件", required = true, dataType = "String[]", dataTypeClass = String[].class),
  })
  @DeleteMapping
  public HttpResult<?> delete(String path, String[] files) {
    IUserFileManager ufm = systemFileManager.currentUser();
    File dir = ufm.getDirectory(StringUtils.isNotBlank(path) ? path : "/", true);
    if (files != null && files.length > 0) {
      Arrays.stream(files)
          .filter(StringUtils::isNotBlank)
          .forEach(name -> IOUtils.deleteFile(new File(dir, name)));
    }
    return HttpResult.succeed();
  }

}
