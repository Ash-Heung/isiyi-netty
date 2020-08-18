package com.isiyi.controller;

import com.isiyi.entity.Greeting;
import com.isiyi.entity.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    /**
     * //使用MessageMapping注解来标识所有发送到“/hello”这个destination的消息，都会被路由到这个方法进行处理.
     * //使用SendTo注解来标识这个方法返回的结果，都会被发送到它指定的destination，“/topic/greetings”.
     *  //传入的参数HelloMessage为客户端发送过来的消息，是自动绑定的。
     * @param message
     * @return
     * @throws Exception
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        // 模拟处理延时
        Thread.sleep(1000);
        //根据传入的信息，返回一个欢迎消息.
        String msg = "Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!";
        return new Greeting(msg);
    }




}
