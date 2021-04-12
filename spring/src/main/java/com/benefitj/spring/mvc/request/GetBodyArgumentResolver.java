package com.benefitj.spring.mvc.request;

import com.alibaba.fastjson.JSONObject;
import com.benefitj.spring.mvc.CustomHandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * GET请求体参数解析
 */
public class GetBodyArgumentResolver implements CustomHandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> parameterType = parameter.getParameterType();
    return parameter.hasParameterAnnotation(GetBody.class)
        && !(parameterType == Object.class
        || parameterType.isPrimitive()
        || parameterType.isArray()
        || parameterType.isAssignableFrom(Number.class)
        || parameterType.isAssignableFrom(String.class)
        || parameterType.isAssignableFrom(Boolean.class)
        || parameterType.isInterface()
        || parameterType.isAssignableFrom(ServletRequest.class)
        || parameterType.isAssignableFrom(ServletResponse.class)
        || parameterType.isAssignableFrom(MultipartFile.class)
    );
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    JSONObject json = new JSONObject();
    webRequest.getParameterMap().forEach((name, values)
        -> json.put(name, values.length == 1 ? values[0] : values));
    GetBody annotation = parameter.getMethodAnnotation(GetBody.class);
    return annotation != null && annotation.type() != Object.class
        ? json.toJavaObject(annotation.type())
        : json.toJavaObject(parameter.getParameterType());
  }

}
