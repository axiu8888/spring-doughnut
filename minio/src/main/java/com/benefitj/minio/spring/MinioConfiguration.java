package com.benefitj.minio.spring;

import com.benefitj.minio.IMinioClient;
import com.benefitj.minio.MinioOptions;
import com.benefitj.minio.MinioTemplate;
import com.benefitj.minio.MinioUtils;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import io.minio.MinioClient;
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
@EnableSpringCtxInit
@Configuration
public class MinioConfiguration {

  /**
   * 属性配置
   */
  @ConfigurationProperties("spring.minio")
  @ConditionalOnMissingBean
  @Bean
  public MinioOptions minIOOptions() {
    return new MinioOptions();
  }

  @ConditionalOnMissingBean
  @Bean
  public IMinioClient minIOClient(MinioOptions options) {
    MinioClient.Builder builder = MinioClient.builder();
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
  public MinioTemplate minIOTemplate(MinioOptions options, IMinioClient client) {
    MinioTemplate template = new MinioTemplate();
    template.setOptions(options);
    template.setClient(client);
    return template;
  }

}
