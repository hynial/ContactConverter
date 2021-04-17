package com.hynial.contactconverter.biz;

import com.hynial.contactconverter.util.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractReader<T> {
    protected final boolean openLog = CommonUtil.getOpenLog();
    protected String input;

    public List<T> read(){
        try {
            System.out.println("Input:" + input);
            File file = new File(input);
            if (!file.exists()) {
                System.out.println("FileNotExist:" + input);
                return null;
            }

            List<String> lines = Files.readAllLines(Paths.get(input));

            return readInstant(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    abstract List<T> readInstant(List<String> lines);
}
