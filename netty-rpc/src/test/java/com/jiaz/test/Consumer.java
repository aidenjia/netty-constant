package com.jiaz.test;


import com.jiaz.proxy.RpcProxy;
import com.jiaz.registry.ZookeeperRegistry;
import com.jiaz.registry.ServerInfo;

public class Consumer {
    public static void main(String[] args) throws Exception {
        // 创建ZookeeperRegistr对象
        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
        discovery.start();
        // 创建代理对象，通过代理调用远端Server
        DemoService demoService = RpcProxy.newInstance(DemoService.class, discovery);
        // 调用sayHello()方法，并输出结果
        String result = demoService.sayHello("hello");
        System.out.println(result);
        // Thread.sleep(10000000L);
    }
}