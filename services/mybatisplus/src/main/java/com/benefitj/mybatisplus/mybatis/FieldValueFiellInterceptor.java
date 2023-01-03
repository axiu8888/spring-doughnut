package com.benefitj.mybatisplus.mybatis;

import com.benefitj.mybatisplus.mybatis.FieldValueFiller;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.util.List;
import java.util.Properties;

/**
 * mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class FieldValueFiellInterceptor implements Interceptor {

  private List<FieldValueFiller> fillers;

  public FieldValueFiellInterceptor(List<FieldValueFiller> fillers) {
    this.fillers = fillers;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
    if (invocation.getArgs()[1] == null) {
      return invocation.proceed();
    }

    final Object target;
    if (invocation.getArgs()[1] instanceof MapperMethod.ParamMap) {
      MapperMethod.ParamMap<?> pm = (MapperMethod.ParamMap<?>) invocation.getArgs()[1];
      //update-begin-author:scott date:20190729 for:批量更新报错issues/IZA3Q--
      if (pm.containsKey("et")) {
        target = pm.get("et");
      } else {
        target = pm.get("param1");
      }
    } else {
      target = invocation.getArgs()[1];
    }
    fillers.forEach(filler -> {
      if (filler.support(target, ms)) {
        filler.fill(target, ms);
      }
    });
    return invocation.proceed();
  }


  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
  }

}
