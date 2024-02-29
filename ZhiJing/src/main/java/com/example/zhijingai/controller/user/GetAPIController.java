package com.example.zhijingai.controller.user;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.example.zhijingai.demo.constant.IpAddressConstant;
import com.example.zhijingai.demo.utils.ImageRequestUtil;
import com.example.zhijingai.demo.utils.MusicReptilianUtil;
import com.example.zhijingai.demo.utils.ParseMarkdownUtil;
import com.example.zhijingai.demo.utils.ToTextFromPPTUtil;
import com.example.zhijingai.entitys.entity.HistoryMessageManager;
import com.example.zhijingai.entitys.entity.User;
import com.example.zhijingai.entitys.vo.ThemeVO;
import com.example.zhijingai.result.Result;
import com.example.zhijingai.service.GetAPIService;
import com.example.zhijingai.service.ThemeService;
import gpt.Constants;
import gpt.QuestAI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@Api(tags = "AI交互问答")
/**
 * @CrossOrigin注解是Spring框架中用于处理跨域请求的一个重要注解，主要用于解决浏览器的同源策略限制问题。
 * 在前后端分离开发中，前端应用（如React、Vue等）和后端API服务不在同一域名下时，就需要处理跨域问题。
 * IpAddressConstant.IPADDRESS_OPEN里面就是封装的允许访问的IP地址
 */
@CrossOrigin(origins = IpAddressConstant.IPADDRESS_OPEN)
public class GetAPIController {

    private QuestAI ai = new QuestAI();
    private String text1;
    private String text2;
    private final String questionForReport = "根据上述策划方案内容生成对应的活动汇报";
    private boolean status = false;
    @Autowired
    private ThemeService themeService;
    @Autowired
    private ImageRequestUtil imageRequestUtil;
    @Autowired
    private GetAPIService getAPIService;
    private static Generation gen;
    private static MessageManager msgManager;
    public GetAPIController() {
        // 初始化MessageManager和Generation对象，只需初始化一次
        msgManager = new MessageManager(10000);
        gen = new Generation();
        // 添加设定好系统指令
        msgManager.add(Message.builder().role(Role.SYSTEM.getValue())
                .content(Constants.INSTRUCT_TEXT)
                .build());
    }
    /**
     *
     * @param question
     * @return 调用AI问答进行返回
     * @throws NoApiKeyException
     * @throws InputRequiredException
     * @throws InterruptedException
     */
    /*
    @ApiOperation("策划案ai生成接口")
    @PostMapping("/questions")
    public Result<String> setAi(@RequestParam String question) throws NoApiKeyException, InputRequiredException, IOException {
        text1 = ai.callWithMessage1(question);
        return Result.success(text1);
    }
     */


