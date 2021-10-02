package com.benefitj.frontend;

import com.benefitj.core.IOUtils;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.mvc.matcher.AntPathRequestMatcher;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

@EnableConfigurationProperties
@SpringBootApplication
public class FrontendApplication {
  public static void main(String[] args) {
    SpringApplication.run(FrontendApplication.class, args);
  }

  @Controller
  @RequestMapping("/")
  public static class ProxyController {

    /**
     * 静态资源
     */
    @Autowired
    private FrontendConf conf;

    private Map<AntPathRequestMatcher, ProxyWrapper> pathMatcherProxies = new LinkedHashMap<>();

    private final Map<String, File> fileCache = new WeakHashMap<>();

    @PostConstruct
    public void onInit() {
      Proxy[] proxies = conf.getProxies();
      if (proxies != null) {
        for (Proxy proxy : proxies) {
          AntPathRequestMatcher matcher = new AntPathRequestMatcher(proxy.getPath(), HttpMethod.GET.name());
          pathMatcherProxies.put(matcher, BeanHelper.copy(proxy, ProxyWrapper.class));
        }
      }
    }

    @RequestMapping("/**")
    public void anyRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
      if (!pathMatcherProxies.isEmpty()) {
        for (AntPathRequestMatcher matcher : pathMatcherProxies.keySet()) {
          if (matcher.matches(request)) {
            String uri = request.getRequestURI();
            if (!(StringUtils.isBlank(request.getContextPath()) || "/".equals(request.getContextPath()))) {
              uri = request.getRequestURI().replaceFirst(request.getContextPath(), "");
            }
            ProxyWrapper proxy = pathMatcherProxies.get(matcher);
            if (StringUtils.isNotBlank(proxy.getResourceDir())) {
              File file = fileCache.get(proxy.getResourceDir() + uri);
              if (file == null) {
                fileCache.put(proxy.getResourceDir() + uri, file = new File(proxy.getResourceDir(), uri));
              }
              System.err.println(uri + " ==>: " + file.exists() + "\n");
              if (file.isFile()) {
                IOUtils.write(file, response.getOutputStream(), false);
                return;
              }
            }

//            // 尝试代理
//            if (StringUtils.isNotBlank(proxy.getRemoteAddr())) {
//              if (proxy.getClient() == null) {
//                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                proxy.setClient(new OkHttpClient.Builder()
//                    .connectTimeout(5, TimeUnit.SECONDS)
//                    .readTimeout(120, TimeUnit.SECONDS)
//                    .writeTimeout(120, TimeUnit.SECONDS)
//                    .addNetworkInterceptor(loggingInterceptor)
//                    .build());
//              }
//
//
//
//              proxy.getClient().newCall(new Request.Builder()
//                  .method(request.getMethod(), null)
//                  .url(proxy.getRemoteAddr() + )
//                  .build());
//              return;
//            }
          }
        }
      }
      // 返回404
      response.setStatus(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
      response.flushBuffer();
    }
  }

  @Setter
  @Getter
  @ConfigurationProperties(prefix = "frontend")
  @Component
  public static class FrontendConf {

    /**
     * 代理
     */
    private Proxy[] proxies;

  }

  @Data
  public static class Proxy {

    /**
     * 代理的路径，默认当前 static 目录，如：/static/**
     */
    private String path = "./static/";
    /**
     * 静态资源目录，如： D:/static/
     */
    private String resourceDir = "";
    /**
     * 远程地址，如：https://192.168.1.123:8080/api/
     */
    private String remoteAddr = "";

  }

  @Data
  public static class ProxyWrapper extends Proxy {
    /**
     * 客户端
     */
    private OkHttpClient client;

  }

}
