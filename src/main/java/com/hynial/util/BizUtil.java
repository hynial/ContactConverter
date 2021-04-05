package com.hynial.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BizUtil {
    public static final String REG_INDEX = " \\d{1,2}$";

    public static String[] getHeadTitles() {
        String[] headTitles = Arrays.stream(PropertyUtil.getValue("headTitles").split(",")).map(title -> title.trim()).toArray(String[]::new);
        return headTitles;
    }

    public static String getAllHeadTitles() {
        return getHeadTitlesMap().keySet().stream().collect(Collectors.joining(","));
    }

    public static int getTitleCount(String title){
        String[] tmp = PropertyUtil.getValue("headTitles").split(title + "_");
        if(tmp != null && tmp.length > 1 && tmp[1] != null){
            String[] countString = tmp[1].split("\\D");
            if(countString != null && countString.length > 0) {
                return Integer.parseInt(countString[0]);
            }
        }

        return 0;
    }

    public static List<String> getMergeFields(){
        return Arrays.stream(PropertyUtil.getValue("mergeFields").split(",")).collect(Collectors.toList());
    }

    public static Map<String, Integer> getHeadTitlesMap() {
        Map<String, Integer> headMap = null;
        String[] titles = getHeadTitles();
        if (titles != null && titles.length > 0) {
            headMap = new LinkedHashMap<>();
        }

        int j = 0;
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            if (title.indexOf("_") > -1) {
                String[] titleIndex = title.split("_");
                int c = Integer.parseInt(titleIndex[1]);
                for (int t = 0; t < c; t++) {
                    headMap.put(titleIndex[0] + " " + (t + 1), j++);
                }
            } else {
                headMap.put(title, j++);
            }
        }

        return headMap;
    }

    public static List<String> getMultiFamilyNames(){
        return Arrays.stream(PropertyUtil.getValue("multiFamilyNames").split(",")).collect(Collectors.toList());
    }

    public static List<String> notFamilyNames(){
        return Arrays.stream(PropertyUtil.getValue("notFamilyNames").split(",")).collect(Collectors.toList());
    }

    public static String getPhoneType(String phoneNumber){
        String phoneType = "M"; // Mobile
        if (phoneNumber.startsWith("0")) {
            phoneType = "H"; // HOME
        }
        if(phoneNumber.length() <= 6){
            phoneType = "S"; // Short number
        }

        return phoneType;
    }
}
