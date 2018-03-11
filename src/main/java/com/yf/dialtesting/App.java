package com.yf.dialtesting;

import com.yf.dialtesting.util.http.DialTesting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
@EnableAutoConfiguration  
public class App {
      
     @RequestMapping(value ="/home", method = RequestMethod.GET)
     @ResponseBody
    public String home(){
        return "你好，Spring Boot";
    }

    @RequestMapping(value ="/dial", method = RequestMethod.POST)
    @ResponseBody
    public String dial(@RequestParam("url")String url, @RequestParam("dialCnt")Integer dialCnt,
                       @RequestParam("interval")Integer interval, @RequestParam("concurrentNum")Integer concurrentNum,
                       @RequestParam("proxyEnabled")Boolean proxyEnabled, @RequestParam("proxyList")String proxyList){
        return DialTesting.dial(url, dialCnt, interval,  concurrentNum, proxyEnabled, proxyList);
    }

    @RequestMapping("/")
    public String dial(HashMap<String,Object> map){
        map.put("hello","hello");
        return"/dialtesting";
    }


    @RequestMapping(value ="/qryTaskProgress", method = RequestMethod.GET)
    @ResponseBody
    public String qryTaskProgress(){
        return DialTesting.loadTaskProgress();
    }


    public static void main(String[] args){  
        SpringApplication.run(App.class, args);
          
    }  

}