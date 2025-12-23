package com.benefitj.spring.mvc.jsonparam;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.PlaceHolder;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.mvc.CustomHandlerMethodArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

/**
 * JSON参数解析
 */
@Slf4j
public class JsonParamArgumentResolver implements CustomHandlerMethodArgumentResolver {

  public static final String ATTRIBUTE_BODY = "body";
  public static final String ATTRIBUTE_JSON = "json";

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(JsonParam.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
    final JsonParam jp = parameter.getParameterAnnotation(JsonParam.class);
    String name = StringUtils.isNotBlank(jp.value()) ? jp.value() : parameter.getParameterName();
    // 获取请求体
    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
    try {
      JSONObject json = (JSONObject) request.getAttribute(ATTRIBUTE_JSON);
      if (json == null) {
        if (request.getInputStream().available() > 0) {
          try {
            Charset encoding = Charset.forName(StringUtils.defaultString(request.getCharacterEncoding(), "UTF-8"));
            String body = IOUtils.readAsString(request.getInputStream(), encoding, false);
            request.setAttribute(ATTRIBUTE_BODY, body);
            json = JSON.parseObject(body);
          } catch (Exception e) {
            log.warn(PlaceHolder.fmt("@JsonParam({}) 解析错误: \n{}", name, CatchUtils.getLogStackTrace(e)));
            return null;
          }
        } else {
          final JSONObject jo = new JSONObject();
          ServletUtils.getParameterMap().forEach((k, v) -> {
            if (k.equals(name) && parameter.getParameterType().isArray()) {
              jo.put(k, v);
            } else {
              jo.put(k, v.length == 1 ? v[0] : v);
            }
          });
          json = jo;
        }
        request.setAttribute(ATTRIBUTE_JSON, json);
      }
      return json != null ? json.getObject(name, parameter.getParameterType()) : null;
    } catch (Throwable e) {
      log.error("解析失败: {}, {}, error -->: {}", name, parameter.getParameterType(), CatchUtils.getLogStackTrace(e));
      return null;
    }
  }
}

