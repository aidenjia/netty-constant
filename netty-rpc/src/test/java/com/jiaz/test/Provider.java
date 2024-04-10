package com.jiaz.test;

import com.jiaz.factory.BeanManager;
import com.jiaz.registry.ZookeeperRegistry;
import com.jiaz.registry.ServerInfo;
import com.jiaz.transport.NettyServer;
import org.apache.curator.x.discovery.ServiceInstance;

public class Provider {


  public static void main(String[] args) throws Exception {
    // 创建DemoServiceImpl，并注册到BeanManager中
    BeanManager.registerBean("demoService", new DemoServiceImpl());
    // 创建ZookeeperRegistry，并将Provider的地址信息封装成ServerInfo
    // 对象注册到Zookeeper
    ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
    discovery.start();
    ServerInfo serverInfo = new ServerInfo("127.0.0.1", 20880);
    discovery.registerService(
        ServiceInstance.<ServerInfo>builder().name("demoService").payload(serverInfo).build());
    //启动DemoRpcServer，等待Client的请求
    NettyServer nettyServer = new NettyServer(20880);
  }
}
