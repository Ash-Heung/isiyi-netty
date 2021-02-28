package com.isiyi.netty.mytomcat.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyTomcat {
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

        try {
            server = new ServerSocket(this.port);

            System.out.println("MyTomcat已经启动，监听端口为："+ this.port);
            //等待用户请求，用一个死循环等待用户请求
            while (true) {
                Socket accept = server.accept();
                // 处理HTTP 请求
                process(accept);
            }
        }catch (Exception e){
            e.printStackTrace();
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
