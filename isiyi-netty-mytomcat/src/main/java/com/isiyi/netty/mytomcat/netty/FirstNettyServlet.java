package com.isiyi.netty.mytomcat.netty;


public class FirstNettyServlet extends MyNettyServlet {
    @Override
    public void doGet(MyNettyRequest request, MyNettyResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(MyNettyRequest request, MyNettyResponse response) throws Exception {
        response.write("this is a first servlet");
    }
}
