package com.benefitj.spring.swagger;

import com.benefitj.core.ShutdownHook;
import com.benefitj.core.TimeUtils;
import com.benefitj.core.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import springfox.documentation.RequestHandler;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.util.List;
import java.util.Optional;

@Slf4j
public class SpringfoxFixedBug implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof WebMvcRequestHandlerProvider) {
      return new WebMvcRequestHandlerProviderWrapper((WebMvcRequestHandlerProvider) bean);
    }
    return bean;
  }

  public static class WebMvcRequestHandlerProviderWrapper extends WebMvcRequestHandlerProvider {

    private WebMvcRequestHandlerProvider provider;

    public WebMvcRequestHandlerProviderWrapper(WebMvcRequestHandlerProvider provider) {
      super(Optional.ofNullable(null), null, null);
      this.provider = provider;
    }

    @Override
    public List<RequestHandler> requestHandlers() {
      try {
        return provider.requestHandlers();
      } catch (Exception e) {
        final long start = TimeUtils.now();
        ShutdownHook.register(() -> {
          if (TimeUtils.diffNow(start) <= 60_000) {
            log.error("\"springboot version >= 2.6 \"，建议配置[ spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER ]");
          }
        });
        throw e;
      }
    }
  }
}
