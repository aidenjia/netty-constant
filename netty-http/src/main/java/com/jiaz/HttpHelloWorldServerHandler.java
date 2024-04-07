package com.jiaz;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaz.pojo.User;
import com.jiaz.serialize.JSONSerializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AsciiString;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  private static final Logger logger = LoggerFactory.getLogger(HttpHelloWorldServerHandler.class);



  private HttpHeaders headers;
  private HttpRequest request;
  private FullHttpRequest fullRequest;

  private static final String FAVICON_ICO = "/favicon.ico";
  private static final AsciiString CONTENT_TYPE = AsciiString.of("Content-Type");
  private static final AsciiString CONTENT_LENGTH = AsciiString.of("Content-Length");
  private static final AsciiString CONNECTION = AsciiString.of("Connection");
  private static final AsciiString KEEP_ALIVE = AsciiString.of("keep-alive");

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    User user = new User();
    user.setUserName("sanshengshui");
    user.setDate(new Date());
    if(msg instanceof HttpRequest){
      request = (HttpRequest) msg;
      headers = request.headers();
      String uri = request.uri();
      if (uri.equals(FAVICON_ICO)){
        return;
      }
      HttpMethod method = request.method();
      if(method.equals(HttpMethod.GET)){
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(
            CharEncoding.UTF_8));
        Map<String, List<String>> uriAttributes = queryDecoder.parameters();
        //此处仅打印请求参数（你可以根据业务需求自定义处理）
        for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
          for (String attrVal : attr.getValue()) {
            logger.info(attr.getKey() + "=" + attrVal);
          }
        }
        user.setMethod("get");
      }else if (method.equals(HttpMethod.POST)){
        //POST请求,由于你需要从消息体中获取数据,因此有必要把msg转换成FullHttpRequest
        fullRequest = (FullHttpRequest)msg;
        //根据不同的Content_Type处理body数据
        dealWithContentType();
        user.setMethod("post");
      }
      JSONSerializer jsonSerializer = new JSONSerializer();
      byte[] content = jsonSerializer.serialize(user);
      FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
      response.headers().set(CONTENT_TYPE, "text/plain");
      response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
      boolean keepAlive = HttpUtil.isKeepAlive(request);
      if (!keepAlive) {
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
      } else {
        response.headers().set(CONNECTION, KEEP_ALIVE);
        ctx.write(response);
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  /**
   * 简单处理常用几种 Content-Type 的 POST 内容（可自行扩展）
   * @throws Exception
   */
  private void dealWithContentType() throws Exception{
    String contentType = getContentType();
    //可以使用HttpJsonDecoder
    if(contentType.equals("application/json")){
      String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
      JSONObject obj = JSON.parseObject(jsonStr);
      for(Map.Entry<String, Object> item : obj.entrySet()){
        logger.info(item.getKey()+"="+item.getValue().toString());
      }

    }else if(contentType.equals("application/x-www-form-urlencoded")){
      //方式一：使用 QueryStringDecoder
      String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
      QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
      Map<String, List<String>> uriAttributes = queryDecoder.parameters();
      for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
        for (String attrVal : attr.getValue()) {
          logger.info(attr.getKey() + "=" + attrVal);
        }
      }

    }else if(contentType.equals("multipart/form-data")){
      //TODO 用于文件上传
    }else{
      //do nothing...
    }
  }
  private String getContentType(){
    String typeStr = headers.get("Content-Type").toString();
    String[] list = typeStr.split(";");
    return list[0];
  }

}
