package com.benefitj.examples.config;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.IdUtils;
import com.benefitj.core.executable.Instantiator;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.mvc.jsonbody.BodyHttpServletRequestWrapper;
import com.benefitj.spring.mvc.jsonbody.JsonBodyProcessor;
import com.benefitj.spring.mvc.jsonbody.JsonBodyMappingSearcher;
import com.benefitj.spring.mvc.jsonbody.JsonBodyRequest;
import com.benefitj.spring.mvc.matcher.OrRequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * body请求处理
 */
public class BodyWrappedFilter extends OncePerRequestFilter {

  final JsonBodyMappingSearcher searcher;

  public BodyWrappedFilter(JsonBodyMappingSearcher searcher) {
    this.searcher = searcher;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    if (ServletUtils.isMultiPart(request)) {
      chain.doFilter(request, response);
      return;
    }
    //"Content-Disposition"
    HttpMethod method = HttpMethod.resolve(request.getMethod());
    if (method == HttpMethod.POST || method == HttpMethod.PUT) {
      for (Map.Entry<OrRequestMatcher, AnnotationMetadata> entry : searcher.getApis().entrySet()) {
        if (entry.getKey().matches(request)) {
          JsonBodyRequest annotation = entry.getValue().getAnnotations(JsonBodyRequest.class).get(0);
          JsonBodyRequest.From[] source = annotation.source();
          AtomicReference<Exception> errorRef = new AtomicReference<>();
          for (JsonBodyRequest.From from : source) {
            try {
              switch (from) {
                case spring:
                  process(request, response, chain, SpringCtxHolder.getBean(annotation.value()), entry);
                  break;
                case create:
                  process(request, response, chain, Instantiator.get().create(annotation.value()), entry);
                  break;
              }
              return;
            } catch (Exception e) {
              errorRef.compareAndSet(null, e);
              //chain.doFilter(request, response);
            }
          }
          if (errorRef.get() != null) {
            ServletUtils.write(response, 400, errorRef.get().getMessage());
          }
          return;
        }
        chain.doFilter(request, response);
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain, JsonBodyProcessor processor, Map.Entry<OrRequestMatcher, AnnotationMetadata> entry) throws ServletException, IOException {
    BodyHttpServletRequestWrapper wrap = BodyHttpServletRequestWrapper.wrap(request);
    JSONObject json = wrap.getStream().toJson(processor);
    json.put("userId", IdUtils.uuid());
    wrap.setNewInput(json.toJSONString().getBytes(request.getCharacterEncoding()));
    chain.doFilter(wrap, response);
  }

}
