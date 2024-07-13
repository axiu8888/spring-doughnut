package com.benefitj.natproxy.app;


import com.benefitj.natproxy.tcp.EnableTcpProxy;
import com.benefitj.natproxy.tcpudp.EnableTcpUdpProxy;
import com.benefitj.natproxy.udp.EnableUdpProxy;
import com.benefitj.natproxy.udptcp.EnableUdpTcpProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableTcpProxy // tcp -> tcp
@EnableUdpProxy // udp -> udp
@EnableTcpUdpProxy // tcp -> udp
@EnableUdpTcpProxy // udp -> tcp
@SpringBootApplication
@Slf4j
public class NatProxyApp {
  public static void main(String[] args) {
    SpringApplication.run(NatProxyApp.class, args);
  }
}
