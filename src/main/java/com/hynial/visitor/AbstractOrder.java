package com.hynial.visitor;

import com.hynial.annotation.AliasField;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.BizUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractOrder {
    public AbstractOrder() {
        Map<String, Integer> fieldMap = BizUtil.getHeadTitlesMap();
        this.fieldStrings = fieldMap.keySet().stream().collect(Collectors.toList());
    }

    protected List<String> fieldStrings;

    protected abstract void visit();

    public String v(ContactsInfo contactsInfo) {
        Field[] fields = contactsInfo.getClass().getDeclaredFields();

        String result = "";
        try {
            for (int i = 0; i < fieldStrings.size(); i++) {
                String field = fieldStrings.get(i);
                for (Field f : fields) {
                    AliasField aliasField = f.getAnnotation(AliasField.class);
                    if (aliasField != null) {
                        if (field.equals(aliasField.value()) || field.replaceAll(BizUtil.REG_INDEX, "").equals(aliasField.value())) {
                            f.setAccessible(true);
                            if (Collection.class.isAssignableFrom(f.getType())) {
                                ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                                Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                                int k = BizUtil.getTitleCount(aliasField.value());
                                int j = 0;
                                if (parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                                    List<String> stringList = (List<String>) f.get(contactsInfo);
                                    if (stringList != null) {
                                        for (String s : stringList) {
                                            result += s + ",";
                                            i++;
                                            j++;
                                        }
                                    }
                                } else if (parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)) {
                                    List<AddressInfo> addressInfoList = (List<AddressInfo>) f.get(contactsInfo);
                                    if (addressInfoList != null) {
                                        for (AddressInfo s : addressInfoList) {
                                            result += (s != null ? s.toString() : "") + ",";
                                            i++;
                                            j++;
                                        }
                                    }
                                } else {
                                    throw new RuntimeException("Unknown Field Type!");
                                }

                                while (j < k) {
//                                    result += aliasField.value() + j + ",";
                                    result += "" + ",";
                                    i++;
                                    j++;
                                }

                                i--;
                            } else {
                                String v = "";
                                if(f.get(contactsInfo) == null){
//                                    if (aliasField.value().equals("Revise Time")){
//                                        v = CommonUtil.getInstantString();
//                                    }
                                }else{
                                    v = f.get(contactsInfo).toString();
                                    if (aliasField.value().equals("Home Phone") && !v.startsWith("'")){
                                        v = "'" + v;
                                    }
                                }

                                result += v + ",";
                            }

                            break;
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result.substring(0, result.length() - 1);
    }
}
