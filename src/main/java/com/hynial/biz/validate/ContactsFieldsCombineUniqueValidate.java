package com.hynial.biz.validate;

import com.hynial.biz.duplicate.CsvDuplicate;
import com.hynial.biz.validate.ivalidate.IResultAction;
import com.hynial.entity.ContactsInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ContactsFieldsCombineUniqueValidate extends ValidateHandler {
    private List<ContactsInfo> contactsInfoList;
    private List<String> aliasFields;

    public ContactsFieldsCombineUniqueValidate(List<ContactsInfo> contactsInfoList, List<String> aliasFields) {
        this.contactsInfoList = contactsInfoList;
        this.aliasFields = aliasFields;
    }

    @Override
    public void validate() {
        CsvDuplicate csvDuplicate = new CsvDuplicate();
        Map<String, List<ContactsInfo>> r = csvDuplicate.categoryByAlias(this.contactsInfoList, this.aliasFields);

        r = r.entrySet().stream().filter(entry -> entry.getValue().size() > 1).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        IResultAction contactFieldResultAction = new MapValidateResultAction(r);

        this.resultActionMap.put(this.getClass().getSimpleName() + "/" + this.aliasFields.stream().collect(Collectors.joining(",")), contactFieldResultAction);
    }
}
