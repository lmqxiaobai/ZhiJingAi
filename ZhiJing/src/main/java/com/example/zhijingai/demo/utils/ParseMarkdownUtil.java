package com.example.zhijingai.demo.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 对markdown格式进行转换
 */
public class ParseMarkdownUtil {
    // 定义一个表计数器（位置），让每一行添加进对应表中
    private int currentTableIndex = 0;
    public void parseMarkdown(XWPFDocument document, String text1){
        String[] lines = text1.split("\n");
        // 当为ture时就表示还在转换表
        boolean inTable = false;
        boolean center1 = false;

        // 将每一行文字进行文本检查和格式设置
        for (String line : lines) {
            // 创建段落对象paragraph，在该段落中创建一个run对象用于插入文本内容
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            // 检查文本是否有加粗，如果包含，则处理加粗
            if (line.contains("**")) {
                int startIndex = line.indexOf("**");
                int endIndex = line.lastIndexOf("**");

                // 确保startIndex在endIndex之前
                if (startIndex < endIndex) {
                    run.setBold(true);

                    // 截取并设置为加粗的部分
                    String boldText = line.substring(startIndex + 2, endIndex);
                    run.setText(boldText, 0);

                    // 截取剩余的文本
                    String remainingText = line.substring(0, startIndex) + line.substring(endIndex + 2);
                    if (!remainingText.isEmpty()) {
                        XWPFRun remainingRun = paragraph.createRun();
                        remainingRun.setText(remainingText);
                    }

                    // 继续下一行的处理
                    continue;
                }
            }

            // 检查标题
            if (line.startsWith("# ")) {
                run.setText(line.substring(2));
                // 设置字体大小
                run.setFontSize(18);
                run.setBold(true);
                // 设置标题居中
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                center1 = true;

                paragraph.setStyle("Heading1");
                continue; // 跳过为标题创建新段落
            } else if (line.startsWith("## ")) {
                run.setText(line.substring(3));
                run.setFontSize(16);
                run.setBold(true);
                if(center1 = false){
                    // 设置标题居中
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    center1 = true;
                }
                paragraph.setStyle("Heading2");
                continue; // 跳过为标题创建新段落
            } else if (line.startsWith("### ")) {
                run.setText(line.substring(4));
                run.setFontSize(14);
                run.setBold(true);
                paragraph.setStyle("Heading3");
                continue; // 跳过为标题创建新段落
            }else if (line.startsWith("#### ")) {
                run.setText(line.substring(5));
                run.setFontSize(12); // 设置适当的字体大小
                run.setBold(true);
                paragraph.setStyle("Heading4");
                continue; // 跳过为标题创建新段落
            }

            // 检查表格
            if (line.contains("|")) {
                if (!inTable) {
                    inTable = true;
                    processTableLine(document, line);
                    continue;
                } else {
                    processTableLine(document, line);
                    continue;
                }
            } else if (inTable && line.trim().equals("-----")) {
                // 继续处理当前表格
                continue;
            } else if (inTable && (line.trim().isEmpty() || line.startsWith("####"))) {
                // 在遇到非表格行时退出表格,并将表格计数器＋1，表示下一次扫描到的表算做表2.行内容都添加到表二里面
                currentTableIndex++;
                inTable = false;
            }

            run.setText(line);
        }
    }

    private enum TableRowType {
        HEADER, DATA, TOTAL
    }

    private void processTableLine(XWPFDocument document, String line) {
        // 假设表格中使用"|"作为分隔符
        String[] cells = line.split("\\|");

        // 删除数组开头和结尾的空单元格
        List<String> cellList = new ArrayList<>(Arrays.asList(cells));
        cellList.removeAll(Collections.singleton(""));

        // 根据内容确定表格行的类型
        TableRowType rowType = determineRowType(cellList);


        // 仅在表格不存在时创建新表格，表数量小于等于计数器时，就代表新表没有创建。
        if (document.getTables().size() <= currentTableIndex) {
            XWPFTable table = document.createTable(1, cellList.size());
            // 填充内容
            for (int i = 0; i < cellList.size(); i++) {
                table.getRow(0).getCell(i).setText(cellList.get(i).trim());
            }

        } else {
            // 如果表格已存在，添加新行，添加进对应表里面
            XWPFTable table = document.getTables().get(currentTableIndex);
            XWPFTableRow newRow = table.createRow();
            for (int i = 0; i < cellList.size(); i++) {
                newRow.getCell(i).setText(cellList.get(i).trim());
            }
            // 根据行类型应用特定的格式
            applyFormatting(newRow, rowType);
        }


    }

    private static TableRowType determineRowType(List<String> cells) {
        // 根据内容确定行的类型
        if (cells.stream().allMatch(String::isEmpty)) {
            return TableRowType.HEADER; // 所有单元格为空，是标题行
        } else if (cells.size() == 1 && cells.get(0).trim().equalsIgnoreCase("合计：")) {
            return TableRowType.TOTAL; // 只有一个单元格包含"合计："，是合计行
        } else {
            return TableRowType.DATA; // 否则是数据行
        }
    }

    private static void applyFormatting(XWPFTableRow row, TableRowType rowType) {
        // 根据行类型应用特定的格式
        if (rowType == TableRowType.HEADER) {
            // 如果需要，为标题行应用格式
            for (XWPFTableCell cell : row.getTableCells()) {
                cell.setColor("AAAAAA"); // 为标题单元格设置灰色背景色
            }
        } else if (rowType == TableRowType.TOTAL) {
            // 如果需要，为合计行应用格式
            for (XWPFTableCell cell : row.getTableCells()) {
                cell.setColor("FFFF00"); // 为合计单元格设置黄色背景色
            }
        }
    }
}
