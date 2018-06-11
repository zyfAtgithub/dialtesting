package com.yf.dialtesting.util.http;

import com.yf.dialtesting.model.DialTestingInfo;
import com.yf.dialtesting.util.common.DateUtils;
import com.yf.dialtesting.util.ip.ValidateUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DialTesting {

    /**
     * 懒汉式单例
     */
    private static volatile DialTesting instance;
    private DialTesting(){};
    public static DialTesting getInstance() {
        if (instance == null) {
            synchronized (DialTesting.class) {
                if (instance == null) {
                    instance = new DialTesting();
                }
            }
        }
        return instance;
    }

    //拨测任务运行标志
    private static volatile boolean running = false;
    private static int totalTaskCnt = 0;//所有task数
    private static int finishedTaskCnt = 0;//已经运行完成的task数

    /** 当前 拨测信息*/
    private static DialTestingInfo currentDialTestingInfo;

    //任务执行信息 key：线程号， value：每次访问记录的详情
    private static Map<String, List<Integer>> taskMap = new HashMap<String, List<Integer>>();
    private static Map<String, JSONObject> taskResultMap = new HashMap<String, JSONObject>();

    //停止所有当前任务标识
    private boolean stopAllTaskFLag = false;

    //原子计数器，用于任务id的获取
    AtomicInteger atomicTaskId = new AtomicInteger(1);

    private synchronized boolean isRunning() {
        return running;
    }

    public synchronized void updateFinishedTaskCnt() {
        finishedTaskCnt++;
        if (finishedTaskCnt == totalTaskCnt) {
            //所有任务都已运行完成
            running = false;
        }
    }

    public static String stopAllTask() {
        getInstance().stopAllTaskFLag = true;
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "success");
        return jsonResult.toString();
    }

    public static String loadTaskProgress() {
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("taskMap", taskMap);
        jsonResult.put("taskResultMap", taskResultMap);
        jsonResult.put("dialTestingInfo", currentDialTestingInfo);
        return jsonResult.toString();
    }

    public static String dial(DialTestingInfo dialTestingInfo) {

        JSONObject jsonResult = new JSONObject();
        if (!ValidateUtil.validateUrl(dialTestingInfo.getUrl())) {
            jsonResult.put("resultCode", "-1");
            jsonResult.put("msg", "URL格式不正确，请检查！！");
            return jsonResult.toString();
        }

        if (dialTestingInfo.isProxyEnabled() && !ValidateUtil.validateIpList(dialTestingInfo.getProxyList(), ",")) {
            jsonResult.put("resultCode", "-1");
            jsonResult.put("msg", "代理IP格式不正确，请检查！！");
            return jsonResult.toString();
        }

        DialTesting dialTesting = DialTesting.getInstance();
        if (dialTesting.isRunning()) {
            //正在进行拨测，请稍后。。。
            jsonResult.put("resultCode", "1");
            jsonResult.put("msg", "有拨测任务正在进行，请稍后...");
            jsonResult.put("taskIdList", taskMap.keySet());
            jsonResult.put("dialTestingInfo", currentDialTestingInfo);
            return jsonResult.toString();
        }

        dialTesting.running = true;
        dialTesting.stopAllTaskFLag = false;
        finishedTaskCnt = 0;
        taskMap.clear();
        taskResultMap.clear();
        int threadPoolNum = dialTestingInfo.getConcurrentNum();
        currentDialTestingInfo = dialTestingInfo;
        dialTesting.atomicTaskId.set(1);
        ExecutorService executorService;
        int i;
        if (currentDialTestingInfo.isProxyEnabled()) {
            //使用代理的
            threadPoolNum = currentDialTestingInfo.getProxyList().size() * currentDialTestingInfo.getConcurrentNum();
            executorService = Executors.newFixedThreadPool(threadPoolNum + 1);
            totalTaskCnt = threadPoolNum;
            for (String proxyIp : currentDialTestingInfo.getProxyList()) {
                HttpHost host = new HttpHost(proxyIp, 80);
                for(i = 1; i <= currentDialTestingInfo.getConcurrentNum(); ++i) {
                    int taskId = dialTesting.atomicTaskId.getAndIncrement();
                    //这个list只是用来便于各线程提交当前执行进度
                    List<Integer> finishedVisitNum =  new ArrayList<Integer>();
                    finishedVisitNum.add(0);//完成数
                    finishedVisitNum.add(0);//成功访问数，返回码200
                    taskMap.put(host.getHostName() + "-" + taskId, finishedVisitNum);
                    JSONObject json = new JSONObject();
                    taskResultMap.put(host.getHostName() + "-" + taskId, json);
                    executorService.execute(dialTesting.new VisitTask(finishedVisitNum, host.getHostName() + "-" + taskId + ".txt", host, json));
                }
            }
            executorService.shutdown();
        } else {
            //不使用代理的情况
            totalTaskCnt = threadPoolNum;
            executorService = Executors.newFixedThreadPool(threadPoolNum + 1);
            for(i = 1; i <= threadPoolNum; ++i) {
                int taskId = dialTesting.atomicTaskId.getAndIncrement();
                //这个list只是用来便于各线程提交当前执行进度
                List<Integer> finishedVisitNum =  new ArrayList<Integer>();
                finishedVisitNum.add(0);//完成数
                finishedVisitNum.add(0);//成功访问数，返回码200
                taskMap.put(taskId + "", finishedVisitNum);
                JSONObject json = new JSONObject();
                taskResultMap.put(taskId + "", json);
                executorService.execute(dialTesting.new VisitTask(finishedVisitNum, taskId + ".txt", (HttpHost)null, json));
            }
            executorService.shutdown();
        }

        //将所有线程信息返回
        jsonResult.put("taskIdList", taskMap.keySet());
        jsonResult.put("dialTestingInfo", currentDialTestingInfo);
        jsonResult.put("resultCode", "0");
        jsonResult.put("msg", "拨测任务已创建，请查看运行情况...");
        return jsonResult.toString();
    }

    public static void main(String[] args) {
//        String url = "http://dxcdntest.ctdns.net/";
//        int dialCnt = 100;
//        int interval = 1;
//        int concurrentNum = 10;
//        boolean proxyEnabled = false;
//        List<String> proxyList = new ArrayList<String>();
//        proxyList.add("58.221.5.17");
//        DialTestingInfo dialTestingInfo = new DialTestingInfo(url, dialCnt, interval, concurrentNum, 1, 1,proxyEnabled, proxyList);
//        String msg = dial(dialTestingInfo);
//        System.out.println(msg);
    }

    //访问任务
    class VisitTask implements Runnable {
        private String dayDir;
        String fileName;
        //这个list只是用来便于各线程提交当前执行进度
        List<Integer> finishedVisitNum;
        private int totalCnt = 0;
        private int count200Ok = 0;
        private int count403 = 0;
        private int count500 = 0;
        private long countOther = 0;
        private HttpHost host;
        private JSONObject resultJson;

        public VisitTask(List<Integer> finishedVisitNum, String fileName, HttpHost host, JSONObject resultJson) {
            this.finishedVisitNum = finishedVisitNum;
            this.fileName = fileName;
            this.host = host;
            this.resultJson = resultJson;
        }


        public void run() {
            this.dayDir = DateUtils.getNowTimeStr("yyyyMMddHHmmss");
            this.visit();

            //本次拨测任务执行完成
            updateFinishedTaskCnt();
        }

        /**
         * 访问
         */
        public void visit() {
            int pos1 = this.fileName.lastIndexOf("-");
            int pos2 = this.fileName.lastIndexOf(".txt");
            String currThreadIndex = this.fileName.substring(pos1 + 1, pos2);
            String begin = DateUtils.getNowTimeStr("yyyy-MM-dd HH:mm:ss.SSS");
            resultJson.put("currThreadIndex", currThreadIndex);
            resultJson.put("begin", begin);
            while(!stopAllTaskFLag && this.totalCnt < currentDialTestingInfo.getDialCount()) {
                JSONObject res = null;
                res = HttpclientUtil.get(currentDialTestingInfo.getUrl(), this.host,
                        currentDialTestingInfo.getConTimeout(), currentDialTestingInfo.getSoTimeout());
                finishedVisitNum.set(0, ++this.totalCnt);
                if (res.getString("statusCode").equals("200")) {
                    ++this.count200Ok;
                    finishedVisitNum.set(1, this.count200Ok);
                } else if (res.getString("statusCode").equals("403")) {
                    ++this.count403;
                } else if (res.getString("statusCode").equals("500")) {
                    ++this.count500;
                } else if (res.getString("statusCode").equals("999")) {
                    ++this.countOther;
                }

                try {
                    Thread.sleep(currentDialTestingInfo.getInterval());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            //统计结果
            String end = DateUtils.getNowTimeStr("yyyy-MM-dd HH:mm:ss.SSS");

            resultJson.put("end", end);
            resultJson.put("totalCnt", this.totalCnt);
            resultJson.put("count200Ok", this.count200Ok);
            resultJson.put("count403", this.count403);
            resultJson.put("count500", this.count500);
            resultJson.put("countOther", this.countOther);

//            StringBuffer buf = new StringBuffer();
//            if (null != this.host) {
//                buf.append("线程号：").append(currThreadIndex).append("\n代理Ip：" + this.host.getHostName());
//                resultJson.put("proxyIp", this.host.getHostName());
//            } else {
//                buf.append("\n线程号：").append(currThreadIndex);
//            }
//            buf.append("\n开始时间：");
//            buf.append(begin).append("\n结束时间：").append(end);
//            buf.append("\n统计：count:[").append(this.totalCnt).append("]").append(", 200 OK count:[").append(this.count200Ok)
//                    .append("], 403 count:[").append(this.count403).append("], 500 count:[")
//                    .append(this.count500).append("], other count:[").append(this.countOther).append("]");
//            System.out.println(buf.toString());
            //FileUtil.appentContent2File(STOR_DIR + this.dayDir + File.separator + this.fileName, buf.toString());

        }
    }
}
