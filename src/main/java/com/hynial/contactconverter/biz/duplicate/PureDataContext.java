package com.hynial.contactconverter.biz.duplicate;

import com.hynial.contactconverter.biz.duplicate.filter.ContactsFieldEqualFilter;
import com.hynial.contactconverter.biz.duplicate.filter.ContactsFieldLengthFilter;
import com.hynial.contactconverter.biz.duplicate.filter.IContactFilter;
import com.hynial.contactconverter.biz.duplicate.merge.ContactsFieldsMerge;
import com.hynial.contactconverter.biz.duplicate.merge.IContactMerge;
import com.hynial.contactconverter.entity.ContactsInfo;
import com.hynial.contactconverter.shape.ContactsComparator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PureDataContext {
    private List<ContactsInfo> dataList;

    public PureDataContext(List<ContactsInfo> dataList) {
        this.dataList = dataList;
    }

    public List<ContactsInfo> pureData(){
        PureDataFactory pureDataFactory = new PureDataFactory(this.dataList);

        List<IContactFilter> contactFilterList = new ArrayList<>();
        IContactFilter contactsFieldEqualFilter = new ContactsFieldEqualFilter("Mobile Phone 1", null);
        IContactFilter contactsFieldLengthFilter = new ContactsFieldLengthFilter("Display Name", 3);

        contactFilterList.add(contactsFieldEqualFilter);
//        contactFilterList.add(contactsFieldLengthFilter);

        pureDataFactory.setContactFilterList(contactFilterList);

        List<IContactMerge> contactMergeList = new ArrayList<>();
        IContactMerge contactMergeLastAndFirstName = new ContactsFieldsMerge(new ArrayList<>(List.of("Last Name", "First Name")));
        contactMergeList.add(contactMergeLastAndFirstName);
        IContactMerge contactMergeDisplayName = new ContactsFieldsMerge(new ArrayList<>(List.of("Display Name")));
//        contactMergeList.add(contactMergeDisplayName);

        pureDataFactory.setContactMergeList(contactMergeList);

        return pureDataFactory.work().stream().sorted(ContactsComparator.comparator).collect(Collectors.toList());
    }
}