    @ApiOperation("策划案ai生成接口")
    @GetMapping(value = "/questions",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void setAi(@RequestParam String question,HttpServletResponse response) throws NoApiKeyException, InputRequiredException, IOException {
        /**
         * 多轮会话解决，明天解决消息记录问题
         */
        getAPIService.setAi(question,response);
        text1 = getAPIService.getAiText();

    }

    /**
     * Ai问答历史信息记录添加
     */
    @ApiOperation("历史信息记录添加")
    @GetMapping("/addRecord")
    public Result addRecord (HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        long userId = user.getId();

        // 截取聊天记录
        List<Message> messageManager = getAPIService.getMsgManager().get().subList(1,getAPIService.getMsgManager().get().size());
        String messageManagerJson = JSON.toJSONString(messageManager);
        if(getAPIService.addHistory(messageManagerJson,userId)){
            return Result.success();
        }else {
            return Result.error("添加失败");
        }
    }

    /**
     * 信息记录查询
     */
    @ApiOperation("历史信息记录查询")
    @GetMapping  ("/listRecord")
    public Result<List<HistoryMessageManager>> listRecord(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        long userId = user.getId();
        List<HistoryMessageManager> list = getAPIService.list(userId);
        return Result.success(list);
    }

    @ApiOperation("word 文档生成")
    @GetMapping("/pageWord")
    public ResponseEntity<byte[]> pageWord() throws IOException {
        // 创建Word文档
        XWPFDocument document = new XWPFDocument();
        // 实例化文档内容格式处理工具类
        ParseMarkdownUtil parse = new ParseMarkdownUtil();
        // text.toString()就是ai回答的最终内容
        parse.parseMarkdown(document,getAPIService.getAiText());
        // 将文档转换为字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        // 设置响应头，包括Content-Disposition用于指示下载和文件名
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\"zhiJing.docx\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // 保存文档new FileOutputStream("C:/Users/LMQ/Desktop/out.docx")
        // document.write(new FileOutputStream("C:/Users/LMQ/Desktop/out.docx"));
        // 返回带有正确头部的字节流
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    /**
     * 图片处理
     */
    @ApiOperation("图片处理接口")
    @PostMapping("/image")
    public Result<String> image( @RequestPart("img") MultipartFile img, @RequestParam("description") String description) throws IOException {

        // 获取项目static的地址
        String staticPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        // 获取图片文件名
        String imageName = img.getOriginalFilename();

        // 图片存储目录及图片名称
        String url_path="img/" + imageName;
        // 图片保存路径
        String savePath = staticPath +"/AI"+ File.separator + url_path;

        System.out.println("文件保存路径"+savePath);
        // 访问路径 = 静态资源路径+文件目录路径
        String visitPath = "AI/" + url_path;
        System.out.println("图片访问url：" + visitPath);

        File saveFile = new File(savePath);
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
        try {
            // 将临时存储的文件移动到真实存储路径下
            img.transferTo(saveFile);
        }catch (IOException e){
            e.printStackTrace();
        }

        // 将前端传来的图片保存到特定目录下，然后imagesUrl就代表图片保存后的路径
        String imageUrl = imageRequestUtil.imageRequest(description,"https://e81c173655.yicp.fun/"+visitPath);


        /*
        // 固定数据
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String imageUrl ="https://e81c173655.yicp.fun/AI/img/10.jpg";

         */
        return Result.success(imageUrl);
    }

    @PostMapping("/report")
    @ApiOperation("根据策划案生成对应汇报总结")
    public Result<String> setReportAi() throws NoApiKeyException, InputRequiredException, IOException {

        if(text1 != null) {
            MessageManager msgManager = ai.getMsgManager();
            msgManager.add(Message.builder().role(Role.USER.getValue()).content(text1).build());
            msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(text1).build());
            text2 = ai.callWithMessage1(questionForReport);
            return Result.success(text2);
        }
        return Result.error("策划案未生成，请先生成策划案！");
    }

    /**
     * 生成ppt汇报
     * @return
     * @throws IOException
     */
    @ApiOperation("生成ppt汇报")
    @GetMapping("/pagePPT")
    public Result<Object> pagePPT(Integer pptId) throws IOException {

        //获取标题
        if(text1 == null) {
            return Result.error("策划案未生成，请先生成策划案！");
        }
        String[] lines = text1.split("\n");
        String reportTitle = null;
        for(int i = 1; i < lines.length; i ++) {
            if(lines[i] == null) continue;
            if(lines[i - 1].contains("活动主题")) {
                reportTitle = lines[i];
                break;
            }
        }
        ThemeVO themeVO = themeService.selectById(pptId);
        //此时获取到了标题，只需传递给生成ppt的类即可
        XMLSlideShow reportPPT = ToTextFromPPTUtil.parseToPPT(reportTitle, text2, themeVO);

        //随机生成的ppt名字
        String pptName = ToTextFromPPTUtil.RandomString(10);

        reportPPT.write(Files.newOutputStream(Paths.get("D:\\" + pptName)));

        return Result.success("成功保存：汇报ppt默认名称为：" + pptName);
    }

    /**
     * 获取背景音乐试听路径
     * @return
     */
    @ApiOperation("获取背景音乐试听路径")
    @GetMapping("/music")
    public Result accessMusicUrl(String name) throws IOException {
        log.info("根据歌曲名获取歌曲试听路径: {}", name);
        String musicURL = MusicReptilianUtil.wangYi(name);
        return Result.success(musicURL);
    }
}
