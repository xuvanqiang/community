package com.honghuang.community.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词汇过滤器(前缀树)
 */
@Component
public class SensitiveFilter{

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TireNode rootNode = new TireNode();

    /**
     * 创建初始化方法,敏感词只需读取一次.设置在spring容器创建时就执行该初始化方法比较合理
     */
    @PostConstruct
    public void init(){
        /*
        对敏感词文件输入流转换成字符输入流进行处理
         */
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加敏感词到前缀树种
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败:" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TireNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TireNode subNode = tempNode.getSubNode(c);

            //subNode不存在则创建并初始化subNode
            if (subNode == null) {
                subNode = new TireNode();
                tempNode.addSubNode(c,subNode);
            }

            //指向子节点,进入下一轮循环
            tempNode = subNode;

            //遍历到keyword的最后一个字符,设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     *过滤敏感词
     * @param text 未过滤文本内容
     * @return  过滤后应返回的文本内容
     */
    public String filter(String text){
        if (text == null) {
            return null;
        }

        //指针1(前缀树指针)
        TireNode tempNode = rootNode;
        //指针2(扫描文本字段开始指针)
        int begin = 0;
        //指针3(扫描文本字段结束指针)
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置(begin-->begin+1,position-->begin)
                position = ++begin;
                //指针1重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isKeywordEnd()){
                //发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //指针1重新指向根节点
                tempNode = rootNode;
            }else{
                //检查下一个字符
                position ++;
            }
        }

        //最后一段字符不符合上面三种情况
        //将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(char c) {
        //isAsciiAlphanumeric()判断是否为标准0-9,a-z以及A-Z字符(这里作取反),而0x2E80~0x9FFF是东亚文字字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    //前缀树
    private class TireNode{

        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点(key表示下级字符,value表示下级节点)
        private Map<Character,TireNode> subNodes = new HashMap<>();

        //获取当前节点结束标识
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        //设置结束标识
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TireNode node) {
            subNodes.put(c,node);
        }

        //获取子节点
        public TireNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }
}
