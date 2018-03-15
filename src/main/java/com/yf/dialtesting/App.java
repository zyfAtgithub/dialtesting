package com.yf.dialtesting;

import com.yf.dialtesting.model.DialTestingInfo;
import com.yf.dialtesting.util.http.DialTesting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@EnableAutoConfiguration  
public class App {

    /**
     * 拨测客户端
     * @param map
     * @return
     */
    @RequestMapping("/")
    public String dial(HashMap<String,Object> map){
        return "dialtesting";
    }

//    /**
//     * 开始拨测任务
//     * @param url
//     * @param dialCnt
//     * @param interval
//     * @param concurrentNum
//     * @param conTimeout
//     * @param soTimeout
//     * @param proxyEnabled
//     * @param proxyList
//     * @return
//     */
//    @RequestMapping(value ="/dial", method = RequestMethod.POST)
//    @ResponseBody
//    public String dial(@RequestParam("url")String url, @RequestParam("dialCnt")Integer dialCnt,
//                       @RequestParam("interval")Integer interval, @RequestParam("concurrentNum")Integer concurrentNum,
//                       @RequestParam("conTimeout")Integer conTimeout,@RequestParam("soTimeout")Integer soTimeout,
//                       @RequestParam("proxyEnabled")Boolean proxyEnabled, @RequestParam("proxyList")String proxyList){
//        return DialTesting.dial(url, dialCnt, interval,  concurrentNum, conTimeout, soTimeout, proxyEnabled, proxyList);
//    }

    /**
     * 开始拨测任务
     * @param dialTestingInfo
     * @return
     */
    @RequestMapping(value ="/dial", method = RequestMethod.POST)
    @ResponseBody
    public String dial(@RequestBody DialTestingInfo dialTestingInfo){
        return DialTesting.dial(dialTestingInfo);
    }


    /**
     * 查看拨测进度
     * @return
     */
    @RequestMapping(value ="/qryTaskProgress", method = RequestMethod.GET)
    @ResponseBody
    public String qryTaskProgress(){
        return DialTesting.loadTaskProgress();
    }

    /**
     * 停止所有正在进行的拨测任务
     * @return
     */
    @RequestMapping(value ="/stopAllTask", method = RequestMethod.POST)
    @ResponseBody
    public String stopAllTask(){
        return DialTesting.stopAllTask();
    }

    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

}