package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@EnableRedisMessageChannel
@EnableHttpLoggingHandler       // HTTP请求日志
@EnableRedisRateLimiter         // redis RateLimiter
@EnableEventBusPoster           // eventbus
@EnableAthenapdf                // PDF
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
//    SpringApplication.run(SpringbootDoughnutApplication.class, args);

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
        System.out.println("Bucket 'hsrg' already exists.");
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
      System.out.println(
          "'D:/home/" + filename + "' is successfully uploaded as object '" + filename + "' to bucket 'v'.");

      //EventLoop.single().execute(() -> System.exit(0));

    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
      System.out.println("HTTP trace: " + e.httpTrace());
    } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }

    System.err.println("finish ........");

  }

  static {
    AppStateHook.register(
        event -> System.err.println("app started ..."),
        event -> System.err.println("app stopped ...")
    );
  }

}
