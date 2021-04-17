package com.hynial.contactconverter.biz.duplicate;

import com.hynial.contactconverter.biz.duplicate.filter.IContactFilter;
import com.hynial.contactconverter.biz.duplicate.merge.IContactMerge;
import com.hynial.contactconverter.entity.ContactsInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PureDataFactory {
    private List<ContactsInfo> contactsInfoList;
    private List<IContactFilter> contactFilterList = new ArrayList<>();
    private List<IContactMerge> contactMergeList = new ArrayList<>();

    public PureDataFactory(List<ContactsInfo> contactsInfoList) {
        this.contactsInfoList = contactsInfoList;
    }

    public List<ContactsInfo> work(){
        if (this.contactsInfoList == null) return null;

        List<ContactsInfo> workingContactInfoList;

        if(this.contactFilterList.size() > 0) {
            workingContactInfoList = filterContext(this.contactsInfoList);
        }else{
            workingContactInfoList = new ArrayList<>(this.contactsInfoList);
        }

        if(this.contactMergeList.size() > 0) {
            workingContactInfoList = mergeContext(workingContactInfoList);
        }

        return workingContactInfoList;
    }

    public List<ContactsInfo> filterContext(List<ContactsInfo> contactsInfoList) {
        List<ContactsInfo> workingContactInfoList = new ArrayList<>();
        for (ContactsInfo contactsInfo : contactsInfoList) {
            if(!filterContext(contactsInfo)){
                workingContactInfoList.add(contactsInfo);
            }
        }

        return workingContactInfoList;
    }

    private boolean filterContext(ContactsInfo contactsInfo){

        for (IContactFilter iContactFilter : this.contactFilterList) {
            if(iContactFilter.filter(contactsInfo)){
                return true;
            }
        }

        return false;
    }

    public List<ContactsInfo> mergeContext(List<ContactsInfo> contactsInfoList){
        List<ContactsInfo> workingList = new ArrayList<>(contactsInfoList);
        for(IContactMerge contactMerge : this.contactMergeList){
            workingList = contactMerge.merge(workingList);
        }

        return workingList;
    }
}
