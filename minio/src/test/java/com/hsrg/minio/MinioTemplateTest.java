package com.hsrg.minio;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.CodecUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.Utils;
import com.benefitj.core.functions.Pair;
import com.hsrg.minio.dto.ItemEntity;
import com.hsrg.minio.dto.LifecycleRuleEntity;
import com.hsrg.minio.spring.MinioConfiguration;
import io.minio.*;
import io.minio.messages.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
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
   * MD5
   */
  @Test
  void test_md5() throws IOException {
    ClassPathResource resource = new ClassPathResource("nginx-80.conf");
    File file = resource.getFile();
    log.info("MD5 ==>: {}", MinioUtils.md5(file));
  }

  /**
   * 检查桶是否存在
   */
  @Test
  void test_bucketExists() {
    boolean bucketExists = template.bucketExists("test");
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

  @Test
  void test_setBucketTags() {
    // 一个桶设置
    String bucketName = "test";
    MinioResult<Boolean> r = template.setBucketTags(Utils.mapOf(
        Pair.of("Author", "dxa")
        //, Pair.of("Description", MinioUtils.encode("测试"))
    ), bucketName);
    if (r.isSuccessful()) {
      log.info("------------------------ 设置桶的标签 ------------------------");
      log.info("设置结果: {}, error: {}", r.isSuccessful(), r.getMessage());
    } else {
      log.info("请求失败: {}", r.getMessage());
      //result.getError().printStackTrace();
    }
  }

  @Test
  void test_addBucketTags() {
    String bucketName = "test";
    MinioResult<Boolean> r = template.addBucketTags(Utils.mapOf(Pair.of("Author2", "dxa2")), bucketName);
    if (r.isSuccessful()) {
      log.info("------------------------ 添加桶的标签 ------------------------");
      log.info("tags: {}", r.getData());
    } else {
      log.info("请求失败: {}", r.getMessage());
      //result.getError().printStackTrace();
    }
  }

  @Test
  void test_getBucketTags() {
    String bucketName = "test";
    MinioResult<Map<String, String>> r = template.getBucketTags(bucketName);
    if (r.isSuccessful()) {
      log.info("------------------------ 获取桶的标签 ------------------------");
      log.info("tags: {}", r.getData());
    } else {
      log.info("请求失败: {}", r.getMessage());
      //result.getError().printStackTrace();
    }
  }

  @Test
  void test_deleteBucketTags() {
    String bucketName = "test";
    MinioResult<Boolean> r = template.deleteBucketTags(bucketName);
    log.info("------------------------ 删除桶的标签 ------------------------");
    log.info("result: {}", r.getData());
  }

  /**
   * 设置桶的版本
   */
  @Test
  void test_setBucketVersioning() {
    String bucketName = "test";
    MinioResult result = template.setBucketVersioning(VersioningConfiguration.Status.SUSPENDED, false, bucketName);
    log.info("设置versioning状态：{}, msg: {}", result.isSuccessful(), result.getMessage());
    log.info("test_setBucketVersioning bucket, [{}] config ==>: {}", bucketName, template.getBucketVersioning(bucketName).getData().status());
  }

  /**
   * 设置桶的副本数量
   */
  @Test
  void test_setBucketReplication() {
//    String bucketName = "test";
//    MinioResult result = template.setBucketReplication(null, Arrays.asList(
//        new ReplicationRule(
//
//        )
//    ), bucketName);
//    log.info("设置副本：{}, msg: {}", result.isSuccessful(), result.getMessage());
//    log.info("test_setBucketVersioning bucket, [{}] config ==>: {}", bucketName, template.getBucketVersioning(bucketName).getData().status());
  }

  /**
   * 获取桶的生命周期配置
   */
  @Test
  void test_getBucketLifecycle() {
    String bucketName = "test";
    MinioResult<LifecycleConfiguration> result = template.getBucketLifecycle(bucketName);
    log.info("getBucketLifecycle bucket, [{}], success: {}, msg: {}, config ==>: {}"
        , bucketName
        , result.isSuccessful()
        , result.getMessage()
        , JSON.toJSONString(result.getData()
            .rules()
            .stream()
            .map(LifecycleRuleEntity::from)
            .collect(Collectors.toList()))
    );
  }

  /**
   * 删除桶
   */
  @Test
  void test_removeBucket() {
    String bucketName = "test";
    boolean removeBucket = template.removeBucket(RemoveBucketArgs.builder(), bucketName, true);
    log.info("remove bucket, [{}], removeBucket: {} bucketExists ==>: {}"
        , bucketName, removeBucket, template.bucketExists(bucketName));
  }

  /**
   * 上传对象
   */
  @Test
  void test_uploadObject() throws IOException {
    String bucketName = "test";
    ClassPathResource resource = new ClassPathResource("nginx-80.conf");
    File file = resource.getFile();
    String objectName = "前缀/" + file.getName();
    MinioResult<ObjectWriteResponse> result = template.uploadObject(objectName, file.getPath(), bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 上传对象 ------------------------");
      ObjectWriteResponse response = result.getData();
      log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
          , response.etag()
          , response.versionId()
          , response.headers()
      );
    } else {
      log.info("请求失败 ==>: {}", result.getMessage());
      //result.getError().printStackTrace();
    }
  }

  /**
   * 上传对象
   */
  @Test
  void test_uploadObjects() {
    String bucketName = "test";
//    File dir = new File("D:\\home\\android\\debug");
    File dir = new File("F:\\迅雷下载");

    List<SnowballObject> objects = MinioUtils.listObjects(dir, "迅雷下载", MinioUtils::snowballObject);
    MinioResult<ObjectWriteResponse> result = template.uploadObjects(UploadSnowballObjectsArgs.builder()
            .objects(objects)
            .userMetadata(Utils.mapOf(Pair.of("Author", "dxa"),
                Pair.of("description", CodecUtils.encodeURL("测试"))))
            .tags(Utils.mapOf(Pair.of("type", "test")))
            .compression(false)
        , bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 上传对象 ------------------------");
      ObjectWriteResponse response = result.getData();
      log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
          , response.etag()
          , response.versionId()
          , response.headers()
      );
    } else {
      log.info("请求失败 ==>: {}", result.getMessage());
      result.getError().printStackTrace();
    }
  }

  /**
   * 上传对象
   */
  @Test
  void test_putObject() throws IOException {
    String bucketName = "test";
    ClassPathResource resource = new ClassPathResource("nginx-80.conf");
    File file = resource.getFile();
    String objectName =  file.getName();
    MinioResult<ObjectWriteResponse> result = template.putObject(PutObjectArgs.builder()
            .contentType(ContentType.get(file.getName()))
            .stream(Files.newInputStream(file.toPath()), file.length(), PutObjectArgs.MIN_MULTIPART_SIZE)
            .tags(Utils.mapOf(Pair.of("Author", "测试者")))
            .userMetadata(MinioUtils.getFileMetadata(file))
        , objectName, bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 上传对象 ------------------------");
      ObjectWriteResponse response = result.getData();
      log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
          , response.etag()
          , response.versionId()
          , response.headers()
      );
    } else {
      log.info("请求失败 ==>: {}", result.getMessage());
      result.getError().printStackTrace();
    }
  }


  /**
   * 上传对象
   */
  @Test
  void test_putObjects() {
    String bucketName = "test";
    File dir = new File("F:\\迅雷下载");
    List<PutObjectArgs.Builder> builders = MinioUtils.listObjects(dir, "根目录", MinioUtils::putObjectArgs);
    MinioResult<List<ObjectWriteResponse>> result = template.putObjects(builders, bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 上传对象 ------------------------");
      List<ObjectWriteResponse> responses = result.getData();
      responses.forEach(response -> {
        log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
            , response.etag()
            , response.versionId()
            , response.headers()
        );
      });
    } else {
      log.info("请求失败 ==>: {}", result.getMessage());
      result.getError().printStackTrace();
    }
  }

  /**
   * 列出桶下的全部对象
   */
  @Test
  void test_listObjects() {
    String bucketName = "test";
    MinioResult<List<Item>> result = template.listObjects(true, bucketName);
    if (result.isSuccessful()) {
      List<ItemEntity> list = result.getData()
          .stream()
          .filter(Objects::nonNull)
          .map(ItemEntity::from)
          .collect(Collectors.toList());
      log.info("全部的文件：{}, msg: {}", JSON.toJSONString(list), result.getMessage());
    } else {
      log.info("请求失败 ==>: {}", result.getMessage());
      //result.getError().printStackTrace();
    }
  }

  /**
   * 查询对象信息
   */
  @Test
  void test_statObject() {
    String bucketName = "test";
    String objectName = "nginx-802.conf";
    MinioResult<StatObjectResponse> result = template.statObject(objectName, bucketName);
    log.info("\n----------------------------------------\n");
    if (result.isSuccessful()) {
      log.info("------------------------ 查询对象信息 ------------------------");
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
      //result.getError().printStackTrace();
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
            .offset(0L)
            .length(100L)
        , objectName, bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 获取对象 ------------------------");
      GetObjectResponse response = result.getData();
      log.info("bytes: {}", IOUtils.readFully(response).toString(StandardCharsets.UTF_8.name()));
      log.info("bucket: {}", response.bucket());
      log.info("object: {}", response.object());
      log.info("region: {}", response.region());
      log.info("headers: {}", response.headers());
    } else {
      log.info("请求失败: {}", result.getMessage());
      //result.getError().printStackTrace();
    }
  }

  /**
   * 下载对象
   */
  @Test
  void test_downloadObject() {
    log.info("------------------------ 下载对象 ------------------------");
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
    log.info("------------------------ 移除对象 ------------------------");
    String bucketName = "test";
    String objectName = "copy_nginx-80.conf";
    log.info("移除对象: {}, {}, {}", bucketName, objectName, template.removeObject(objectName, bucketName));
  }

  /**
   * 拷贝对象
   */
  @Test
  void test_copyObject() {
    String bucketName = "test";
    String srcObjectName = "nginx-80.conf";
    CopySource source = CopySource.builder()
        .object(srcObjectName) // 源对象
        .bucket(bucketName) // 源对象的桶
        .build();
    MinioResult<ObjectWriteResponse> result = template.copyObject("copy_" + srcObjectName, source, Directive.COPY, bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 拷贝对象 ------------------------");
      ObjectWriteResponse response = result.getData();
      log.info("bucket: {}", response.bucket());
      log.info("object: {}", response.object());
      log.info("etag: {}", response.etag());
      log.info("region: {}", response.region());
      log.info("versionId: {}", response.versionId());
      log.info("headers: {}", response.headers());
    } else {
      log.info("请求失败: {}", result.getMessage());
      //result.getError().printStackTrace();
    }
  }

  /**
   * 组合对象
   */
  @Test
  void test_composeObject() {
    // 把 nginx-80.conf 和 copy_nginx-80.conf 合并到 compose_nginx-80.conf
    String bucketName = "test";
    String objectName = "compose_nginx-80.conf";
    MinioResult<ObjectWriteResponse> result = template.composeObject(ComposeObjectArgs.builder()
        , objectName
        , Arrays.asList(
            ComposeSource.builder().object("nginx-80.conf").bucket(bucketName).build(),
            ComposeSource.builder().object("copy_nginx-80.conf").bucket(bucketName).build()
        )
        , bucketName);
    if (result.isSuccessful()) {
      log.info("------------------------ 合并对象 ------------------------");
      ObjectWriteResponse response = result.getData();
      log.info("bucket: {}", response.bucket());
      log.info("object: {}", response.object());
      log.info("etag: {}", response.etag());
      log.info("region: {}", response.region());
      log.info("versionId: {}", response.versionId());
      log.info("headers: {}", response.headers());
    } else {
      log.info("请求失败: {}", result.getMessage());
      //result.getError().printStackTrace();
    }
  }

  @Test
  void test_setObjectTags() {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    MinioResult<Boolean> r = template.setObjectTags(objectName
        , Utils.mapOf(Pair.of("Author", "dxa"), Pair.of("Description", CodecUtils.encodeURL("测试")))
        , bucketName);
    if (r.isSuccessful()) {
      log.info("------------------------ 设置对象标签 ------------------------");
      log.info("设置结果: {}, error: {}", r.isSuccessful(), r.getMessage());
    } else {
      log.info("请求失败: {}", r.getMessage());
      //result.getError().printStackTrace();
    }
  }

  @Test
  void test_getObjectTags() {
    String bucketName = "test";
    String objectName = "nginx-80.conf";
    MinioResult<Tags> r = template.getObjectTags(objectName, bucketName);
    if (r.isSuccessful()) {
      log.info("------------------------ 获取对象标签 ------------------------");
      Tags tags = r.getData();
      Map<String, String> map = tags.get();
      log.info("tags: {}", map);
    } else {
      log.info("请求失败: {}", r.getMessage());
      //result.getError().printStackTrace();
    }
  }

//  @Test
//  void test_setBucketPolicy() {
//    String bucketName = "test";
//    template.setBucketPolicy(MinIOUtils.setBucketPolicy(bucketName, ""));
//    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, template.bucketExists(bucketName));
//  }

}