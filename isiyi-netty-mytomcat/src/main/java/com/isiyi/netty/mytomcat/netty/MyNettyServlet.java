package com.isiyi.netty.mytomcat.netty;

import com.isiyi.netty.mytomcat.io.MyRequest;
import com.isiyi.netty.mytomcat.io.MyResponse;

public abstract class MyNettyServlet {

    public void service(MyNettyRequest request, MyNettyResponse response) throws Exception{
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request, response);
        }else {
            doPost(request, response);
        }


    }

    public abstract void doGet(MyNettyRequest request, MyNettyResponse response) throws Exception;

    public abstract void doPost(MyNettyRequest request, MyNettyResponse response) throws Exception;

}
