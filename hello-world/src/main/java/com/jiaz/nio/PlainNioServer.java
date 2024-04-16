package com.jiaz.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {

  public void serve(int port) throws IOException {
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);
    ServerSocket ss = serverChannel.socket();
    InetSocketAddress address = new InetSocketAddress(port);
    ss.bind(address);
    // 打开Selector 来处理Channel
    Selector selector = Selector.open();
    //将ServerSocket注册到Selector以接受连接
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
    while (true) {
      // 等待连接
      selector.select();
      // 获取已准备好的Channel
      Set<SelectionKey> readyKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = readyKeys.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        iterator.remove();
        //检查事件是否是一个新的已经就绪可以被接受的连接
        if (key.isAcceptable()) {
          //接受连接并创建一个SocketChannel
          ServerSocketChannel server = (ServerSocketChannel) key.channel();
          SocketChannel client = server.accept();
          client.configureBlocking(false);
          //接受客户端，并将它注册到选择器
          client.register(selector, SelectionKey.OP_WRITE |
              SelectionKey.OP_READ, msg.duplicate());
          System.out.println(
              "Accepted connection from " + client);
        }
        //检查套接字是否已经准备好写数据
        if (key.isWritable()) {
          SocketChannel client =
              (SocketChannel) key.channel();
          ByteBuffer buffer =
              (ByteBuffer) key.attachment();
          while (buffer.hasRemaining()) {
            //将数据写到已连接的客户端
            if (client.write(buffer) == 0) {
              break;
            }
          }
          //关闭连接
          client.close();
        }

      }
    }
  }
}
