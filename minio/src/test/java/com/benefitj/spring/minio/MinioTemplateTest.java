package com.benefitj.spring.minio;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.Utils;
import com.benefitj.core.functions.Pair;
import com.benefitj.spring.minio.builder.LifecycleRuleBuilder;
import com.benefitj.spring.minio.dto.ItemEntity;
import com.benefitj.spring.minio.dto.LifecycleRuleEntity;
import com.benefitj.spring.minio.spring.MinioConfiguration;
import io.minio.*;
import io.minio.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes = {MinioConfiguration.class})
@Slf4j
class MinioTemplateTest {

  @Autowired
  MinioTemplate template;

//  final String bucketName = "test";
  final String bucketName = "hsrg";

  /**
   * MD5
   */
  @Test
  void test_md5() throws IOException {
    ClassPathResource resource = new ClassPathResource("application.properties");
    File file = resource.getFile();
    log.info("MD5 ==>: {}", MinioUtils.md5(file));
  }

  /**
   * 检查桶是否存在
   */
  @Test
  void test_bucketExists() {
    boolean bucketExists = template.bucketExists(bucketName);
    log.info("bucketExists ==>: {}", bucketExists);
  }

  /**
   * 获取全部的桶
   */
  @Test
  void test_listBuckets() {
    template.listBuckets()
        .promise()
        .then((result, data) -> {
          log.info("listBuckets ==>: {}", result.getData()
              .stream()
              .map(bucket -> String.format("[%s]", bucket.name() + ", " + DateFmtter.fmt(bucket.creationDate().toInstant().toEpochMilli())))
              .collect(Collectors.toList()));
        })
        .error((result, error) -> {
          log.info("获取失败：{}", error.getMessage());
        });
  }

