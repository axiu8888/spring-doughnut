package com.benefitj.minio.spring;

import com.benefitj.minio.IMinioClient;
import com.benefitj.minio.MinioOptions;
import com.benefitj.minio.MinioTemplate;
import com.benefitj.minio.MinioUtils;
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
  public MinioTemplate minioTemplate(MinioOptions options,
                                     IMinioClient minioClient) {
    MinioTemplate template = new MinioTemplate();
    template.setOptions(options);
    template.setClient(minioClient);
    return template;
  }

}
