package com.mohamadamin.persianmaterialdatetimepicker.utils;

public class LanguageUtils {

    public static String getPersianNumbers(String string) {
        string = string.replace("0", "۰");
        string = string.replace("1", "١");
        string = string.replace("2", "۲");
        string = string.replace("3", "۳");
        string = string.replace("4", "۴");
        string = string.replace("5", "۵");
        string = string.replace("6", "۶");
        string = string.replace("7", "۷");
        string = string.replace("8", "۸");
        string = string.replace("9", "۹");
        return string;
    }

}
