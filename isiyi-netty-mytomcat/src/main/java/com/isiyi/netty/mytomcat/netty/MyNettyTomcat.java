package com.isiyi.netty.mytomcat.netty;

import com.isiyi.netty.mytomcat.io.MyRequest;
import com.isiyi.netty.mytomcat.io.MyResponse;
import com.isiyi.netty.mytomcat.io.MyServlet;
import com.isiyi.netty.mytomcat.io.MyTomcat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyNettyTomcat {

    private int port = 8080;
    private ServerSocket server;
    private Map<String, MyNettyServlet> servletMap = new HashMap<>();

    private Properties webXml = new Properties();

    private void init(){
        try {

            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");

            webXml.load(fis);

            for (Object k : webXml.keySet()) {
                String key  = k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webXml.getProperty(key);

                    String className = webXml.getProperty(servletName + ".className");
                    //单实例，多线程
                    MyNettyServlet obj = (MyNettyServlet) Class.forName(className).newInstance();
                    servletMap.put(url, obj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        // 加载配置，初始化servletMap
        init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端初始化处理
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            socketChannel.pipeline()
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new HttpResponseEncoder())
                                    .addLast(new MyTomcatHandler());
                        }
                    })
                    //针对主线程的配置，分配线程最大的数量是128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = server.bind(this.port).sync();
            System.out.println("tomcat 已经启动，监听端口为："+ this.port);
            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


    public class MyTomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;
                //转交给我们自定义的request
                MyNettyRequest request = new MyNettyRequest(ctx, req);
                //转交给我们自定义的response
                MyNettyResponse response = new MyNettyResponse(ctx, req);

                String url = request.getUrl();

                System.out.println("servletMap:"+servletMap.toString());
                if(servletMap.containsKey(url)){
                    servletMap.get(url).service(request, response);
                }else {
                    response.write("404-NOT-FOUND");
                }
            }
        }
    }

    public static void main(String[] args) {
        new MyNettyTomcat().start();
    }

}
