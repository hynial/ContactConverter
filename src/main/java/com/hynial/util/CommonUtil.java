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
}
