package com.hynial.biz.duplicate.filter;

import com.hynial.entity.ContactsInfo;

public interface IContactFilter {
    boolean filter(ContactsInfo contactsInfo);
}
