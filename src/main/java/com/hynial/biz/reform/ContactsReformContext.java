package com.hynial.biz.reform;

import com.hynial.entity.ContactsInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ContactsReformContext {
    private List<ContactsInfo> contactsInfoList;
    private List<IReform<ContactsInfo>> reformList;

    public ContactsReformContext(List<ContactsInfo> contactsInfoList) {
        this.contactsInfoList = contactsInfoList;
    }

    public List<ContactsInfo> reformContext(){
        if(this.contactsInfoList == null) return null;

        this.reformList = new ArrayList<>();
        IReform<ContactsInfo> contactsInfoIReform = new ContactsDetachNameReform();
        this.reformList.add(contactsInfoIReform);

        if(this.reformList == null || this.reformList.size() == 0){
            return this.contactsInfoList;
        }


        for(ContactsInfo contactsInfo : this.contactsInfoList){
            this.reformList.forEach(reform -> {
                reform.reform(contactsInfo);
            });
        }

        return this.contactsInfoList;
    }
}
