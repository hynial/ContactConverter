package com.hynial.contactconverter.biz.duplicate.merge;

import com.hynial.contactconverter.entity.ContactsInfo;

import java.util.List;

public interface IContactMerge {
    List<ContactsInfo> merge(List<ContactsInfo> contactsInfoList);
}
