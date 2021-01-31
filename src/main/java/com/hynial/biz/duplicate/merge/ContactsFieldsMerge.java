package com.hynial.biz.duplicate.merge;

import com.hynial.entity.ContactsInfo;
import com.hynial.util.CommonUtil;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ContactsFieldsMerge implements IContactMerge {
    private List<String> fieldAliasList;

    public ContactsFieldsMerge(List<String> fieldAliasList) {
        this.fieldAliasList = fieldAliasList;
    }

    @Override
    public List<ContactsInfo> merge(List<ContactsInfo> contactsInfoList) {
        Map<String, ContactsInfo> contactsInfoMap = new LinkedHashMap<>();
        for (ContactsInfo contactsInfo : contactsInfoList){
            String k = mergeKey(contactsInfo);
            ContactsInfo originalContactInfo = contactsInfoMap.get(k);
            if(originalContactInfo == null){
                contactsInfoMap.put(k, contactsInfo);
            }else{
                originalContactInfo.merge(contactsInfo);
            }
        }
        return contactsInfoMap.values().stream().collect(Collectors.toList());
    }

    private String mergeKey(ContactsInfo contactsInfo){
        String key = "";
        for (String fieldAlias : this.fieldAliasList) {
            try {
                String fieldValue = contactsInfo.getStringByAlias(fieldAlias);
                if(CommonUtil.isEmpty(fieldValue)) fieldValue = "";

                key += fieldValue + ",";
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage() + "WhenDeal:" + fieldAlias);
            }
        }

        if (key.endsWith(",")) key = key.substring(0, key.length() - 1);

        if(CommonUtil.isEmpty(key.replaceAll(",", ""))){
            throw new RuntimeException("Impossible: fields all null!" + this.fieldAliasList.stream().collect(Collectors.joining(",")));
        }
        return key;
    }
}
