package com.hynial.biz.duplicate.filter;

import com.hynial.entity.ContactsInfo;
import lombok.Data;

@Data
public class ContactsFieldLengthFilter implements IContactFilter {
    private String fieldAlias;
    private int minLength;
    private boolean filterNullValue = false;

    public ContactsFieldLengthFilter(String fieldAlias, int minLength) {
        this.fieldAlias = fieldAlias;
        this.minLength = minLength;
    }

    public boolean filter(ContactsInfo contactsInfo) {
        try {
            String fieldValue = contactsInfo.getStringByAlias(this.fieldAlias);
            if(fieldValue == null){
                if(this.filterNullValue) {
                    return true;
                }else{
                    return false;
                }
            }

            if(fieldValue.length() < minLength){
                return true;
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