  /**
   * 创建桶
   */
  @Test
  void test_makeBucket() {
    template.makeBucket(bucketName);
    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, template.bucketExists(bucketName));
  }

  @Test
  void test_setBucketTags() {
    // 一个桶设置
    template.setBucketTags(
            Utils.mapOf(Pair.of("Author", "dxa")
                //, Pair.of("Description", CodecUtils.encodeURL("测试"))
            ), bucketName)
        .promise()
        .then((r, data) -> {
          log.info("------------------------ 设置桶的标签 ------------------------");
          log.info("设置结果: {}, error: {}", r.isSuccessful(), r.getMessage());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  @Test
  void test_addBucketTags() {
    template.addBucketTags(Utils.mapOf(Pair.of("Author", "dxa")), bucketName)
        .promise()
        .then((r, data) -> {
          log.info("------------------------ 添加桶的标签 ------------------------");
          log.info("tags: {}", r.getData());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  @Test
  void test_getBucketTags() {
    template.getBucketTags(bucketName)
        .promise()
        .then((r, data) -> {
          log.info("------------------------ 获取桶的标签 ------------------------");
          log.info("tags: {}", r.getData());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  @Test
  void test_deleteBucketTags() {
    log.info("------------------------ 删除桶的标签 ------------------------");
    template.deleteBucketTags(bucketName)
        .promise()
        .then((r, data) -> log.info("result: {}", r.getData()))
        .error((result, error) -> log.info("error: {}", error.getMessage()));
  }

  /**
   * 设置桶的版本
   */
  @Test
  void test_setBucketVersioning() {
    MinioResult result = template.setBucketVersioning(VersioningConfiguration.Status.SUSPENDED, false, bucketName);
    log.info("设置versioning状态：{}, msg: {}", result.isSuccessful(), result.getMessage());
    log.info("test_setBucketVersioning bucket, [{}] config ==>: {}", bucketName, template.getBucketVersioning(bucketName).getData().status());
  }

  /**
   * 设置桶的副本数量
   */
  @Test
  void test_setBucketReplication() {
//    MinioResult result = template.setBucketReplication(null, Arrays.asList(
//        new ReplicationRule(
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
    MinioResult<LifecycleConfiguration> result = template.getBucketLifecycle(bucketName);
    log.info("getBucketLifecycle bucket, [{}], success: {}, msg: {}, config ==>: {}"
        , bucketName
        , result.isSuccessful()
        , result.getMessage()
        , JSON.toJSONString((result.getData() != null ? result.getData().rules().stream() : Stream.<LifecycleRule>empty())
            .map(LifecycleRuleEntity::from)
            .collect(Collectors.toList()))
    );
  }

  @Test
  void test_setBucketLifecycle() {
    // 定义生命周期规则 XML
    String lifecycleConfig =
        "<LifecycleConfiguration>" +
            "  <Rule>" +
            "    <ID>ExpireLogs</ID>" +
            "    <Prefix>logs/</Prefix>" +   // 设置前缀，例如 'logs/'
            "    <Status>Enabled</Status>" +
            "    <Expiration>" +
            "      <Days>0</Days>" +         // 0 天意味着立即删除
            "      <ExpiredObjectDeleteMarker>true</ExpiredObjectDeleteMarker>" +
            "    </Expiration>" +
            "    <AbortIncompleteMultipartUpload>" +
            "      <DaysAfterInitiation>1</DaysAfterInitiation>" + // 1 天后清理未完成的 multipart upload
            "    </AbortIncompleteMultipartUpload>" +
            "  </Rule>" +
            "</LifecycleConfiguration>";

    new LifecycleConfiguration(Arrays.asList(LifecycleRuleBuilder.builder()
            .setId("Expire_R_Peaks")
            .setAbortIncompleteMultipartUpload(1)
            .setStatus(true)
            .setExpiration(ZonedDateTime.now(), 1, true)
//            .setFilter(null, "", )
            .toRule()
    ));

//    // 设置生命周期策略
//    template.setBucketLifecycle(
//        SetBucketLifecycleArgs.builder()
//            .bucket("my-bucket") // 替换为你的 bucket 名称
//            .config(new LifecycleConfiguration(Arrays.asList(new LifecycleRule(lifecycleConfig))))
//            .build()
//    );
  }


  /**
   * 删除桶
   */
  @Test
  void test_removeBucket() {
    boolean removeBucket = template.removeBucket(RemoveBucketArgs.builder(), bucketName, true);
    log.info("remove bucket, [{}], removeBucket: {} bucketExists ==>: {}"
        , bucketName, removeBucket, template.bucketExists(bucketName));
  }

  @Test
  void test_delete() {
    MinioResult<List<DeleteError>> result = template.removeObjects(bucketName, item -> item.objectName().startsWith("测试/tmp"));
    log.info("remove bucket, [{}], removeBucket: {} bucketExists ==>: {}"
        , bucketName, result, template.bucketExists(bucketName));
  }

  /**
   * 上传对象
   */
  @Test
  void test_uploadObject() throws IOException {
    ClassPathResource resource = new ClassPathResource("nginx-80.conf");
    File file = resource.getFile();
    String prefix = "/测试/";
    String objectName = prefix + file.getName();
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
      //error.printStackTrace();
    }
  }

  /**
   * 上传对象
   */
  @Deprecated
  @Test
  void test_uploadObjects() {
    File dir = new File("D:/tmp/");
    String prefix = "/SQL/";
    List<SnowballObject> objects = MinioUtils.listObjects(dir, prefix, MinioUtils::snowballObject);
    template.uploadSnowballObjects(UploadSnowballObjectsArgs.builder()
                .objects(objects)
                .userMetadata(MinioUtils.mapOf(Pair.of("Author", "dxa"),
                    Pair.of("description", MinioUtils.encodeURL("测试"))))
                .tags(MinioUtils.mapOf(Pair.of("type", "test")))
                .compression(true)
            , bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 上传对象 ------------------------");
          ObjectWriteResponse response = result.getData();
          log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
              , response.etag()
              , response.versionId()
              , response.headers()
          );
        })
        .error((result, error) -> {
          log.info("请求失败 ==>: {}", result.getMessage());
          error.printStackTrace();
        });
  }

  /**
   * 上传对象
   */
  @Test
  void test_putObject() throws IOException {
    ClassPathResource resource = new ClassPathResource("application.properties");
    File file = resource.getFile();
    String objectName = "测试/" + file.getName();
    template.putObject(PutObjectArgs.builder()
                .contentType(ContentType.get(file.getName()))
                .stream(Files.newInputStream(file.toPath()), file.length(), PutObjectArgs.MIN_MULTIPART_SIZE)
                .userMetadata(MinioUtils.getFileMetadata(file))
            , objectName, bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 上传对象 ------------------------");
          ObjectWriteResponse response = result.getData();
          log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
              , response.etag()
              , response.versionId()
              , response.headers()
          );
        })
        .error((result, error) -> {
          log.info("请求失败 ==>: {}", result.getMessage());
          error.printStackTrace();
        });
  }

  /**
   * 上传对象
   */
  @Test
  void test_putObjects() {
    File dir = new File("D:/tmp/");
    String prefix = "/测试/";
    List<PutObjectArgs.Builder> builders = MinioUtils.listObjects(dir, prefix, MinioUtils::putObjectArgs);
    template.putObjects(builders, bucketName).promise()
        .then((result, data) -> {
          log.info("------------------------ 上传对象 ------------------------");
          List<ObjectWriteResponse> responses = result.getData();
          responses.forEach(response -> {
            log.info("response ==>\n etag: {}, \n versionId: {}, \n headers: {}"
                , response.etag()
                , response.versionId()
                , response.headers()
            );
          });
        })
        .error((result, error) -> {
          log.info("请求失败 ==>: {}", result.getMessage());
          error.printStackTrace();
        });
  }

  /**
   * 列出桶下的全部对象
   */
  @Test
  void test_listObjects() {
    template.listObjects(true, bucketName)
        .promise()
        .then((result, data) -> {
          List<ItemEntity> list = result.getData()
              .stream()
              .filter(Objects::nonNull)
              .map(ItemEntity::from)
              .collect(Collectors.toList());
          log.info("全部的文件：{}, msg: {}", JSON.toJSONString(list), result.getMessage());
        })
        .error((result, error) -> {
          log.info("请求失败 ==>: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  /**
   * 查询对象信息
   */
  @Test
  void test_statObject() {
    log.info("\n----------------------------------------\n");
    String prefix = "/测试/";
    String objectName = prefix + "nginx-80.conf";
    template.statObject(objectName, bucketName)
        .promise()
        .then((result, data) -> {
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
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
    log.info("\n----------------------------------------\n");
  }

  /**
   * 获取对象
   */
  @Test
  void test_getObject() throws UnsupportedEncodingException {
    String prefix = "/测试/";
    String objectName = prefix + "nginx-80.conf";
    template.getObject(GetObjectArgs.builder()
                .offset(0L)
                .length(100L)
            , objectName, bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 获取对象 ------------------------");
          GetObjectResponse response = result.getData();
          log.info("bytes: {}", IOUtils.readAsString(response, StandardCharsets.UTF_8));
          log.info("bucket: {}", response.bucket());
          log.info("object: {}", response.object());
          log.info("region: {}", response.region());
          log.info("headers: {}", response.headers());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  /**
   * 下载对象
   */
  @Test
  void test_downloadObject() {
    log.info("------------------------ 下载对象 ------------------------");
    String prefix = "/测试/";
    String objectName = prefix + "nginx-80.conf";
    File file = IOUtils.createFile("D:/tmp/nginx-80.conf");
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
    String objectName = "copy_nginx-80.conf";
    log.info("移除对象: {}, {}, {}", bucketName, objectName, template.removeObject(objectName, bucketName));
  }

  /**
   * 拷贝对象
   */
  @Test
  void test_copyObject() {
    String prefix = "/测试/";
    String srcName = prefix + "nginx-80.conf";
    String objectName = prefix + "copy_nginx-80.conf";
    CopySource source = CopySource.builder()
        .object(srcName) // 源对象
        .bucket(bucketName) // 源对象的桶
        .build();
    template.copyObject(source, objectName, Directive.COPY, bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 拷贝对象 ------------------------");
          ObjectWriteResponse response = result.getData();
          log.info("bucket: {}", response.bucket());
          log.info("object: {}", response.object());
          log.info("etag: {}", response.etag());
          log.info("region: {}", response.region());
          log.info("versionId: {}", response.versionId());
          log.info("headers: {}", response.headers());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  /**
   * 组合对象
   */
  @Test
  void test_composeObject() {
    // 把 nginx-80.conf 和 copy_nginx-80.conf 合并到 compose_nginx-80.conf
    String objectName = "compose_nginx-80.conf";
    template.composeObject(ComposeObjectArgs.builder()
            , objectName
            , Arrays.asList(
                ComposeSource.builder().object("nginx-80.conf").bucket(bucketName).build(),
                ComposeSource.builder().object("copy_nginx-80.conf").bucket(bucketName).build()
            )
            , bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 合并对象 ------------------------");
          ObjectWriteResponse response = result.getData();
          log.info("bucket: {}", response.bucket());
          log.info("object: {}", response.object());
          log.info("etag: {}", response.etag());
          log.info("region: {}", response.region());
          log.info("versionId: {}", response.versionId());
          log.info("headers: {}", response.headers());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  @Test
  void test_setObjectTags() {
    String objectName = "nginx-80.conf";
    template.setObjectTags(objectName
            , MinioUtils.mapOf(Pair.of("Author", "dxa"), Pair.of("Description", MinioUtils.encodeURL("测试")))
            , bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 设置对象标签 ------------------------");
          log.info("设置结果: {}, error: {}", result.isSuccessful(), result.getMessage());
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

  @Test
  void test_getObjectTags() {
    String objectName = "nginx-80.conf";
    template.getObjectTags(objectName, bucketName)
        .promise()
        .then((result, data) -> {
          log.info("------------------------ 获取对象标签 ------------------------");
          Tags tags = result.getData();
          Map<String, String> map = tags.get();
          log.info("tags: {}", map);
        })
        .error((result, error) -> {
          log.info("请求失败: {}", result.getMessage());
          //error.printStackTrace();
        });
  }

//  @Test
//  void test_setBucketPolicy() {
//    template.setBucketPolicy(MinIOUtils.setBucketPolicy(bucketName, ""));
//    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, template.bucketExists(bucketName));
//  }

}