package combenefitj.httpproxy;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import okhttp3.*;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@EnableSpringCtxInit
@EnableAppStateListener
@SpringBootApplication
public class HttpProxyApp {
  public static void main(String[] args) {
    SpringApplication.run(HttpProxyApp.class);
  }

  static {
    AppStateHook.register(
        event -> {
          System.err.println("【" + SpringCtxHolder.getAppName() + "】 start ...");
        }
        , event -> {
          System.err.println("【" + SpringCtxHolder.getAppName() + "】 stop ...");
        });
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onAppStart() {
    // 代理验证的账号及密码
    final OkHttpClient proxyClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.198", 80)))
        .proxyAuthenticator(new Authenticator() {
          @Override
          public Request authenticate(Route route, Response response) throws IOException {
            String credential = Credentials.basic("admin", "hsrg8888");
            return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
          }
        })
        .build();
    EventLoop.newSingle(false).execute(() -> {
      try {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir("D:/home/znsx/tomcat");
        String contextPath = "";
        StandardContext context = new StandardContext();
        context.setPath(contextPath);
        context.addLifecycleListener(new Tomcat.FixContextListener());
        tomcat.getHost().addChild(context);

        tomcat.addServlet(contextPath, "apiServlet", new GenericServlet() {
          @Override
          public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            proxyClient.newCall(wrapRequest(request, "/supprt/api/"));
          }
        });
        context.addServletMappingDecoded("/support/api", "apiServlet");
        tomcat.addWebapp("/supportApp", "D:/home/znsx/supportApp");
        //设置不开启keep-alive
        //AbstractHttp11Protocol abstractHttp11Protocol = (AbstractHttp11Protocol)(tomcat.getConnector().getProtocolHandler());
        //abstractHttp11Protocol.setMaxKeepAliveRequests(1);
        tomcat.start();
        tomcat.getServer().await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

  }

  String proxyHost = "http://192.168.1.198:80";

  private Request wrapRequest(HttpServletRequest request, String apiPrefix, String proxyApiPrefix) {
    Request.Builder builder = new Request.Builder();
    // 添加header
    enumerationToMap(request.getHeaderNames(), request::getHeader).forEach(builder::addHeader);
    builder.url(proxyHost + request.getRequestURI().replaceFirst(apiPrefix, proxyApiPrefix));

    try {
      request.getParts().forEach(new Consumer<Part>() {
        @Override
        public void accept(Part part) {
//          part.getContentType();
//          MultipartBody.Builder partBuilder = new MultipartBody.Builder();
//          iteratorToMap(part.getHeaderNames(), part::getHeader).forEach(partBuilder::addFormDataPart);
//          partBuilder.addPart(RequestBody.create(part.getInputStream(), part.getName(), part.getContentType()));
//          partBuilder.build();
        }
      });
    } catch (IOException | ServletException e) {
      e.printStackTrace();
    }

    switch (request.getMethod().toUpperCase()) {
      case "GET":
        break;
      case "PUT":
      case "POST":
        break;
    }


    return builder.build();
  }

  /**
   * Enumeration 转换为Map
   *
   * @param itr        Iterator
   * @param mappedFunc 转换
   * @param <K>        键
   * @param <V>        值
   * @return 返回Map
   */
  public <K, V> Map<K, V> iteratorToMap(Iterable<K> itr, Function<K, V> mappedFunc) {
    return iteratorToMap(itr, mappedFunc, new LinkedHashMap<>());
  }

  /**
   * Enumeration 转换为Map
   *
   * @param itr        Iterator
   * @param mappedFunc 转换
   * @param map        存储的Map
   * @param <K>        键
   * @param <V>        值
   * @return 返回Map
   */
  public <K, V> Map<K, V> iteratorToMap(Iterable<K> itr, Function<K, V> mappedFunc, Map<K, V> map) {
    itr.forEach(key -> map.put(key, mappedFunc.apply(key)));
    return map;
  }

  /**
   * Enumeration 转换为Map
   *
   * @param e          Enumeration
   * @param mappedFunc 转换
   * @param <K>        键
   * @param <V>        值
   * @return 返回Map
   */
  public <K, V> Map<K, V> enumerationToMap(Enumeration<K> e, Function<K, V> mappedFunc) {
    return enumerationToMap(e, mappedFunc, new LinkedHashMap<>());
  }

  /**
   * Enumeration 转换为Map
   *
   * @param e          Enumeration
   * @param mappedFunc 转换
   * @param map        存储的Map
   * @param <K>        键
   * @param <V>        值
   * @return 返回Map
   */
  public <K, V> Map<K, V> enumerationToMap(Enumeration<K> e, Function<K, V> mappedFunc, Map<K, V> map) {
    K key;
    V value;
    while (e.hasMoreElements()) {
      key = e.nextElement();
      value = mappedFunc.apply(key);
      map.put(key, value);
    }
    return map;
  }

}
