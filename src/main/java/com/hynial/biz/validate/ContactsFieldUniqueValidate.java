package com.hynial.biz.validate;

import com.hynial.biz.duplicate.CsvDuplicate;
import com.hynial.biz.validate.ivalidate.IResultAction;
import com.hynial.entity.ContactsInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ContactsFieldUniqueValidate extends ValidateHandler {
    private String fieldAliasName;
    private List<ContactsInfo> contactsInfoList;

    public ContactsFieldUniqueValidate(String fieldAliasName, List<ContactsInfo> contactsInfoList) {
        this.fieldAliasName = fieldAliasName;
        this.contactsInfoList = contactsInfoList;
    }

    @Override
    public void validate() {
        CsvDuplicate csvDuplicate = new CsvDuplicate();
        Map<String, List<ContactsInfo>> r = csvDuplicate.categoryByAlias(this.contactsInfoList, this.fieldAliasName);

        r = r.entrySet().stream().filter(entry -> entry.getValue().size() > 1).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        IResultAction contactFieldResultAction = new MapValidateResultAction(r);

        this.resultActionMap.put(this.getClass().getName(), contactFieldResultAction);
    }
}
