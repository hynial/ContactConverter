package com.hynial.biz.duplicate;

import com.hynial.biz.CsvReader;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.BizUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CsvDuplicate {
    public static int minMobileNumber = 5;
    private String input;

    public CsvDuplicate(String input) {
        this.input = input;
    }

    private List<ContactsInfo> read(){
        CsvReader csvReader = new CsvReader().setInput(this.input);
        List<ContactsInfo> contactsInfoList = csvReader.read();
        return contactsInfoList;
    }
    // by column
    public Map<String, List<ContactsInfo>> categoryByAlias(List<ContactsInfo> contactsInfoList, String aliasOpt) {
        return categoryByAlias(contactsInfoList, new ArrayList<>(List.of(aliasOpt)));
    }

    // by columns, except same fields in List
    public Map<String, List<ContactsInfo>> categoryByAlias(List<ContactsInfo> contactsInfoList, List<String> aliasOpts){
        Map<String, List<ContactsInfo>> categoryMap = new HashMap<>();

        List<String> aliasOptCopy = new ArrayList<>(aliasOpts);
        LinkedHashMap<String, Integer> aliasIndexMap = new LinkedHashMap<String, Integer>();

        for (int i = 0; i < aliasOptCopy.size(); i++) {
            String aliasOpt = aliasOptCopy.get(i);
            String[] aliasInd = aliasOpt.trim().split(BizUtil.REG_INDEX, -1);
            String alias = aliasOpt;
            int ind = -1;
            if (aliasInd.length > 1){
                alias = aliasInd[0];
                ind = Integer.parseInt(aliasOpt.replace(alias, "").trim());
            }
            aliasOptCopy.set(i, alias);
            aliasIndexMap.put(alias, ind);
        }

        try {
            for(ContactsInfo c : contactsInfoList){
                String valueString = "", separator = ",";
                for (int i = 0; i < aliasOptCopy.size(); i++) {
                    String alias = aliasOptCopy.get(i);
                    if(ContactsInfo.getAliasMap().get(alias) == null){
                        aliasOptCopy.set(i, aliasOpts.get(i));
                    }
                    alias = aliasOptCopy.get(i);

                    Object value = c.getValueByAlias(alias);

                    if (value instanceof String) { // null
                        valueString += value + separator;
                    } else if(value instanceof List<?>){
                        int ind = aliasIndexMap.get(alias).intValue();
                        ParameterizedType parameterizedType = (ParameterizedType) ContactsInfo.getAliasMap().get(alias).getGenericType();
                        Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        if (parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                            List<String> vals = (List<String>) value;
                            if(vals != null && ind != -1 && ind  < vals.size()){
                                String val = vals.get(ind - 1);
                                valueString += val + separator;
                            }
                        } else if (parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)) {
                            List<AddressInfo> vals = (List<AddressInfo>) value;
                            // TODO
                            throw new RuntimeException("TODO-SupportedType:" + AddressInfo.class.getSimpleName());
                        }else{
                            throw new RuntimeException("UnsupportedType");
                        }
                    }else{
                        if(null == value){
                            System.out.println("null value happened!");
                        }
                        throw new RuntimeException("UnsupportedTypeWhenGetValueFromAlias:" + alias);
                    }

                }
                if(valueString.endsWith(separator)) valueString = valueString.substring(0, valueString.length() - 1);
                addValue(categoryMap, valueString, c);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return categoryMap;
    }

    private void addValue(Map<String, List<ContactsInfo>> categoryMap, String val, ContactsInfo c){
        List<ContactsInfo> contactsInfos = categoryMap.get(val);
        if(contactsInfos == null) {
            categoryMap.put(val, new ArrayList<>(List.of(c)));
        }else{
            contactsInfos.add(c);
        }
    }

    // by all mobile numbers find same numbers contact
    public Map<String, List<ContactsInfo>> uniqueByMobileNumber(List<ContactsInfo> contactsInfoList){
        if(contactsInfoList == null || contactsInfoList.size() == 0) return null;

        Map<String, List<ContactsInfo>> result = new HashMap<>();
        for(ContactsInfo contactsInfo : contactsInfoList){
            List<String> mobiles = contactsInfo.getMobilePhones();
            if(mobiles == null) continue;

            for (int i = 0; i < mobiles.size(); i++) {
                if(result.get(mobiles.get(i)) == null){
                    result.put(mobiles.get(i), new ArrayList<>(List.of(contactsInfo)));
                }else{
                    result.get(mobiles.get(i)).add(contactsInfo);
                }
            }
        }

        result = result.entrySet().stream().filter(entry ->{
            List<ContactsInfo> tmp = entry.getValue();
            if(tmp == null) return false;
            if(tmp.size() > 1) return true;
            return false;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if(result == null) return null;

        return result;
    }
}
