package com.honghuang.community;

import java.io.IOException;

public class WkTests {

    public static void main(String[] args) {
        String cmd = "D:/javaapps/wkhtmltopdf/bin/wkhtmltoimage --quality 75  https://www.baidu.com d:/work/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
