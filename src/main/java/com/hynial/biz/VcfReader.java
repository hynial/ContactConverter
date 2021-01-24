package com.hynial.biz;

import com.hynial.annotation.AliasField;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.BizUtil;
import com.hynial.util.PropertyUtil;
import com.hynial.visitor.AbstractOrder;
import com.hynial.visitor.OriginalOrderVisitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VcfReader {
    private String vcfPath = PropertyUtil.getValue("vcfPath");
    //    private String outPath = PropertyUtil.getValue("outPath");
    private String outPath = "/Users/hynial/IdeaProjects/ContactConverter/1.csv";

    public void read() {
        File vcfFile = new File(vcfPath);
        if (!vcfFile.exists()) {
            System.out.println("FileNotExist:" + vcfPath);
            return;
        }

        Pattern pattern = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get(vcfPath));
            ContactsInfo contactsInfo = null;
            List<ContactsInfo> contactsInfoList = new ArrayList<>();
            String itemRecord = "";
            for (String line : lines) {
                if (line == null) continue;
                //System.out.println(line);

                if (line.startsWith("BEGIN:")) {
                    contactsInfo = new ContactsInfo();
                    itemRecord = "";
                } else if (line.startsWith("END:")) {
                    System.out.println(itemRecord);
                    matchFields(contactsInfo, itemRecord);
                    System.out.println(contactsInfo.toString());
                    contactsInfoList.add(contactsInfo);
                    contactsInfo = null;
                } else {
                    itemRecord += line + "^^^";
                }
            }

            if (contactsInfoList != null && contactsInfoList.size() > 0) {
                buildCSV(contactsInfoList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void matchFields(ContactsInfo contactsInfo, String recordParam) {
        try {
            Field[] fields = contactsInfo.getClass().getDeclaredFields();
            for (Field f : fields) {
                AliasField aliasField = f.getAnnotation(AliasField.class);
                if (aliasField != null) {
                    if (!"".equals(aliasField.reg())) {
                        f.setAccessible(true);
                        Pattern pattern = Pattern.compile(aliasField.reg());
                        Matcher matcher = pattern.matcher(recordParam);

                        if (Collection.class.isAssignableFrom(f.getType())) {
                            // List
                            ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                            Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            if(parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                                // String
                                List<String> list = new ArrayList<>();
                                while (matcher.find()) {
                                    list.add(matcher.group(1));
                                }
                                f.set(contactsInfo, list);
                            }else if(parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)){
                                // AddressInfo
                                List<AddressInfo> addressInfoList = new ArrayList<>();
                                while(matcher.find()){
                                    AddressInfo addressInfo = new AddressInfo();
                                    String preDeal = matcher.group(1);
                                    String[] deals = preDeal.split(";", -1);
                                    if (deals == null || deals.length < 6){
                                        throw new RuntimeException("AddressFormatError!");
                                    }
                                    addressInfo.setStreet(deals[2] == null ? "" : deals[2].replace("\\n", " "));
                                    addressInfo.setCity(deals[3]);
                                    addressInfo.setState(deals[4]);
                                    addressInfo.setPostalCode(deals[5]);
                                    addressInfo.setCountry(deals[6]);

                                    String itemFlag = recordParam.substring(matcher.start(0) - 8, matcher.start(0) - 1 ).replaceAll("\\^", "");
                                    String countryCodeReg = itemFlag + "\\.X-ABADR:(.*?)\\^";
                                    Pattern pattern1 = Pattern.compile(countryCodeReg, Pattern.CASE_INSENSITIVE);
                                    Matcher matcher1 = pattern1.matcher(recordParam);
                                    if(matcher1.find()){
                                        addressInfo.setCountryCode(matcher1.group(1));
                                    }

                                    String areaReg = itemFlag + "\\.X-APPLE-SUBLOCALITY:(.*?)\\^";
                                    pattern1 = Pattern.compile(areaReg, Pattern.CASE_INSENSITIVE);
                                    matcher1 = pattern1.matcher(recordParam);
                                    if(matcher1.find()){
                                        addressInfo.setAddress(matcher1.group(1));
                                    }

                                    String addressTypeReg = itemFlag + "\\.X-ABLabel:(.*?)\\^";
                                    pattern1 = Pattern.compile(addressTypeReg, Pattern.CASE_INSENSITIVE);
                                    matcher1 = pattern1.matcher(recordParam);
                                    if(matcher1.find()){
                                        addressInfo.setAddressType(matcher1.group(1));
                                    }

                                    addressInfoList.add(addressInfo);
                                }

                                f.set(contactsInfo, addressInfoList);
                            }
                        } else if(BizUtil.getMergeFields().contains(aliasField.value())){
                            String[] regs = aliasField.reg().split(",");
                            if(regs == null || regs.length % 2 == 1) {
                                throw new RuntimeException("MergeFieldsConfigError");
                            }

                            // 增加item前缀, 由唯一性的field正则(Value)
                            String[] preItem = null;
                            for(int n = 1; n < regs.length; n += 2) {
                                Pattern uniquePattern = Pattern.compile(regs[n], Pattern.CASE_INSENSITIVE);
                                Matcher uniqueMatcher = uniquePattern.matcher(recordParam);
                                List<String> preItemList = new ArrayList<>();
                                while (uniqueMatcher.find()) {
                                    String itemFlag = recordParam.substring(uniqueMatcher.start(0) - 8, uniqueMatcher.start(0) - 1).replaceAll("\\^", "");
                                    preItemList.add(itemFlag);
                                }
                                preItem = preItemList.toArray(String[]::new);
                            }
                            Map<String, String> linkedHashMap = new LinkedHashMap<>();

                            Integer integer = 0;
                            Integer j = 0;
                            for(int start = 0; start < regs.length; start += 2){
                                // 正则唯一的情况
//                                Pattern titlePattern = Pattern.compile(regs[start], Pattern.CASE_INSENSITIVE);
//                                Matcher titleMatcher = titlePattern.matcher(recordParam);
//                                while(titleMatcher.find()){
//                                    linkedHashMap.put(integer.toString(), titleMatcher.group(1));
//                                    integer++;
//                                }
                                // 正则不唯一的情况
                                for(int m = 0; m < preItem.length; m++){
                                    Pattern titlePattern = Pattern.compile(preItem[m] + "\\." + regs[start], Pattern.CASE_INSENSITIVE);
                                    Matcher titleMatcher = titlePattern.matcher(recordParam);
                                    if(titleMatcher.find()) {
                                        linkedHashMap.put(integer.toString(), titleMatcher.group(1).replaceAll("[_\\$<>!]", ""));
                                        integer++;
                                    }
                                }
                                String cateChar = ":";
                                Pattern valuePattern = Pattern.compile(regs[start + 1], Pattern.CASE_INSENSITIVE);
                                Matcher valueMatcher = valuePattern.matcher(recordParam);
                                while(valueMatcher.find()){
                                    linkedHashMap.put(j.toString(), linkedHashMap.get(j.toString()) + cateChar + valueMatcher.group(1));
                                    j++;
                                }
                            }
                            String totalValue = linkedHashMap.values().stream().collect(Collectors.joining("\n"));
                            f.set(contactsInfo, "\"" + totalValue + "\"");
                        } else {
                            String regValue = "";
                            if (matcher.find()) {
                                regValue = matcher.group(1);
                            }
                            f.set(contactsInfo, regValue);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void buildCSV(List<ContactsInfo> contactsInfoList) {
        try {
            String titles = BizUtil.getAllHeadTitles();
            System.out.println(titles);

            OriginalOrderVisitor order = new OriginalOrderVisitor();
            String result = "";
            for (ContactsInfo contactsInfo : contactsInfoList) {
                String line = order.v(contactsInfo);
                System.out.println(line);
                result += line + "\n";
            }

            // add BOM head to avoid excel open encode error.
            byte[] BOM = {(byte) 0xEF,(byte) 0xBB,(byte) 0xBF};
            Files.write(Paths.get(outPath), BOM, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//            Files.writeString(Paths.get(outPath), titles + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.writeString(Paths.get(outPath), titles + "\n" + result, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
