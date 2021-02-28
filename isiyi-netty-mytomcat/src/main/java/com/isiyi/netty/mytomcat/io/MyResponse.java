package com.isiyi.netty.mytomcat.io;

import java.io.OutputStream;

public class MyResponse {
    private OutputStream out;
    public MyResponse(OutputStream out){
        this.out = out;
    }

    public void write(String message) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 OK\n")
                .append("content-type: text/html;charset=UTF-8;\n")
                .append("\r\n")
                .append(message);
        out.write(builder.toString().getBytes());


    }
}
