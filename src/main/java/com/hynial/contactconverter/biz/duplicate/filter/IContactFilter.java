package com.hynial.contactconverter.biz.duplicate.filter;

import com.hynial.contactconverter.entity.ContactsInfo;

public interface IContactFilter {
    boolean filter(ContactsInfo contactsInfo);
}
