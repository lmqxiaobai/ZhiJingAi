package com.example.zhijingai.demo.utils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 验证码过期计时工具类，将用户传递过来的验证码进行比较及是否超时
 */
public class CodeUtil {
    public static HashMap<String,String> codeMessage = new HashMap<>();

    public void timerCache(String emil, String code){
        codeMessage.put(emil,code);
        Timer timer = new Timer();
        // 安排计时器任务，当他超过60000毫秒后执行TimerDeleteTask任务
        timer.schedule(new TimerDeleteTask(emil,timer),60000);
    }
    // TimerDeleteTask内部类继承TimerTask，当计时器到达时执行该任务，run（）
    class TimerDeleteTask extends TimerTask {
        private String emil;
        private Timer timer;
        public TimerDeleteTask(String emil, Timer timer) {
            this.emil = emil;
            this.timer = timer;
        }
        @Override
        public void run() {
            codeMessage.remove(emil);
            timer.cancel();
            if (null != timer) {
                timer = null;
            }
        }
    }
}
