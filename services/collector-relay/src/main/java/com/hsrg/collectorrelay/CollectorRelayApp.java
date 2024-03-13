package com.hsrg.collectorrelay;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.spring.listener.EnableAppStateListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Stream;


@EnableAppStateListener
@SpringBootApplication
public class CollectorRelayApp {
  public static void main(String[] args) {
    SpringApplication.run(CollectorRelayApp.class, args);
    EventLoop.main().execute(() -> {/* nothing done */});

//    changeName(new File("D:\\home\\znsx\\collector-relay\\CHE"));
//    base64ToPdf();

//    ByteArrayCopy copy = ByteArrayCopy.get();
//    byte[] data = HexUtils.hexToBytes("53656E734563686F5F3541342E305F56312E302E305F4669726D5665723A56322E302E315F486172645665723A56322E302E305F49443A31313030303834365F526573703A312D31366269742D3235487A5F4563673A31306269742D323030487A5F33417865733A31306269742D3235487A5F53704F323A376269742D3530487A5F0000000000000000000000000000000000000000000000000000000000000237BAC00002527A000000000000000031313030303834362D323032315F30345F32322D31365F33315F35372D3031362E434845000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
//    System.err.println("长度: " + (HexUtils.bytesToInt(copy.copy(data, 0xA0, 4)) / 576));
//    System.err.println("包序号: " + HexUtils.bytesToInt(copy.copy(data, 0xA0 + 4, 4)));
//    System.err.println("文件名: " + new String(copy.copy(data, 0xB0, 100), StandardCharsets.UTF_8).trim());

  }

  private static void base64ToPdf() {
    String body = IOUtils.readFileAsString(new File("D:\\home\\znsx\\base64.txt"));
    byte[] decode = Base64.getDecoder().decode(body);
    JSONObject json = JSON.parseObject(new String(decode, StandardCharsets.UTF_8));
    System.err.println(json);
    String pdf = json.getJSONObject("postDataReport").getString("PDFContent");
    File dest = IOUtils.createFile("D:/home/znsx/test.pdf");
    IOUtils.write(Base64.getDecoder().decode(pdf), dest, false);
  }

  private static void changeName(File dir) {
    File[] files = dir.listFiles();
    if (files != null && files.length > 0) {
      Stream.of(files)
          .filter(f -> f.getName().endsWith(".HEX"))
          .forEach(f -> {
            f.renameTo(new File(f.getParentFile(), f.getName().replace(".HEX", ".CHE")));
          });
    }
  }


}
