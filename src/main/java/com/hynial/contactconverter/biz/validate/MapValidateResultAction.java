package com.hynial.contactconverter.biz.validate;

import com.hynial.contactconverter.biz.validate.ivalidate.IResultAction;
import com.hynial.contactconverter.entity.ContactsInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class MapValidateResultAction implements IResultAction {

    private Map<String, List<ContactsInfo>> columnMap;

    public MapValidateResultAction(Map<String, List<ContactsInfo>> columnMap) {
        this.columnMap = columnMap;
    }

    @Override
    public boolean actionHandle() {
        if(this.columnMap == null){
            return true; // pass status
        }

        this.columnMap.forEach((x, y) ->{
            System.out.println("FieldValue:" + x);
            System.out.println(y.stream().map(contactsInfo -> contactsInfo.getDisplayName()).collect(Collectors.joining(",")));
        });

        return this.columnMap.size() == 0 ;
    }
}
