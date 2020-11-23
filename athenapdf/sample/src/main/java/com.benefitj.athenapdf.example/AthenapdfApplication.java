package com.benefitj.athenapdf.example;

import com.benefitj.athenapdf.spring.EnableAthenapdfConfiguration;
import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@EnableAutoApplicationListener
@EnableAthenapdfConfiguration
@SpringBootApplication
public class AthenapdfApplication {
  public static void main(String[] args) {
    SpringApplication.run(AthenapdfApplication.class, args);
  }

//  @Slf4j
//  @Component
//  public static class OnCallAthenapdfStarter implements IApplicationStartedEventListener {
//
//    private final AthenapdfHelper helper = AthenapdfHelper.INSTANCE;
//
//    @Autowired
//    private AthenapdfProperty property;
//
//    @Override
//    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
//      System.err.println("调用: " + JSON.toJSONString(property));
//
//      AthenapdfCall call = helper.execute(new File(property.getCacheDir())
//          , property.getUrl()
//          , property.getFilename()
//          , null
//      );
//      log.info("调用结果: {}", JSON.toJSONString(call));
//    }
//
//  }

  @Setter
  @Getter
  @Component
  @ConfigurationProperties(prefix = "spring.athenapdf")
  public static class AthenapdfProperty {
    /**
     * 保存文件的路径
     */
    private String cacheDir;

    /**
     * 文件名
     */
    private String filename;
    /**
     * HTML的路径
     */
    private String url;

  }


}
