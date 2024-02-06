package com.benefitj.spring.ctx;

import com.benefitj.spring.listener.AppStartListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@ConditionalOnMissingBean(SpringCtxHolderInitializer.class)
@Configuration
public class SpringCtxHolderInitializer implements ApplicationContextAware, DisposableBean {

  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Lazy(value = false)
  @Bean
  public SpringCtxHolder springCtxHolder(ApplicationContext context) {
    setApplicationContext(context);
    return holder;
  }

  @Bean
  public AppStartListener springCtxHolderInit(SpringCtxHolder holder) {
    return event -> {
      // ignore
      holder.getContext();
    };
  }

  private final Logger log = LoggerFactory.getLogger(getClass());

  final SpringCtxHolder holder = SpringCtxHolder.get();

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    if (holder.context != null) {
      return;
    }
    log.debug("初始化ApplicationContext");
    // 注入ApplicationContext
    holder.setDestroy(false);
    holder.setContext(context);
  }

  @Override
  public void destroy() throws Exception {
    holder.setDestroy(true);
    //holder.setContext(null);
    log.debug("销毁ApplicationContext");
  }

}
