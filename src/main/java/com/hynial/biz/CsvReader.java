package com.hynial.biz;

import com.hynial.annotation.AliasField;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.BizUtil;
import com.hynial.util.CommonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@Data
@NoArgsConstructor
public class CsvReader extends AbstractReader<ContactsInfo> {

    public CsvReader setInput(String input) {
        super.input = input;
        return this;
    }

    @Override
    List<ContactsInfo> readInstant(List<String> lines) {
        List<ContactsInfo> contactsInfoList = new ArrayList<>();
        String headLine = CommonUtil.removeUTF8BOM(lines.get(0));
        String[] heads = headLine.split(",", -1);
        int fieldCount = heads.length;
        if (openLog) {
            System.out.println(lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String unit = line;
            int lineCount = line.split(",", -1).length;
            while (lineCount < fieldCount) {
                i++;
                lineCount += lines.get(i).split(",", -1).length - 1;
                unit += "\n" + lines.get(i);
            }

            if (openLog) System.out.println(unit);

            ContactsInfo contactsInfo = new ContactsInfo();
            matchField(contactsInfo, unit, heads);
            contactsInfoList.add(contactsInfo);
            contactsInfo = null;
        }

        return contactsInfoList;
    }

    private void matchField(ContactsInfo contactsInfo, String row, String[] heads) {
        Map<String, Integer> headMap = new HashMap<>();
        for (int i = 0; i < heads.length; i++) {
            headMap.put(heads[i], Integer.valueOf(i));
        }

        String[] rowFields = row.split(",", -1);

        try {
            Field[] fields = contactsInfo.getClass().getDeclaredFields();
            for (Field f : fields) {
                AliasField aliasField = f.getAnnotation(AliasField.class);
                if (aliasField == null) {
                    continue;
                }
                f.setAccessible(true);
                if (Collection.class.isAssignableFrom(f.getType())) {
                    // List
                    ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                    Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    if (parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)) {
                        // AddressInfo
                        List<AddressInfo> addressInfoList = new ArrayList<>();
                        f.set(contactsInfo, addressInfoList);

                        Integer subIndex = 1, column;
                        while ((column = headMap.get(aliasField.value() + " " + subIndex.intValue())) != null) {
                            String fieldValue = rowFields[column.intValue()];
                            String[] addressValues = fieldValue.split(AddressInfo.ADDRESS_ATTR_SEPARATOR);
                            if(addressValues.length < 9){
                                System.out.println("AddressFormatError!");
                                subIndex++;
                                continue;
                            }
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setAddressType(addressValues[0]);
                            addressInfo.setCountry(addressValues[1]);
                            addressInfo.setState(addressValues[2]);
                            addressInfo.setCity(addressValues[3]);
                            addressInfo.setDistrict(addressValues[4]);
                            addressInfo.setStreet1(addressValues[5]);
                            addressInfo.setStreet2(addressValues[6]);
                            addressInfo.setPostalCode(addressValues[7]);
                            addressInfo.setCountryCode(addressValues[8]);
                            addressInfoList.add(addressInfo);
                            subIndex++;
                        }

                    } else if (parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                        // String
                        List<String> list = new ArrayList<>();
                        f.set(contactsInfo, list);

                        Integer subIndex = 1, column;
                        while ((column = headMap.get(aliasField.value() + " " + subIndex.intValue())) != null) {
                            String fieldValue = rowFields[column.intValue()];
                            list.add(fieldValue);
                            subIndex++;
                        }
                    } else {
                        throw new RuntimeException("UnknownParameterizedType!");
                    }
                } else if (String.class.isAssignableFrom(f.getType())) {
                    // String
                    Integer column = headMap.get(aliasField.value());
                    // !!! Notice this char : ﻿
                    // Merge Field, the same as single field
                    if (BizUtil.getMergeFields().contains(aliasField)) {
                        f.set(contactsInfo, rowFields[column.intValue()]);
                    } else {
                        f.set(contactsInfo, rowFields[column.intValue()]);
                    }
                } else {
                    throw new RuntimeException("UnknownTypeError!");
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
