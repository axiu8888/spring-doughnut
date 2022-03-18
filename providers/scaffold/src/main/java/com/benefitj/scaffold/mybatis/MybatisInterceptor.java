package com.benefitj.scaffold.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.util.List;

/**
 * 填值插件
 */
@Slf4j
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class MybatisInterceptor implements Interceptor {

  private List<InterceptorHandler> handlers;

  public MybatisInterceptor(List<InterceptorHandler> handlers) {
    this.handlers = handlers;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
    SqlCommandType sqlCommandType = statement.getSqlCommandType();
    Object parameter = invocation.getArgs()[1];
    for (InterceptorHandler callback : handlers) {
      Object result = callback.intercept(invocation, statement, sqlCommandType, parameter);
      if (result != null) {
        return result;
      }
    }
    return invocation.proceed();
  }

}
