package com.example.zhijingai.demo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Input;
import entity.JsonRequest;
import entity.Parameters;
import gpt.Constants;
import okhttp3.*;
import org.springframework.stereotype.Controller;

import java.io.IOException;

/**
 * 图片处理工具类，将用户上传图片传递给ai服务器，通过处理返回特定路径
 */
@Controller
public class ImageRequestUtil {
    public String imageRequest(String description,String imagesUrl){
        // 你的API密钥
        String apiKey = Constants.apiKey;

        // 返回图片路径
        String imageUrl = "";
        // API端点
        String endpoint = "https://dashscope.aliyuncs.com/api/v1/services/aigc/background-generation/generation/";

        // 构建请求体
        JsonRequest jsonRequest = new JsonRequest();
        jsonRequest.setModel("wanx-background-generation-v2");


        Input input = new Input();
        // 设置透明背景的主题头像图片url,仅支持png格式且为RGB格式的图片（可以选择透明稍微有一点RGB不明显的背景，然后将用户上传的图片作为引导图上进行生成）
        input.setBase_image_url(imagesUrl);
        // 设置引导图URL,就是用户上传的图片路径
        input.setRef_image_url("https://e81c173655.yicp.fun/AI/img/1.jpeg");
        // 设置引导文本提示词
        input.setRef_prompt(description);
        // 设置负向提示祠，不希望出现的内容
        //input.setNeg_ref_prompt("");
        Parameters parameters = new Parameters();
        // 设置与引导图的相似度，数值越大，相似度越低，0~999
        parameters.setNoise_level("450");

        jsonRequest.setInput(input);
        jsonRequest.setParameters(parameters);

        // 将实体类转换为json格式
        Gson gson = new Gson();
        String jsonBody = gson.toJson(jsonRequest);

        // 使用OkHttpClient发起http请求
        OkHttpClient client = new OkHttpClient();

        // 创建POST请求对象
        Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", apiKey)
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        try {
            // 执行请求，将请求对象向http发生请求
            Response response = client.newCall(request).execute();
            System.out.println("上传图片响应:"+response);
            if (response.isSuccessful()) {
                // 获取响应
                String responseBody = response.body().string();
                // 解析响应里面的json数据
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();

                // 在json数据里面去提取任务ID
                String taskId = jsonResponse.getAsJsonObject("output").get("task_id").getAsString();
                System.out.println("任务已创建，任务ID: " + taskId);

                // 等待任务完成并返回修改后的图片路径
                imageUrl = waitLoadResults(apiKey, taskId);
                System.out.println("最终:"+imageUrl);
            } else {
                // 处理请求失败的情况
                imageUrl = response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    public static String waitLoadResults(String apiKey, String taskId) {
        String imageUrl ="";
        while (true) {
            try {
                // 等待5秒后查询任务状态
                Thread.sleep(5000);
                String taskStatus = checkTaskStatus(apiKey, taskId);
                if (taskStatus.equals("SUCCEEDED")) {
                    // 任务已完成，可以加载修改后的图片
                    imageUrl = ResultImages(apiKey, taskId);
                    break;
                } else if (taskStatus.equals("FAILED")) {
                    System.out.println("任务失败，请检查任务状态。");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return imageUrl;
    }

    public static String checkTaskStatus(String apiKey, String taskId) {
        OkHttpClient client = new OkHttpClient();

        // 创建GET请求来查询任务状态
        String taskStatusUrl = "https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId;

        Request request = new Request.Builder()
                .url(taskStatusUrl)
                .header("Authorization", apiKey)
                .get()
                .build();

        try {
            // 执行请求
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // 解析响应JSON
                String responseBody = response.body().string();
                System.out.println("执行到checkTaskStatus:"+responseBody);
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();
                // 提取任务状态
                String taskStatus = jsonResponse.getAsJsonObject("output").get("task_status").getAsString();
                System.out.println("任务状态: " + taskStatus);
                return taskStatus;
            } else {
                // 处理请求失败的情况
                System.out.println("查询任务状态失败：" + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static String ResultImages(String apiKey, String taskId) {
        String imageUrl = "";
        OkHttpClient client1 = new OkHttpClient();

        // 创建GET请求来获取结果
        String taskStatusUrl = "https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId;

        Request request = new Request.Builder()
                .url(taskStatusUrl)
                .header("Authorization", apiKey)
                .get()
                .build();

        try {
            // 执行请求
            Response response = client1.newCall(request).execute();
            if (response.isSuccessful()) {

                // 解析响应JSON
                String responseBody = response.body().string();
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();
                // 提取任务状态
                String taskStatus = jsonResponse.getAsJsonObject("output").get("task_status").getAsString();
                if ("SUCCEEDED".equals(taskStatus)){
                    // 因为json格式下results里面的url为数组，所以要用jsonArray
                    JsonArray jsonArray = jsonResponse.getAsJsonObject("output").get("results").getAsJsonArray();
                    for (int i = 0; i<jsonArray.size();i++){
                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                        // 获取图像 URL
                        imageUrl = jsonObject.get("url").getAsString();
                    }
                }else {
                    System.out.println(jsonResponse);
                }
                return imageUrl;
            } else {
                // 处理请求失败的情况
                System.out.println("查询任务状态失败：" + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "出现错误~";
    }
}
