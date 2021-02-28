package com.isiyi.netty.mytomcat.netty;

import com.isiyi.netty.mytomcat.io.MyRequest;
import com.isiyi.netty.mytomcat.io.MyResponse;
import com.isiyi.netty.mytomcat.io.MyServlet;
import com.isiyi.netty.mytomcat.io.MyTomcat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

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
    private Map<String, MyServlet> servletMap = new HashMap<>();

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
                    MyServlet obj = (MyServlet) Class.forName(className).newInstance();
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
                                    .addLast(new HttpResponseDecoder())
                                    .addLast(new HttpRequestDecoder())
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

    private void process(Socket accept) throws Exception{

        InputStream inputStream = accept.getInputStream();
        OutputStream outputStream = accept.getOutputStream();
        // 处理请求头
        MyRequest request = new MyRequest(inputStream);
        MyResponse response = new MyResponse(outputStream);

        // 从协议内容获取URL， 把相应的反射进行实例化
        String url = request.getUrl();

        if(servletMap.containsKey(url)){
            servletMap.get(url).service(request, response);
        }else {
            response.write("404-NOT_FOUND");
        }

        outputStream.flush();
        outputStream.close();

        inputStream.close();

        accept.close();
    }

    public static void main(String[] args) {
        new MyTomcat().start();
    }

}
