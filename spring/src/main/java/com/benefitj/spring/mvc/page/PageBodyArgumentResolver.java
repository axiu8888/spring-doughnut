package com.benefitj.spring.mvc.page;

import com.alibaba.fastjson.JSONObject;
import com.benefitj.spring.mvc.CustomHandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 请求分页参数解析
 */
public class PageBodyArgumentResolver implements CustomHandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> parameterType = parameter.getParameterType();
    return parameter.hasParameterAnnotation(PageBody.class)
        && parameterType.isAssignableFrom(PageableRequest.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    Class<?> parameterType = parameter.getParameterType();
    Constructor<?> constructor = Arrays.stream(parameterType.getConstructors())
        .min(Comparator.comparingInt(Constructor::getParameterCount))
        .orElse(null);
    PageableRequest page;
    if (constructor != null) {
      page = (PageableRequest) constructor.newInstance();
    } else {
      page = (PageableRequest) parameterType.newInstance();
    }
    JSONObject json = new JSONObject();
    webRequest.getParameterMap().forEach((name, values) ->
        json.put(name, values.length == 1 ? values[0] : values));
    Object condition = json.toJavaObject(((ParameterizedType)parameter.getGenericParameterType()).getActualTypeArguments()[0]);
    page.setCondition(condition);
    return page;
  }

}
