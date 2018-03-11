package com.yf.dialtesting.util.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yf.dialtesting.util.Constants;
import com.yf.dialtesting.util.common.DateUtils;
import com.yf.dialtesting.util.common.FileUtil;
import com.yf.dialtesting.util.common.StringUtils;
import com.yf.dialtesting.util.ip.ValidateUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpHost;

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

    //任务执行信息 key：线程号， value：每次访问记录的详情
    private static Map<String, List<String>> taskMap = new HashMap<String, List<String>>();

    //日志文件输出路径
    private final String STOR_DIR = Constants.STOR_DIR;

    private static String dayDir;
    private static long totalCnt = 0L;
    protected static long count200Ok = 0L;
    protected static long count403 = 0L;
    protected static long countOther = 0L;
    private int dialCount;
    private long interval;
    private String URL;
    private String[] PROXY_LIST = new String[0];

    private synchronized boolean isRunning() {
        return running;
    }

    public synchronized void updateFindshedTaskCnt() {
        finishedTaskCnt++;
        if (finishedTaskCnt == totalTaskCnt) {
            //所有任务都已运行完成
            running = false;
        }
    }


    public static String loadTaskProgress() {
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("taskMap", taskMap);
        return jsonResult.toString();
    }

    public static String dial(String url, int dialCount, int interval,
    int concurrentNum, boolean proxyEnabled, String proxyList) {

        JSONObject jsonResult = new JSONObject();
        if (!ValidateUtil.validateUrl(url)) {
            jsonResult.put("resultCode", "-1");
            jsonResult.put("msg", "URL格式不正确，请检查！！");
            return jsonResult.toString();
        }

        if (proxyEnabled && !ValidateUtil.validateIpList(proxyList, ",")) {
            jsonResult.put("resultCode", "-1");
            jsonResult.put("msg", "代理IP格式不正确，请检查！！");
            return jsonResult.toString();
        }

        DialTesting dialTesting = DialTesting.getInstance();
        if (dialTesting.isRunning()) {
            //正在进行拨测，请稍后。。。
            jsonResult.put("resultCode", "1");
            jsonResult.put("msg", "有拨测任务正在进行，请稍后。。。");
            jsonResult.put("taskIdList", taskMap.keySet());
            return jsonResult.toString();
        }

        dialTesting.running = true;
        finishedTaskCnt = 0;
        taskMap.clear();

        dialTesting.URL = url;
        dialTesting.PROXY_LIST = proxyList.split(",");
        int threadPoolNum = concurrentNum;

        dialTesting.interval = interval;
        dialTesting.dialCount = dialCount;
        ExecutorService executorService;
        int i;
        if (proxyEnabled) {
            //使用代理的
            threadPoolNum = dialTesting.PROXY_LIST.length * concurrentNum;
            executorService = Executors.newFixedThreadPool(threadPoolNum + 1);
            totalTaskCnt = threadPoolNum;
            for (String proxyIp : dialTesting.PROXY_LIST) {
                HttpHost host = new HttpHost(proxyIp, 80);
                for(i = 1; i <= threadPoolNum; ++i) {
                    //这个list只是用来便于各线程提交当前执行进度
                    List<String> finishedVisitNum =  new ArrayList<String>();
                    taskMap.put(host.getHostName() + "-" + dialCount + "-" + i, finishedVisitNum);
                    executorService.execute(dialTesting.new VisitTask(finishedVisitNum, host.getHostName() + "-" + i + ".txt", host));
                }
            }
            executorService.shutdown();
        } else {
            //不使用代理的情况
            totalTaskCnt = threadPoolNum;
            executorService = Executors.newFixedThreadPool(threadPoolNum + 1);
            for(i = 1; i <= threadPoolNum; ++i) {
                //这个list只是用来便于各线程提交当前执行进度
                List<String> finishedVisitNum =  new ArrayList<String>();
                taskMap.put(dialCount + "-" + i, finishedVisitNum);
                executorService.execute(dialTesting.new VisitTask(finishedVisitNum, i + ".txt", (HttpHost)null));
            }
            executorService.shutdown();
        }

        //将所有线程信息返回
        jsonResult.put("taskIdList", taskMap.keySet());
        jsonResult.put("resultCode", "0");
        jsonResult.put("msg", "拨测任务已创建，请查看运行情况。。");
        return jsonResult.toString();
    }

    public static void main(String[] args) {
//        String url = "http://dxcdntest.ctdns.net/";
        String url = "http://cdntest.ctdns.net/";
        int dialCnt = 1;
        int interval = 1;
        int concurrentNum = 1;
        boolean proxyEnabled = false;
        String proxList = "58.221.5.17";
        String msg = dial(url, dialCnt, interval, concurrentNum, proxyEnabled, proxList);
        System.out.println(msg);

    }

    //访问任务
    class VisitTask implements Runnable {
        private String dayDir;
        String fileName;
        //这个list只是用来便于各线程提交当前执行进度
        List<String> finishedVisitNum;
        private long totalCnt = 0L;
        private long count200Ok = 0L;
        private long count403 = 0L;
        private long count500 = 0L;
        private long countOther = 0L;
        private HttpHost host;

        public VisitTask(List<String> finishedVisitNum, String fileName, HttpHost host) {
            this.finishedVisitNum = finishedVisitNum;
            this.fileName = fileName;
            this.host = host;
        }

        public void visit() {
            int pos1 = this.fileName.lastIndexOf("-");
            int pos2 = this.fileName.lastIndexOf(".txt");
            String currThreadIndex = this.fileName.substring(pos1 + 1, pos2);
            int cnt = 0;
            String begin = DateUtils.getNowTimeStr("yyyy-MM-dd HH:mm:ss.SSS");
            while(cnt < dialCount) {
                JSONObject res = null;
                ++cnt;
                ++this.totalCnt;
                res = HttpclientUtil.get(URL, this.host, 2000, 3000);
                if (res.getString("statusCode").equals("200")) {
                    ++this.count200Ok;
                } else if (res.getString("statusCode").equals("403")) {
                    ++this.count403;
                } else if (res.getString("statusCode").equals("500")) {
                    ++this.count500;
                } else if (res.getString("statusCode").equals("999")) {
                    ++this.countOther;
                }

                String content = "第" + cnt + "次：" + res.getString("begin") + "\t" + res.getString("end") + "\t" + URL + "\t" + res.getString("cost") + "\t" + res.getString("statusCode");
//                String content = "线程" + currThreadIndex + "（第" + cnt + "次访问）-->\t" + res.getString("begin") + "\t" + res.getString("end") + "\t" + URL + "\t" + res.getString("cost") + "\t" + res.getString("statusCode");
                if (null != this.host) {
                    content = content + "\tproxy:" + this.host.getHostName();
                }

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException var11) {
                    var11.printStackTrace();
                }

                finishedVisitNum.add(content);
                System.out.println(content);

            }

            StringBuffer buf = new StringBuffer();
            if (null != this.host) {
                buf.append("线程号：").append(currThreadIndex).append("\n代理Ip：" + this.host.getHostName());
            } else {
                buf.append("\n线程号：").append(currThreadIndex);
            }

            buf.append("\n开始时间：");
            buf.append(begin).append("\n结束时间：").append(DateUtils.getNowTimeStr("yyyy-MM-dd HH:mm:ss.SSS"));
            buf.append("\n统计：count:[").append(this.totalCnt).append("]").append(", 200 OK count:[").append(this.count200Ok)
                    .append("], 403 count:[").append(this.count403).append("], 500 count:[")
                    .append(this.count500).append("], other count:[").append(this.countOther).append("]");
            FileUtil.appentContent2File(STOR_DIR + this.dayDir + File.separator + this.fileName, buf.toString());
        }

        public void run() {
            this.dayDir = DateUtils.getNowTimeStr("yyyyMMddHHmmss");
            this.visit();

            //本次拨测任务执行完成
            updateFindshedTaskCnt();
        }
    }
}
