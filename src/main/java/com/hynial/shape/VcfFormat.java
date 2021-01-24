package com.hynial.shape;

import com.hynial.entity.ContactsInfo;

import java.util.List;

public class VcfFormat {
    private static final String LINE_SEPARATOR = "\n";
    private static final String START = "BEGIN:VCARD";
    private static final String VERSION = "VERSION:3.0";
    private static final String PRODID = "PRODID:-//Apple Inc.//iOS 13.6//EN";
    private static final String END = "END:VCARD";


    public String shape(ContactsInfo contactsInfo){
        String result = "";

        return result;
    }

    public String shapes(List<ContactsInfo> contactsInfoList){
        String result = "";

        for (ContactsInfo contactsInfo : contactsInfoList) {
            String item = this.shape(contactsInfo);
            result += item + LINE_SEPARATOR;
        }

        return result;
    }
}
