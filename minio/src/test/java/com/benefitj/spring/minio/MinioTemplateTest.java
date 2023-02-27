package com.benefitj.spring.minio;

import com.benefitj.minio.IMinIOClient;
import com.benefitj.minio.MinIOHelper;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class MinioTemplateTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private IMinIOClient proxy = IMinIOClient.newProxy(MinioClient.builder()
      .endpoint("https://192.168.1.203", 9006, false)
      //.credentials("hsrg", "hsrg8888")
      .credentials("Ei4H6nMGYs9NcISp", "CYMQoY59q3NCgp4fbYnmiFnZNzXjs76T")
      .build());

  private MinIOHelper helper = MinIOHelper.get();

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void test1() {
    try {
      // Create a minioClient with the MinIO server playground, its access key and secret key.
      MinioClient minioClient = MinioClient.builder()
          .endpoint("https://192.168.1.203", 9006, false)
          //.credentials("hsrg", "hsrg8888")
          .credentials("Ei4H6nMGYs9NcISp", "CYMQoY59q3NCgp4fbYnmiFnZNzXjs76T")
          .build();

      // Make 'asiatrip' bucket if not exist.
      boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("hsrg").build());
      if (!found) {
        // Make a new bucket called 'asiatrip'.
        minioClient.makeBucket(MakeBucketArgs.builder().bucket("hsrg").build());
      } else {
        log.info("Bucket 'hsrg' already exists.");
      }

      // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
      // 'asiatrip'.
      String filename = "nginx-80.conf";
      minioClient.uploadObject(
          UploadObjectArgs.builder()
              .bucket("hsrg")
              .object(filename)
              .filename("D:\\home\\logs\\" + filename)
              .build());
      log.info(
          "'D:/home/" + filename + "' is successfully uploaded as object '" + filename + "' to bucket 'v'.");

      //EventLoop.single().execute(() -> System.exit(0));

    } catch (MinioException e) {
      log.info("Error occurred: " + e);
      log.info("HTTP trace: " + e.httpTrace());
    } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }

    log.info("finish ........");

  }

  @Test
  void testFileType() {
    MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
    String contentType = mftm.getContentType("D:\\home\\logs\\nginx-80.conf");
    log.info("contentType: {}", contentType);
  }

  @Test
  void test_bucketExists() {
    boolean bucketExists = proxy.bucketExists(helper.bucketExistsArgs("hsrg"));
    log.info("bucketExists ==>: {}", bucketExists);
  }

  @Test
  void test_makeBucket() {
    String bucketName = "test";
    proxy.makeBucket(helper.makeBucket(bucketName));
    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, proxy.bucketExists(helper.bucketExistsArgs(bucketName)));
  }

  @Test
  void test_setBucketPolicy() {
    String bucketName = "test";
    proxy.setBucketPolicy(helper.setBucketPolicy(bucketName, ""));
    log.info("make bucket, [{}] bucketExists ==>: {}", bucketName, proxy.bucketExists(helper.bucketExistsArgs(bucketName)));
  }

}