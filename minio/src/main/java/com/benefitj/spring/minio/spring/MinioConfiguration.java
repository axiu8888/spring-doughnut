package com.benefitj.spring.minio.spring;

import com.benefitj.spring.minio.*;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置
 */
@EnableConfigurationProperties
@Configuration
public class MinioConfiguration {

  /**
   * 属性配置
   */
  @ConfigurationProperties("spring.minio")
  @ConditionalOnMissingBean
  @Bean
  public MinioOptions minioOptions() {
    return new MinioOptions();
  }

  @ConditionalOnMissingBean
  @Bean
  public IMinioClient minioClient(MinioOptions options) {
    MinioClient.Builder builder = MinioClient.builder();
    builder.httpClient(new OkHttpClient.Builder()
        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(options.getLogLevel()))
        .build());
    builder.endpoint(options.getEndpoint(), options.getPort(), false);
    if (StringUtils.isNotBlank(options.getRegion())) {
      builder.region(options.getRegion());
    }
    if (StringUtils.isNoneBlank(options.getAccessKey(), options.getSecretKey())) {
      builder.credentials(options.getAccessKey(), options.getSecretKey());
    }
    return MinioUtils.newProxy(builder.build());
  }

  @ConditionalOnMissingBean
  @Bean
  public IMinioTemplate minioTemplate(MinioOptions options,
                                      IMinioClient minioClient) {
    return new MinioTemplateImpl(options, minioClient);
  }

}
