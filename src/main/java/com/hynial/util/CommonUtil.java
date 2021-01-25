package com.hynial.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CommonUtil {

    public static void writeFileWithBom(String outPath, String content){
        try {
            // add BOM head to avoid excel open encode error.
            byte[] BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            Files.write(Paths.get(outPath), BOM, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.writeString(Paths.get(outPath), content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String outPath, String content){
        try {
             Files.writeString(Paths.get(outPath), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String instantToString(String instantString){
        try {
            Instant instant = Instant.parse(instantString);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
            return dateTimeFormatter.format(instant);
        } catch (DateTimeParseException e){
            System.out.println(e.getMessage());
            return instantString;
        }
    }

    public static final Long LunarConst = 778017L;
    public static String formatLunar(String originalString){
        try{
            boolean isRunYue = false;
            if(originalString.indexOf("L") > -1){
                originalString = originalString.replaceAll("L", "");
                isRunYue = true;
            }

            String date = originalString.substring(originalString.length() - 4);
            String year = originalString.substring(0, originalString.length() - 4);
            Long y = Long.valueOf(year) - LunarConst;

            return y.toString() + (isRunYue ? "L" : "") + date;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return originalString;
        }
    }

    public static String getLunarBirthdayLong(String readableDate){
        try {
            boolean isRunYue = false;
            if(readableDate.indexOf("L") > -1){
                readableDate = readableDate.replaceAll("L", "");
                isRunYue = true;
            }
            String date = readableDate.substring(readableDate.length() - 4);
            if(isRunYue){
                date = date.substring(0, 2) + "L" + date.substring(2);
            }
            String year = readableDate.substring(0, readableDate.length() - 4);
            Long y = Long.valueOf(year) + LunarConst;

            String rst = y.toString() + date;
            int i = rst.length();
            while(i < 12){
                rst = "0" + rst;
                i++;
            }
            return rst;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return readableDate;
        }
    }
}
