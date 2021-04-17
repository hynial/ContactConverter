package com.hynial.contactconverter.biz.validate;

import com.hynial.contactconverter.biz.duplicate.CsvDuplicate;
import com.hynial.contactconverter.biz.validate.ivalidate.IResultAction;
import com.hynial.contactconverter.entity.ContactsInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ContactsMobileUniqueValidate extends ValidateHandler {
    private List<ContactsInfo> contactsInfoList;

    public ContactsMobileUniqueValidate(List<ContactsInfo> contactsInfoList) {
        this.contactsInfoList = contactsInfoList;
    }

    @Override
    public void validate() {
        CsvDuplicate csvDuplicate = new CsvDuplicate();

        Map<String, List<ContactsInfo>> r = csvDuplicate.uniqueByMobileNumber(this.contactsInfoList);

        IResultAction mobileUniqueResultAction = new MapValidateResultAction(r);

        this.resultActionMap.put(this.getClass().getSimpleName(), mobileUniqueResultAction);
    }

}
