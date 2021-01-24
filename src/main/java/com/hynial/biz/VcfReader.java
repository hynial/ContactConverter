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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VcfReader {
    private String vcfPath = PropertyUtil.getValue("vcfPath");
    //    private String outPath = PropertyUtil.getValue("outPath");
    private String outPath = "/Users/hynial/IdeaProjects/ContactConverter/1.csv";

    private static String cateString = "^^^";
    private static String regName = "^N:([^;]*?);([^;]*?);.*";
    private static String regMobileNumber = "^TEL;type=CELL;type=VOICE;type=pref:(\\d*)"; // TEL;type=CELL;type=VOICE;type=pref:15160087650
    private static String regOrg = "ORG:([^;]*);";
    private static String regTotal = "^^VERSION:.*[^F]N:(?<lastName>[^;]*?);(?<firstName>[^;]*?);.*(:?TEL;type=CELL;type=VOICE;type=pref:(?<mobile>\\d*))?.*(\\^ORG:(?<org>[^;]*);)?.*";

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
//        Pattern pattern = Pattern.compile(regTotal);
//        Matcher matcher = pattern.matcher(recordParam);
//        if(matcher.find()){
//            try {
//                contactsInfo.setFirstName(matcher.group("firstName"));
//                contactsInfo.setLastName(matcher.group("lastName"));
//                contactsInfo.setDisplayName(contactsInfo.getLastName() + contactsInfo.getFirstName());
//                contactsInfo.setMobilePhone(matcher.group("mobile"));
//                contactsInfo.setOrganization(matcher.group("org"));
//            }catch (IllegalArgumentException illegalArgumentException){
//                System.out.println("~~~~~~~~~~" + illegalArgumentException.getMessage() + "~~~~~~~~");
//            }
//        }

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

            Files.writeString(Paths.get(outPath), titles + "\n",
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Files.writeString(Paths.get(outPath), result,
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
