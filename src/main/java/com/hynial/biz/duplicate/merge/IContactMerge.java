package com.hynial.biz.duplicate.merge;

import com.hynial.entity.ContactsInfo;

import java.util.List;

public interface IContactMerge {
    List<ContactsInfo> merge(List<ContactsInfo> contactsInfoList);
}
