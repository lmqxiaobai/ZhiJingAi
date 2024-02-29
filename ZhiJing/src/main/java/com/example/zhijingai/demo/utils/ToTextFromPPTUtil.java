package com.example.zhijingai.demo.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.example.zhijingai.demo.exception.ThemeNotFoundException;
import com.example.zhijingai.entitys.entity.*;
import com.example.zhijingai.entitys.vo.ThemeVO;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ToTextFromPPTUtil {

    private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    //@Value("${sky.alioss.access-key-id}")
    private static String accessKeyId = "LTAI5t8LkTDL6M2iUSFTvQjm";
    //@Value("${sky.alioss.access-key-secret}")
    private static String accessKeySecret = "gqHSOzg3i0zotPT46sAblKGJgXT7oc";
    //@Value("${sky.alioss.bucket-name}")
    private static String bucketName = "yilina-cangqiong";

    private static String[] lines;
    private static int linesLength;
    private static XMLSlideShow ppt;
    private static String[] subTitle = new String[]{"一、", "二、", "三、", "四、", "五、", "六、", "七、", "八、", "九、", "十、", "十一、", "十二、"};


    /**
     * 根据策划案标题和markdown汇报总结全自动生成ppt
     *
     * @param reportTitle
     * @param text2
     * @return
     * @throws IOException
     */
    public static XMLSlideShow parseToPPT(String reportTitle, String text2, ThemeVO themeVO) throws IOException {

        if(themeVO == null) {
            throw new ThemeNotFoundException("此ppt主题不存在或无法使用");
        }

        //创建一个ppt文档
        ppt = new XMLSlideShow();

        //分割字符串存入字符串数组里面
        lines = text2.split("\n");
        linesLength = lines.length;

        List<ThemePicture> themePictureList = themeVO.getThemePictureList();
        List<ThemeTextStyle> themeTextStyleList = themeVO.getThemeTextStyleList();
        List<ThemeTitleStyle> themeTitleStyleList = themeVO.getThemeTitleStyleList();
        List<ThemeEndStyle> themeEndStyleList = themeVO.getThemeEndStyleList();
        List<ThemeSubStyle> themeSubStyleList = themeVO.getThemeSubStyleList();

        if(themeTitleStyleList == null || themeTextStyleList == null || themePictureList == null || themeSubStyleList == null || themeEndStyleList == null){
            throw new ThemeNotFoundException("此ppt主题无法使用");
        }

        // ### 嵌入标题
        addTitle(reportTitle, ppt, themePictureList, themeTitleStyleList);

        // ### 添加目录至ppt第二页
        addSubtitle(ppt, themePictureList);
        //addSubtitle(ppt);

        // ### 提取文字内容
        addTextAndTextTitle(linesLength, lines, ppt, reportTitle, themePictureList, themeTextStyleList, themeSubStyleList);
        //addTextAndTextTitle(linesLength, lines, ppt, reportTitle);

        // ### 生成结尾
        addEnd(themeEndStyleList, themePictureList);
        //addEnd();

        return ppt;
    }


    /**
     * 添加正文和总标题（addTextAndTextTitle）的子方法
     * 由于本策划案的一些特定格式并不固定，
     * 所以我们需要根据markdown文档的总标题与内容标题的加粗特性去判断如下内容：
     * 在dfs方法中，我们需要根据比较##或者###来确定每个小分块的标题符号
     *
     * @param version1
     * @param version2
     */
    private static String versionCount(String version1, String version2, String[] lines) {
        int version1Num = 0;
        int version2Num = 0;

        for (String line : lines) {
            if (line.startsWith(version1)) {
                version1Num++;
            } else if (line.startsWith(version2)) {
                version2Num++;
            }
        }
        if (version1Num > 0 && version2Num > 0) {
            //两种符号都存在的情况下，谁数量大，则说明对应的符号是部分内容标题的加粗符号的数量
            return version1Num > version2Num ? version1 : version2;
        }
        //反之则有一种不存在，直接返回另一种
        return version1Num == 0 ? version2 : version1;
    }

    /**
     * 添加正文内容和内容标题
     *
     * @param linesLength
     * @param lines
     * @param ppt
     * @param reportTitle
     * @throws IOException
     */
    private static void addTextAndTextTitle(int linesLength, String[] lines, XMLSlideShow ppt, String reportTitle, List<ThemePicture> themePictureList, List<ThemeTextStyle> themeTextStyleList, List<ThemeSubStyle> themeSubStyleList) throws IOException {

        String version1 = "##", version2 = "###";
        //去确定内容标题的加粗符号，后续需要根据它去执行下一步程序
        String version = versionCount(version1, version2, lines);

        //将内容大标题取出
        List<String> subTitleList = subTitleCount(lines);

        int indexPPt = 2;
        //在正式添加总标题与内容
        textAndTitleOP:
        for (int i = 1; i < linesLength; i++) {

            /*因为格式不稳定的问题，所以我们必须防止带有标题或活动汇报等无关字眼或重复内容出现在ppt里*/
            if (lines[i - 1].startsWith("##") && lines[i - 1].contains("活动汇报")) continue;

            if (lines[i - 1].startsWith("##") && lines[i - 1].contains("标题")) continue;

            //在插入正文前需要寻找总标题部分是否有##加粗，有则跳过
            if (lines[i - 1].contains(reportTitle) && lines[i - 1].startsWith("##")) {
                //存在直接跳过
                continue;
            }

            //通过开头的#去寻找每一段内容的标题
            if (lines[i - 1] != null && lines[i - 1].startsWith(version) && lines[i - 1].contains(version)) {

                //此时遍历到了某一个点的标题，在lines数组中，我们需要一直遍历直到遇到下一个#为止，即能确认一个板块的内容
                int start = i - 1;
                //去掉加粗符号
                int index = lines[start].lastIndexOf(version);
                //从index开始截取直到最后，以便写入文本框内
                //此处需要判断version是##还是###
                String strTitle;
                if (version.equals("###")) {
                    strTitle = lines[start].substring(index + 3);
                } else {
                    strTitle = lines[start].substring(index + 2);
                }

                /*在创建幻灯片时，需要判断该内容下是否有小标题，如果存在，则需要将该内容的主标题设置在居中位置且放大字体，作为单独的一页*/
                //TODO: /*先单独设立一张幻灯片，用来存放标题*/待修改
                for (String str : subTitle) {
                    if(strTitle.contains(str)) {
                        XSLFSlide titleSlide = ppt.createSlide();
                        //添加背景图片
                        String pictureAddress = null;
                        for(ThemePicture picture : themePictureList) {
                            String category = picture.getPictureCategory();
                            if(category.contains("副标题")) {
                                pictureAddress = picture.getPictureAddress();
                            }
                        }
                        if(pictureAddress != null) {
                            InputStream inputStream = downloadFile(pictureAddress);
                            XSLFPictureData xslfPictureData = ppt.addPicture(inputStream, PictureData.PictureType.JPEG);
                            XSLFPictureShape picture = titleSlide.createPicture(xslfPictureData);
                            picture.setAnchor(new Rectangle(0, 0, 960, 540));
                        }
                        ThemeSubStyle subStyle = themeSubStyleList.get(0);
                        XSLFTextBox titleSlideTextBox = titleSlide.createTextBox();
                        titleSlideTextBox.setAnchor(new Rectangle(subStyle.getRecX(), subStyle.getRecY(), subStyle.getRecWidth(), subStyle.getRecHeight()));
                        XSLFTextParagraph titleParagraph = titleSlideTextBox.addNewTextParagraph();
                        XSLFTextRun titleRun = titleParagraph.addNewTextRun();
                        titleRun.setText(strTitle);
                        if(subStyle.getSubBold().equals("加粗")) {
                            titleRun.setBold(true);
                        }
                        titleRun.setFontSize(subStyle.getSubSize());
                        titleRun.setFontFamily(subStyle.getSubFamily());
                        titleRun.setFontColor(new Color(subStyle.getColorR(), subStyle.getColorG(), subStyle.getColorB()));
                        titleParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
                        indexPPt++;
                    }
                }

                /*添加完标题后还需要判断其下一个位置是否存在内容小副标题*/
                if(lines[i] != null && lines[i].startsWith("##") && lines[i].contains("##")) {
                    //存在时，防止二次添加大标题
                    continue;
                }

                //创建幻灯片
                XSLFSlide slide = ppt.createSlide();
                //这里需要根据indexPPt变量来确定当前该处于第几张ppt，需要给对应的ppt添加背景图片
                XSLFSlide slide1 = ppt.getSlides().get(indexPPt);
                indexPPt++;

                //创建好幻灯片后调用添加标题以及设置标题样式的方法
                addTextTitle(slide, slide1, strTitle, lines, i, themePictureList);

                //我们每次循环时，逐行向ppt添加内容(此行的下一行是文本)  此时内容标题已经在当前幻灯片中写好
                //创建文本框且设置样式
                ThemeTextStyle themeTextStyle = themeTextStyleList.get(0);
                XSLFTextBox textBox = slide.createTextBox();
                textBox.setAnchor(new Rectangle(themeTextStyle.getRecX(), themeTextStyle.getRecY(), themeTextStyle.getRecWidth(), themeTextStyle.getRecHeight()));

                //调用逐行添加文本的操作
                addText(start, version, textBox, themeTextStyle);

                /*此时添加文本的操作已经全部完成，需返回至parseToPPT方法进行汇报总结与感谢*/
            }
        }
    }

    /**
     * 逐行添加文本操作
     *
     * @param start
     * @param version
     * @param textBox
     */
    private static void addText(int start, String version, XSLFTextBox textBox, ThemeTextStyle themeTextStyle) {
        //逐行添加文本
        for (int j = start + 1; j < linesLength; j++) {
            if (lines[j] != null && !lines[j].startsWith(version)) {
                //创建新的文本段落和文本运行(文本框)
                XSLFTextParagraph textParagraph = textBox.addNewTextParagraph();
                XSLFTextRun textRun = textParagraph.addNewTextRun();
                //TODO: 设置内容，样式等，需要判断是否存在*号，有则需要加粗
                String strText = "";
                int leftIndex = -1, rightIndex = -1;

                if (lines[j] != null && lines[j].contains("**")) {
                    //去掉**号且设置加粗
                    leftIndex = lines[j].indexOf("**");
                    rightIndex = lines[j].lastIndexOf("**");
                    //截取序号
                    String strLeft = lines[j].substring(0, leftIndex);
                    //截取加粗小标题
                    String strBoldTitle = lines[j].substring(leftIndex + 2, rightIndex);
                    //截取文本内容
                    String strLastText = lines[j].substring(rightIndex + 2);
                    strText.concat(strLeft).concat(strBoldTitle).concat(strLastText);
                    //调用添加正文的方法
                    addOnlyText(textParagraph, strLeft, strBoldTitle, strLastText, themeTextStyle);
                } else {
                    //不存在则直接存
                    strText = lines[j];
                    String strNewText = strText;
                    if (strText.startsWith("-")) {
                        //存在降级符号则进行降级操作
                        textParagraph.setIndentLevel(1);
                        int index = strText.indexOf("-") + 1;
                        strNewText = strText.substring(index);
                    }
                    textRun.setText("        " + strNewText);
                    textRun.setFontSize(18.0);
                }
                //设置换行
                //textParagraph.addLineBreak();

            } else if (lines[j] != null && lines[j].startsWith(version)) {
                break;
            }
        }
    }

    /**
     * 添加内容标题+设置标题样式与背景
     *
     * @param slide
     * @param slide1
     * @param strTitle
     * @throws IOException
     */
    private static void addTextTitle(XSLFSlide slide, XSLFSlide slide1, String strTitle, String[] lines, int index, List<ThemePicture> themePictureList) throws IOException {

        //给标题幻灯片添加背景图片
        String pictureAddress = null;
        for(ThemePicture picture : themePictureList) {
            String category = picture.getPictureCategory();
            if(category.contains("文本内容")) {
                pictureAddress = picture.getPictureAddress();
            }
        }
        if(pictureAddress != null) {
            InputStream inputStream = downloadFile(pictureAddress);
            XSLFPictureData xslfPictureData = ppt.addPicture(inputStream, PictureData.PictureType.JPEG);
            XSLFPictureShape picture = slide1.createPicture(xslfPictureData);
            picture.setAnchor(new Rectangle(0, 0, 960, 540));
        }
        //添加幻灯片标题
        XSLFTextBox pptTitle = slide.createTextBox();
        //设置位置和大小
        /*如下代码请看addTextAndTextTitle方法，因为标题下面必有文字内容，所以不怕指针越界*/
//        if (index + 1 < lines.length && lines[index + 1] != null && lines[index + 1].startsWith("##")) {
//            pptTitle.setAnchor(new Rectangle(50, 200, 600, 80));
//        } else {
//            //TODO: 数据库内的位置标准
//            pptTitle.setAnchor(new Rectangle(50, 120, 600, 80));
//        }
        pptTitle.setAnchor(new Rectangle(50, 120, 600, 80));
        /*设置标题内容和样式*/
        //在标题框（pptTitle）中创建一个文本段落，便于操作文本内容样式等
        XSLFTextParagraph titleParagraph = pptTitle.addNewTextParagraph();
        //再创建一个新的文本运行，通过文本运行来操作文本内容样式等
        XSLFTextRun titleTextRun = titleParagraph.addNewTextRun();
        //添加文本，不添加加粗符号
        titleTextRun.setText(strTitle);
        //设置字体
//        if (index + 1 < lines.length && lines[index + 1] != null && lines[index + 1].startsWith("##")) {
//            titleTextRun.setFontSize(38.0);
//        } else {
//            titleTextRun.setFontSize(23.0);
//        }
        titleTextRun.setFontSize(23.0);
        //设置加粗
        titleTextRun.setBold(true);
        //设置标题居中
        titleParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
        //设置标题颜色
        titleTextRun.setFontColor(Color.GRAY);

    }

    /**
     * 添加ppt的总标题
     *
     * @param reportTitle
     * @param ppt
     */
    private static void addTitle(String reportTitle, XMLSlideShow ppt, List<ThemePicture> pictures, List<ThemeTitleStyle> titleStyles) throws IOException {
        // ### 嵌入标题
        /*拼接字符串操作*/
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("《").append(reportTitle).append("》\n").append("汇报总结");
        //将拼接好的标题转为String
        String title = stringBuilder.toString();
        //创建一张幻灯片用于存放标题
        XSLFSlide slide = ppt.createSlide();

        /*给标题幻灯片设置背景格式*/
        XSLFSlide slide1 = ppt.getSlides().get(0);

        //TODO: 添加背景元素，待优化
        //给标题幻灯片添加背景图片
        String pictureAddress = null;
        for(ThemePicture picture : pictures) {
            String category = picture.getPictureCategory();
            if(category != null && category.contains("主标题")) {
                pictureAddress = picture.getPictureAddress();
                break;
            }
        }
        //TODO: 2.4日记得查看，此段代码成功
        if(pictureAddress != null) {

            /*https://yilina-cangqiong.oss-cn-beijing.aliyuncs.com/ce7de4ab-e43e-42a4-ba06-14dac6ff5045.jpg*/
            //调用下载oss下的文件的方法
            InputStream inputStream = downloadFile(pictureAddress);
//            File image = new File(tempPath);
            XSLFPictureData firstSlide = ppt.addPicture(inputStream, PictureData.PictureType.JPEG);
            XSLFPictureShape picture = slide1.createPicture(firstSlide);
            picture.setAnchor(new Rectangle(0, 0, 960, 540));
        }
        //XSLFPictureData firstSlide = ppt.addPicture(new File("D:\\springEnviroment\\contest\\zhi-jing-ai2.0\\zhiJingAI\\src\\test\\static\\first\\titlePic.jpg"), PictureData.PictureType.JPEG);
        //添加vlog背景
        String vlogInitPath = ResourceUtils.getURL("classpath:static/AI/img/logo.png").getPath();
        //此时截取到的字符串开头为/，需要删除
        String vlogPath = vlogInitPath.substring(1);
        //替换为正确的路径格式
        String replaceVlogPath = vlogPath.replace("/", "\\");
        //给标题幻灯片添加vlog图片
        XSLFPictureData vlogSlide = ppt.addPicture(new File(replaceVlogPath), PictureData.PictureType.PNG);
        XSLFPictureShape vlog = slide1.createPicture(vlogSlide);
        int newPx = 80;
        vlog.setAnchor(new Rectangle2D.Double(0, slide1.getSlideShow().getPageSize().getHeight() - newPx, newPx + 20, newPx));
        /*添加背景元素结束*/

        //添加幻灯片标题
        XSLFTextBox pptTitle = slide.createTextBox();
        //每个ppt主题在每个参数表中只有一个参数
        ThemeTitleStyle titleStyle = titleStyles.get(0);
        //设置标题框位置和大小
        //pptTitle.setAnchor(new Rectangle(50, 175, 600, 100));
        pptTitle.setAnchor(new Rectangle(titleStyle.getRecX(), titleStyle.getRecY(), titleStyle.getRecWidth(), titleStyle.getRecHeight()));
        /*设置标题内容和样式*/
        //在标题框（pptTitle）中创建一个文本段落，便于操作文本内容样式等
        XSLFTextParagraph titleParagraph = pptTitle.addNewTextParagraph();
        //再创建一个新的文本运行，通过文本运行来操作文本内容样式等
        XSLFTextRun titleTextRun = titleParagraph.addNewTextRun();
        //将获取到的标题title插入到文本框中
        titleTextRun.setText(title);
        //设置大小
        //titleTextRun.setFontSize(32.0);
        titleTextRun.setFontSize(titleStyle.getTitleSize());
        //titleTextRun.setFontColor(Color.GRAY);
        //titleTextRun.setFontColor(new Color(58, 133, 179));
        titleTextRun.setFontColor(new Color(titleStyle.getColorR(), titleStyle.getColorG(), titleStyle.getColorB()));
        //设置加粗
        //titleTextRun.setBold(true);
        String titleBold = titleStyle.getTitleBold();
        if(titleBold.equals("加粗")) {
            titleTextRun.setBold(true);
        }
        //设置标题居中
        titleParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
        /*到此标题整理完成*/
    }

    /**
     * 添加加粗好后的正文
     * (addText)的子方法
     *
     * @param textParagraph
     * @param strLeft
     * @param strBoldTitle
     * @param strLastText
     */
    private static void addOnlyText(XSLFTextParagraph textParagraph, String strLeft, String strBoldTitle, String strLastText, ThemeTextStyle themeTextStyle) {
        //TODO: 分三个部分分别添加进去即可实现部分加粗(待优化)
        String strNumber = strLeft;
        if (strLeft != null && strLeft.startsWith("-")) {
            //存在降级符号则进行降级操作
            textParagraph.setIndentLevel(1);
            int index = strLeft.indexOf("-") + 1;
            strNumber = strLeft.substring(index);
        }
        XSLFTextRun run1 = textParagraph.addNewTextRun();
        run1.setText("        " + strNumber);
        run1.setFontSize(themeTextStyle.getTextSize());
        if(!themeTextStyle.getTextBold().equals("不加粗")) {
            run1.setBold(true);
        }
        XSLFTextRun run2 = textParagraph.addNewTextRun();
        run2.setText(strBoldTitle);
        //加粗部分必须加粗
        run2.setBold(true);
        run2.setFontSize(themeTextStyle.getTextSize());
        XSLFTextRun run3 = textParagraph.addNewTextRun();
        run3.setText(strLastText);
        run3.setFontSize(themeTextStyle.getTextSize() - 1);
        if(!themeTextStyle.getTextBold().equals("不加粗")) {
            run3.setBold(true);
        }
    }

    /**
     * 添加结束幻灯片
     *
     * @throws IOException
     */
    private static void addEnd(List<ThemeEndStyle> themeEndStyleList, List<ThemePicture> themePictureList) throws IOException {
        //结尾部分
        //新创建一张幻灯片
        XSLFSlide slideForEndAndThanks = ppt.createSlide();
        /*给结束幻灯片设置背景格式*/
        XSLFSlide slide1End = ppt.getSlides().get(ppt.getSlides().size() - 1);

        //给结束幻灯片添加背景图片
        String pictureAddress = null;
        for(ThemePicture themePicture : themePictureList) {
            String category = themePicture.getPictureCategory();
            //TODO: contains内参数待修改
            if(category.contains("结尾")) {
                pictureAddress = themePicture.getPictureAddress();
            }
        }
        if(pictureAddress != null) {
            //调用下载oss文件方法
            InputStream inputStream = downloadFile(pictureAddress);
            //只要pic非空，那么一定能够得到oss文件的输入流，所以，不用加if语句判断
            XSLFPictureData firstSlideEnd = ppt.addPicture(inputStream, PictureData.PictureType.JPEG);
            XSLFPictureShape pictureEnd = slide1End.createPicture(firstSlideEnd);
            //设置背景图片为全屏模式
            pictureEnd.setAnchor(new Rectangle(0, 0, 960, 540));
        }
        //创建感谢的文本框
        XSLFTextBox textBoxEnd = slideForEndAndThanks.createTextBox();
        //调整文本框的位置
        ThemeEndStyle themeEndStyle = themeEndStyleList.get(0);
        textBoxEnd.setAnchor(new Rectangle(themeEndStyle.getRecX(), themeEndStyle.getRecY(), themeEndStyle.getRecWidth(), themeEndStyle.getRecHeight()));

        //创建汇报人的文本框
        XSLFTextBox textBoxPerson = slideForEndAndThanks.createTextBox();
        textBoxPerson.setAnchor(new Rectangle(50, 400, 600, 200));


        //TODO: 待修改+优化
        /**
         * 添加结束部分
         */
        addEndTextAndStyle(textBoxEnd, "汇报总结到此为止，谢谢各位！", themeEndStyle.getEndSize(), new Color(themeEndStyle.getColorR(), themeEndStyle.getColorG(), themeEndStyle.getColorB()), TextParagraph.TextAlign.CENTER);
        //这个不变
        addEndTextAndStyle(textBoxPerson, "汇报人：xxx", 26.0, new Color(0, 0, 0), TextParagraph.TextAlign.RIGHT);
    }

    /**
     * 添加结束部分
     *
     * @param text
     * @param size
     * @param color
     * @param textAlign
     */
    private static void addEndTextAndStyle(XSLFTextBox textBoxEnd, String text, double size, Color color, TextParagraph.TextAlign textAlign) throws IOException {

        //添加新的文本段落与文本运行
        XSLFTextParagraph textParagraphEnd = textBoxEnd.addNewTextParagraph();
        XSLFTextRun textRunEnd = textParagraphEnd.addNewTextRun();
        //添加结束语句：汇报总结到此为止，谢谢各位！
        textRunEnd.setText(text);
        //设置字体等样式
        textRunEnd.setBold(true);
        textRunEnd.setFontSize(size);
        textRunEnd.setFontColor(color);
        textParagraphEnd.setTextAlign(textAlign);

        //设置换行
        textParagraphEnd.addLineBreak();
    }

    /**
     * 添加目录幻灯片
     * @param ppt
     */
    private static void addSubtitle(XMLSlideShow ppt, List<ThemePicture> themePictureList) throws IOException {

        //添加新的副标题汇总幻灯片
        XSLFSlide subTitle = ppt.createSlide();

        /*添加背景图片*/
        XSLFSlide subSlide = ppt.getSlides().get(1);
        //拿取标题目录的背景图片
        String pictureAddress = null;
        for(ThemePicture picture : themePictureList) {
            String category = picture.getPictureCategory();
            if(category.contains("目录")) {
                pictureAddress = picture.getPictureAddress();
            }
        }
        //非空即为存在该背景图片，调用下载方法
        if(pictureAddress != null) {
            InputStream inputStream = downloadFile(pictureAddress);
            //下载完成，添加背景图片
            XSLFPictureData xslfPictureData = ppt.addPicture(inputStream, PictureData.PictureType.JPEG);
            XSLFPictureShape picture = subSlide.createPicture(xslfPictureData);
            picture.setAnchor(new Rectangle(0, 0, 960, 540));
        }

        //添加目录框
        //TODO: 2.20 需要优化
        XSLFTextBox contentBox = subTitle.createTextBox();
        contentBox.setAnchor(new Rectangle(250, 50, 600, 120));
        //段落运行
        XSLFTextParagraph contentParagraph = contentBox.addNewTextParagraph();
        XSLFTextRun contentRun = contentParagraph.addNewTextRun();
        contentRun.setText("目录");
        contentRun.setBold(true);
        contentRun.setFontSize(40.0);
        contentRun.setFontFamily("微软雅黑");
        //添加副标题汇总文本框
        XSLFTextBox subTitleBox = subTitle.createTextBox();
        subTitleBox.setAnchor(new Rectangle(200, 200, 600, 300));
        //调用统计标题个数方法，并传入集合中
        List<String> subTitleList = subTitleCount(lines);
        //TODO: 将所有标题添加至文本框内，待修改
        subTitleList.forEach(list -> {
            if(list.contains("##")) {
                int index = list.indexOf("#");
                String strSubTitle = list.substring(index + 3);
                XSLFTextParagraph subTitleParagraph = subTitleBox.addNewTextParagraph();
                XSLFTextRun subTitleRun = subTitleParagraph.addNewTextRun();
                subTitleRun.setText(strSubTitle);
                subTitleRun.setFontSize(18.0);
                subTitleRun.setBold(true);
                subTitleRun.setFontFamily("宋体");
            }
        });
    }

    /**
     * 统计内容标题的个数与收集
     * @param lines
     * @return
     */
    private static List<String> subTitleCount(String[] lines) {

        List<String> subTitleList = new ArrayList<>();

        int number = 0;
        subTitle: for (int i = 0; i < subTitle.length; i++) {
            //遍历lines数组
            subLines: for(int j = number + 1; j < linesLength; j ++) {

                if(lines[j] == null) continue;

                if(lines[j] != null && lines[j].contains(subTitle[i])) {
                    number = j;
                    subTitleList.add(lines[j]);
                    break subLines;
                }

                if(j == linesLength - 1) {
                    break subTitle;
                }
            }
        }
        return subTitleList;
    }


    /**
     * 随机生成ppt文件名
     * @param length
     * @return
     */
    public static String RandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        sb.append(".pptx");
        return sb.toString();
    }

    /**
     * 从oss下载文件至后台
     * @param pictureAddress
     * @return 以输入流的形式返回
     */
    public static InputStream downloadFile(String pictureAddress) {
        //创建一个oss的客户端，方便申请访问对应的bucket
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //调用随机生成文件名方法
        //String tempPath = RandomString(7);
        //截取文件名称（注：oss文件默认名称为其url路径的最后一个反斜杠开始到最后文件格式结束的部分）
        int index = pictureAddress.lastIndexOf("/") + 1;
        String strPath = pictureAddress.substring(index);
        //获取到oss文件，以object类接收
        OSSObject object = ossClient.getObject(bucketName, strPath);
        //下载oss文件并通过字节流返回
        InputStream inputStream = object.getObjectContent();
        return inputStream;
    }
}
