package com.benefitj.mybatisplus.config;

import com.benefitj.mybatisplus.security.UserTokenManager;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@EnableSwaggerApi
@PropertySource(value = "classpath:swagger-api-info.properties", encoding = "UTF-8")
@EnableHttpLoggingHandler
@EnableCustomArgumentResolverWebMvcConfigurer
@Configuration
public class WebMvcConfig {

  @Bean
  public UserTokenManager userTokenManager() {
    return new UserTokenManager() {
      @Override
      public String getToken() {
        return "eyJhbGciOiJIUzUxMiJ9.eyJkZXBhcnRzIjoiW1wiNDIwMmYyNDk5ODc5NDg3NThmYmMwNjM4NTU3ODdjNjBcIixcIjBiOTlkZmM5YmY1NDRiYWZhZjUxOGNjMTJkMzllN2QwXCIsXCI5MWQ2ZWFkMjJiNmI0M2I2OTBkOGRlYmE4ODVkMDc5MlwiXSIsIm9yZ0lkIjoiNDIwMmYyNDk5ODc5NDg3NThmYmMwNjM4NTU3ODdjNjAiLCJyb290T3JnSWQiOiI5MWQ2ZWFkMjJiNmI0M2I2OTBkOGRlYmE4ODVkMDc5MiIsImp0aSI6Ijg5Y2ZlODE2NWQ3YzRlYTg5ZTc4ZWVlMmUxZGE2MzBkIiwic3ViIjoiNGQ0NGU1MDU5MWEzZWZhNDEzNjhiOWM2N2ViMDhhMzciLCJpc3MiOiJoc3JnIiwiaWF0IjoxNjcyNzEyODAxLCJleHAiOjE2NzMzMTc2MDF9.OPPzK3mJ9JwrP-TmyVPBgKlGitpPAF2azvewGQFcSYheZndYRwof4xl0YRsVANrMXGyKi1hlSeEY_mdr1iCJgA";
      }

      @Override
      public String getUserId() {
        return "4d44e50591a3efa41368b9c67eb08a37";
      }

      @Override
      public String getOrgId() {
        return "4202f249987948758fbc063855787c60";
      }
    };
  }

}
