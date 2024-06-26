package com.benefitj.spring.mvc.multibody;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.PrimitiveType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * MultiRequestBody解析器
 * 解决的问题：
 * 1、单个字符串等包装类型都要写一个对象才可以用@RequestBody接收；
 * 2、多个对象需要封装到一个对象里才可以用@RequestBody接收。
 * 主要优势：
 * 1、支持通过注解的value指定JSON的key来解析对象。
 * 2、支持通过注解无value，直接根据参数名来解析对象
 * 3、支持基本类型的注入
 * 4、支持GET和其他请求方式注入
 * 5、支持通过注解无value且参数名不匹配JSON串key时，根据属性解析对象。
 * 6、支持多余属性(不解析、不报错)、支持参数“共用”（不指定value时，参数名不为JSON串的key）
 * 7、支持当value和属性名找不到匹配的key时，对象是否匹配所有属性。
 */
public class MultiRequestBodyArgumentResolver implements HandlerMethodArgumentResolver {

  public static final String JSON_BODY_ATTRIBUTE = "JSON_REQUEST_BODY";

  /**
   * 设置支持的方法参数类型
   *
   * @param parameter 方法参数
   * @return 支持的类型
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    // 支持带@MultiRequestBody注解的参数
    return parameter.hasParameterAnnotation(MultiRequestBody.class);
  }

  /**
   * 参数解析，利用fastjson
   * 注意：非基本类型返回null会报空指针异常，要通过反射或者JSON工具类创建一个空对象
   */
  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    String jsonBody = getRequestBody(webRequest);
    JSONObject json = JSON.parseObject(jsonBody);
    // 根据@MultiRequestBody注解value作为json解析的key
    MultiRequestBody annotation = parameter.getParameterAnnotation(MultiRequestBody.class);
    String key = StringUtils.getIfBlank(annotation.value(), parameter::getParameterName);
    Object value = json.get(key);
    if (key.contains(".")) {
      Object _v = null;
      for (String name : key.split("\\.")) {
        _v = (_v != null ? ((JSONObject) _v) : json).get(name);
      }
      value = _v;
    }
    if (value == null && annotation.required()) {
      throw new IllegalArgumentException(String.format("required param %s is not present", key));
    }
    // 通过注解的value或者参数名解析，能拿到value进行解析
    return value != null ? PrimitiveType.castTo(value, parameter.getParameterType()) : null;
  }

  /**
   * 获取请求体JSON字符串
   */
  private String getRequestBody(NativeWebRequest webRequest) {
    HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
    // 有就直接获取
    String jsonBody = (String) webRequest.getAttribute(JSON_BODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
    // 没有就从请求中读取
    if (jsonBody == null) {
      try {
        jsonBody = IOUtils.toString(req.getReader());
        webRequest.setAttribute(JSON_BODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return jsonBody;
  }
}
