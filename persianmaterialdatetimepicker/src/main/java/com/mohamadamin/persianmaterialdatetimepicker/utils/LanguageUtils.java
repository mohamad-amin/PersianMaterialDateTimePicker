package com.mohamadamin.persianmaterialdatetimepicker.utils;

import java.util.ArrayList;

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

    public static String[] getPersianNumbers(String[] strings) {
        for (int i=0; i<strings.length; i++) {
            strings[i] = getPersianNumbers(strings[i]);
        }
        return strings;
    }

    public static ArrayList<String> getPersianNumbers(ArrayList<String> strings) {
        for (int i=0; i<strings.size(); i++) {
            strings.set(i, getPersianNumbers(strings.get(i)));
        }
        return strings;
    }

    public static String getLatinNumbers(String string) {
        string = string.replace("۰", "0");
        string = string.replace("١", "1");
        string = string.replace("۲", "2");
        string = string.replace("۳", "3");
        string = string.replace("۴", "4");
        string = string.replace("۵", "5");
        string = string.replace("۶", "6");
        string = string.replace("۷", "7");
        string = string.replace("۸", "8");
        string = string.replace("۹", "9");
        return string;
    }

}
