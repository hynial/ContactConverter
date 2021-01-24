package com.hynial.util;

import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;

import java.util.*;
import java.util.stream.Collectors;

public class BizUtil {
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
}