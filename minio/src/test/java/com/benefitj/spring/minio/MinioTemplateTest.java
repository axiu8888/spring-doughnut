package com.benefitj.spring.minio;

import com.benefitj.core.IOUtils;
import com.benefitj.minio.MinioResult;
import com.benefitj.minio.MinioTemplate;
import com.benefitj.minio.spring.MinioConfiguration;
import io.minio.*;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {MinioConfiguration.class})
class MinioTemplateTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  MinioTemplate template;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  /**
   * 检查桶是否存在
   */
  @Test
  void test_bucketExists() {
    boolean bucketExists = template.bucketExists("hsrg");
    log.info("bucketExists ==>: {}", bucketExists);
  }

  /**
   * 获取全部的桶
   */
  @Test
  void test_listBuckets() {
    MinioResult<List<Bucket>> result = template.listBuckets();
    if (result.isSuccessful()) {
      log.info("listBuckets ==>: {}", result.getData().stream()
          .map(bucket -> String.format("[%s]", bucket.name() + ", " + bucket.creationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
          .collect(Collectors.toList()));
    } else {
      log.info("获取失败：{}", result.getMessage());
    }
  }

  /**
   * 创建桶
   */
  @Test
  void test_makeBucket() {
    String bucketName = "test";
    template.makeBucket(bucketName);
    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, template.bucketExists(bucketName));
  }

  /**
   * 删除桶
   */
  @Test
  void test_removeBucket() {
    String bucketName = "test";
    template.removeBucket(bucketName);
    log.info("remove bucket, [{}] bucketExists ==>: {}", bucketName, template.bucketExists(bucketName));
  }

  /**
   * 上传对象
   */
  @Test
  void test_uploadObject() {
    String bucketName = "test";
    File file = new File("D:\\home\\logs\\nginx-80.conf");
    String objectName = file.getName();
    MinioResult<ObjectWriteResponse> result = template.uploadObject(objectName, file.getPath(), bucketName);
    if (result.isSuccessful()) {
      ObjectWriteResponse response = result.getData();
      log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
          , response.etag()
          , response.versionId()
          , response.headers()
      );
    } else {
      log.info("uploadResponse error ==>: {}", result.getMessage());
    }
  }

  /**
   * 查询对象信息
   */
  @Test
  void test_statObject() {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    MinioResult<StatObjectResponse> result = template.statObject(objectName, bucketName);
    log.info("\n----------------------------------------\n");
    if (result.isSuccessful()) {
      StatObjectResponse response = result.getData();
      log.info("bucket: {}", response.bucket());
      log.info("object: {}", response.object());
      log.info("etag: {}", response.etag());
      log.info("region: {}", response.region());
      log.info("deleteMarker: {}", response.deleteMarker());
      log.info("lastModified: {}", response.lastModified());
      log.info("legalHold: {}", response.legalHold());
      log.info("retentionMode: {}", response.retentionMode());
      log.info("retentionRetainUntilDate: {}", response.retentionRetainUntilDate());
      log.info("contentType: {}", response.contentType());
      log.info("size: {}", response.size());
      log.info("userMetadata: {}", response.userMetadata());
      log.info("versionId: {}", response.versionId());
      log.info("headers: {}", response.headers());
    } else {
      log.info("请求失败: {}", result.getMessage());
    }
    log.info("\n----------------------------------------\n");
  }

  /**
   * 获取对象
   */
  @Test
  void test_getObject() throws UnsupportedEncodingException {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    MinioResult<GetObjectResponse> result = template.getObject(GetObjectArgs.builder()
            .offset(20L)
            .length(100L)
        , objectName, bucketName);
    if (result.isSuccessful()) {
      GetObjectResponse response = result.getData();
      log.info("bytes: {}", IOUtils.readFully(response).toString(StandardCharsets.UTF_8.name()));
      log.info("bucket: {}", response.bucket());
      log.info("object: {}", response.object());
      log.info("region: {}", response.region());
      log.info("headers: {}", response.headers());
    } else {
      log.info("请求失败: {}", result.getMessage());
    }
  }

  /**
   * 下载对象
   */
  @Test
  void test_downloadObject() {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    File file = IOUtils.createFile("D:/home/logs/tmp/nginx-80.conf");
    MinioResult<File> result = template.downloadObject(objectName, file.getPath(), true, bucketName);
    log.info("下载对象: {}, {}, {}, file.exist: {}", bucketName, objectName
        , result.getMessage()
        , file.exists() && file.length() > 0
    );
  }

  /**
   * 移除对象
   */
  @Test
  void test_removeObject() {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    log.info("移除对象: {}, {}, {}", bucketName, objectName, template.removeObject(objectName, bucketName));
  }

  /**
   * 拷贝对象
   */
  @Test
  void test_copyObject() {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    CopySource source = CopySource.builder()
        .object(objectName) // 源对象
        .bucket(bucketName) // 源对象的桶
        .build();
    MinioResult<ObjectWriteResponse> result = template.copyObject("copy_" + objectName, source, Directive.COPY, bucketName);
    if (result.isSuccessful()) {
      ObjectWriteResponse response = result.getData();
      log.info("bucket: {}", response.bucket());
      log.info("object: {}", response.object());
      log.info("etag: {}", response.etag());
      log.info("region: {}", response.region());
      log.info("versionId: {}", response.versionId());
      log.info("headers: {}", response.headers());
    } else {
      log.info("请求失败: {}", result.getMessage());
    }
  }

  /**
   * 组合对象
   */
  @Test
  void test_composeObject() {
    /*String bucketName = "test";
    String objectName = "nginx-80.conf";
    MinioResult<ObjectWriteResponse> result = template.composeObject(objectName,
        , null, bucketName);
    log.info("下载对象: {}, {}, {}, file.exist: {}", bucketName, objectName
        , result.getMessage()
    );*/
  }

//  @Test
//  void test_setBucketPolicy() {
//    String bucketName = "test";
//    template.setBucketPolicy(MinIOUtils.setBucketPolicy(bucketName, ""));
//    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, template.bucketExists(bucketName));
//  }

}