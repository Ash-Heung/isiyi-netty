package com.isiyi.netty.mytomcat.io;

public abstract class MyServlet {

    public void service(MyRequest request, MyResponse response) throws Exception{
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request, response);
        }else {
            doPost(request, response);
        }


    }

    public abstract void doGet(MyRequest request, MyResponse response) throws Exception;

    public abstract void doPost(MyRequest request, MyResponse response) throws Exception;

}
