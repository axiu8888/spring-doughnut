package com.benefitj.spring.ctx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(0)
@Component
public class SpringCtxHolderInitializer implements ApplicationContextAware, DisposableBean {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final SpringCtxHolder holder = SpringCtxHolder.getInstance();

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    logger.debug("初始化ApplicationContext");
    // 注入ApplicationContext
    holder.setDestroy(false);
    holder.setContext(applicationContext);
  }

  @Override
  public void destroy() throws Exception {
    holder.setDestroy(true);
    //holder.setContext(null);
    logger.debug("销毁ApplicationContext");
  }


}
