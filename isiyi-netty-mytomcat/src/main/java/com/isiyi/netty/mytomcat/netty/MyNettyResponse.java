package com.isiyi.netty.mytomcat.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import sun.awt.geom.AreaOp;

public class MyNettyResponse {
    private ChannelHandlerContext ctx;
    private HttpRequest request;
    public MyNettyResponse(ChannelHandlerContext ctx, HttpRequest request){
        this.ctx = ctx;
        this.request = request;
    }
    public void write(String message) throws Exception {
        if(null == message || message.length() == 0){
            return;
        }
        try {
            //设置HTTP及请求头信息
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(message.getBytes("UTF-8"))
            );
            httpResponse.headers().set("Content-Type", "text/html");
            ctx.write(httpResponse);
        }finally {
            ctx.flush();
            ctx.close();
        }
    }
}
