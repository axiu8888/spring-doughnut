package com.benefitj.spring.mvc.query;

import com.alibaba.fastjson.JSONObject;
import com.benefitj.spring.mvc.CustomHandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * QueryBody参数解析
 */
public class QueryBodyArgumentResolver implements CustomHandlerMethodArgumentResolver {

  private Class<? extends QueryRequest> bodyType;
  private Class<? extends Annotation> annotationType;

  public QueryBodyArgumentResolver(Class<? extends QueryRequest> bodyType,
                                   Class<? extends Annotation> annotationType) {
    this.bodyType = bodyType;
    this.annotationType = annotationType;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> parameterType = parameter.getParameterType();
    return parameter.hasParameterAnnotation(getAnnotationType())
        && parameterType.isAssignableFrom(getBodyType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    JSONObject json = new JSONObject();
    webRequest.getParameterMap().forEach((name, values) ->
        json.put(name.startsWith("condition.") ? name.replaceFirst("condition.", "") : name, values.length == 1 ? values[0] : values.length != 0 ? values : null));
    Object condition = json.toJavaObject(((ParameterizedType)parameter.getGenericParameterType()).getActualTypeArguments()[0]);
    QueryRequest request = json.toJavaObject(getBodyType());
    request.setCondition(condition);
    request.setParameters(json);
    return request;
  }

  public Class<? extends QueryRequest> getBodyType() {
    return bodyType;
  }

  public void setBodyType(Class<? extends QueryRequest> bodyType) {
    this.bodyType = bodyType;
  }

  public Class<? extends Annotation> getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(Class<? extends Annotation> annotationType) {
    this.annotationType = annotationType;
  }
}
