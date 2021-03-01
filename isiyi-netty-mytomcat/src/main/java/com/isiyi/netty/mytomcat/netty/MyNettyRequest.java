package com.isiyi.netty.mytomcat.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class MyNettyRequest {
    private String url;
    private String method;
    private ChannelHandlerContext ctx;
    private HttpRequest request;
    public MyNettyRequest(ChannelHandlerContext ctx, HttpRequest request){
        this.ctx = ctx;
        this.request = request;
    }

    public Map<String, List<String>> getParameters(){
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        return queryStringDecoder.parameters();
    }

    public String getParameters(String name){
        Map<String, List<String>> parameters = getParameters();
        List<String> list = parameters.get(name);
        if(null == list){
            return null;
        }else {
            return list.get(0);
        }
    }

    public String getUrl() {
        return request.uri();
    }


    public String getMethod() {
        return request.method().name();
    }

}
