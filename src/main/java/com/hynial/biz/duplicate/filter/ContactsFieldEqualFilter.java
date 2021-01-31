package com.hynial.biz.duplicate.filter;

import com.hynial.entity.ContactsInfo;
import lombok.Data;

@Data
public class ContactsFieldEqualFilter implements IContactFilter {
    private String fieldAlias;
    private String filterValue;

    public ContactsFieldEqualFilter(String fieldAlias, String filterValue) {
        this.fieldAlias = fieldAlias;
        this.filterValue = filterValue;
    }

    @Override
    public boolean filter(ContactsInfo contactsInfo) {
        try {
            String actualValue = contactsInfo.getStringByAlias(this.fieldAlias);
            return this.filterValue == null ? actualValue == null : this.filterValue.equalsIgnoreCase(actualValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
