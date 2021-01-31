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
        String result = "";
        try {
            for (int i = 0; i < fieldStrings.size(); i++) {
                String fieldAlias = fieldStrings.get(i);
                fieldAlias = fieldAlias.replaceAll(BizUtil.REG_INDEX, "");
                Field f = contactsInfo.getAliasMap().get(fieldAlias);
                if (f == null) {
                    throw new RuntimeException("NotExistField:" + fieldAlias);
                }
                f.setAccessible(true);
                if (Collection.class.isAssignableFrom(f.getType())) {
                    ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                    Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                    int k = BizUtil.getTitleCount(fieldAlias);
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
//                        result += fieldAlias + j + ",";
                        result += "" + ",";
                        i++;
                        j++;
                    }

                    i--;
                } else {
                    String v = "";
                    if (f.get(contactsInfo) == null) {
//                        if (fieldAlias.equals("Revise Time")){
//                            v = CommonUtil.getInstantString();
//                        }
                    } else {
                        v = f.get(contactsInfo).toString();
                        if (fieldAlias.equals("Home Phone") && !v.startsWith("'")) {
                            v = "'" + v;
                        }
                    }

                    result += v + ",";
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result.substring(0, result.length() - 1);
    }
}
