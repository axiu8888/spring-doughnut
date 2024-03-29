package com.benefitj.fileserver.controller;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.*;
import com.benefitj.core.functions.Pair;
import com.benefitj.minio.ContentType;
import com.benefitj.minio.MinioResult;
import com.benefitj.minio.MinioTemplate;
import com.benefitj.minio.MinioUtils;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.fileserver.controller.vo.FileItemVo;
import com.benefitj.fileserver.controller.vo.HttpResult;
import com.benefitj.fileserver.controller.vo.UploadVo;
import io.minio.GetObjectResponse;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.StatObjectResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Api(tags = "文件服务", description = "上传，下载")
@AopWebPointCut
@RestController
@RequestMapping("/files")
public class FileController {

  @Autowired
  private MinioTemplate template;

  @ApiOperation("上传文件")
  @PostMapping(value = "upload", consumes = "multipart/form-data;charset=utf-8")
  public HttpResult<List<FileItemVo>> upload(@ApiParam("桶") @RequestParam String bucketName,
                                             @ApiParam("保存路径") @RequestParam(required = false) String path,
                                             @ApiParam("元信息") @RequestPart(required = false) JSONObject metadata,
                                             @ApiParam("文件列表") @RequestPart MultipartFile[] files) {
    if (StringUtils.isBlank(bucketName)) {
      return HttpResult.fail("缺少bucketName参数!");
    }
    List<MultipartFile> list = files != null ? Arrays.asList(files) : Collections.emptyList();
    if (list.isEmpty()) {
      return HttpResult.fail("缺少可保存的文件");
    }

    File tmpDir = new File("D:/home/tmp/", IdUtils.uuid());
    try {
      List<File> tmpFiles = list.stream()
          .map(mf -> {
            File dest = IOUtils.createFile(tmpDir, mf.getOriginalFilename());
            CatchUtils.tryThrow(() -> mf.transferTo(dest));
            return dest;
          })
          .collect(Collectors.toList());

      List<FileItemVo> results = tmpFiles.stream()
          .map(f -> Pair.of(f, PutObjectArgs.builder()
              .bucket(bucketName)
              .object(MinioUtils.getObjectName(path, f.getName()))
              .contentType(ContentType.get(f.getName()))
              .stream(
                  CatchUtils.ignore(() -> Files.newInputStream(f.toPath())),
                  f.length(),
                  PutObjectArgs.MIN_MULTIPART_SIZE
              )
              .userMetadata(MinioUtils.concat(MinioUtils.getFileMetadata(f), "x-user-", metadata))
          ))
          .map(pair -> {
            PutObjectArgs args = pair.getValue().build();
            MinioResult<ObjectWriteResponse> result = template.putObject(pair.getValue(), args.object(), bucketName);
            return UploadVo.builder()
                .code(result.getCode())
                .message(result.getMessage())
                .name(args.object())
                .size(args.objectSize())
                .lastModified(pair.getKey().lastModified())
                .suffix(Utils.getFileSuffix(args.object()))
                .contentType(ContentType.get(pair.getKey().getName()))
                .url("/" + bucketName + "/" + args.object())
                .metadata(Utils.toMap(args.userMetadata(), (k, v) -> CodecUtils.decodeURL(v)))
                .build();
          })
          .collect(Collectors.toList());
      return HttpResult.succeed(results);
    } finally {
      // 强制删除
      IOUtils.delete(tmpDir);
    }
  }

  @ApiOperation("下载文件")
  @GetMapping("download")
  public void download(HttpServletRequest request,
                       HttpServletResponse response,
                       @ApiParam("桶") String bucketName,
                       @ApiParam("保存路径") String filename) {
    filename = CodecUtils.decodeURL(filename);
    try {
      filename = filename.replace("\\", "/").replace("//", "/");
      String name = filename.lastIndexOf("/") > 0 ? filename.substring(filename.lastIndexOf("/") + 1) : filename;

      MinioResult<StatObjectResponse> statObject = template.statObject(filename, bucketName);
      if (statObject.isSuccessful()) {
        StatObjectResponse data = statObject.getData();
        ServletUtils.RangeSettings rangeSettings = ServletUtils.parseRangeHeader(request, data.size(), true);
        MinioResult<GetObjectResponse> result = template.getObject(filename
            , rangeSettings.getStart()
            , rangeSettings.getContentLength()
            , bucketName);
        if (result.isSuccessful()) {
          ServletUtils.setResponseHeaders(response, rangeSettings, name);
          IOUtils.write(result.getData(), response.getOutputStream());
        } else {
          response.setCharacterEncoding("UTF-8");
          response.setContentType("application/json");
          ServletUtils.write(response, 400, JsonUtils.toJsonBytes(HttpResult.fail(result.getMessage())));
        }
      }
    } catch (Exception e) {
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      ServletUtils.write(response, 400, JsonUtils.toJsonBytes(HttpResult.fail("下载失败: " + e.getMessage())));
    }
  }

}
