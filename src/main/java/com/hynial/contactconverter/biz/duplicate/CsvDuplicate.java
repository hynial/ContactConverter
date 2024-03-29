package com.hynial.contactconverter.biz.duplicate;

import com.hynial.contactconverter.entity.ContactsInfo;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CsvDuplicate {
    // by column
    public Map<String, List<ContactsInfo>> categoryByAlias(List<ContactsInfo> contactsInfoList, String aliasOpt) {
        return categoryByAlias(contactsInfoList, new ArrayList<>(Arrays.asList(new String[]{aliasOpt})));
    }

    // by columns, except same fields in List
    public Map<String, List<ContactsInfo>> categoryByAlias(List<ContactsInfo> contactsInfoList, List<String> aliasOpts){
        Map<String, List<ContactsInfo>> categoryMap = new HashMap<>();

        List<String> aliasOptCopy = new ArrayList<>(aliasOpts);

        try {
            for(ContactsInfo c : contactsInfoList){
                String valueString = "", separator = ",";
                for (int i = 0; i < aliasOptCopy.size(); i++) {
                    String alias = aliasOptCopy.get(i);
                    if(ContactsInfo.getAliasMap().get(alias) == null){
                        aliasOptCopy.set(i, aliasOpts.get(i));
                    }
                    alias = aliasOptCopy.get(i);

                    String value = c.getStringByAlias(alias);
                    if(value == null) value = "";
                    valueString += value + separator;
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
            categoryMap.put(val, new ArrayList<>(Arrays.asList(new ContactsInfo[]{c})));
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
                    result.put(mobiles.get(i), new ArrayList<>(Arrays.asList(new ContactsInfo[]{contactsInfo})));
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
