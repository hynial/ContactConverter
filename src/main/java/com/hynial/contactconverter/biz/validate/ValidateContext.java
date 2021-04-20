package com.hynial.contactconverter.biz.validate;

import com.hynial.contactconverter.biz.validate.ivalidate.IResultAction;
import com.hynial.contactconverter.entity.ContactsInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class ValidateContext {
    private List<ContactsInfo> contactsInfoList;
    private Map<String, IResultAction> resultContextMap;

    private boolean passStatue = true;

    public ValidateContext(List<ContactsInfo> contactsInfoList) {
        this.contactsInfoList = contactsInfoList;
    }

    public void validateAction(){
        ValidateHandler mobileValidateHandler = new ContactsMobileUniqueValidate(this.contactsInfoList);
        ValidateHandler displayNameFieldValidateHandler = new ContactsFieldUniqueValidate("Display Name", this.contactsInfoList);
        ValidateHandler homePhoneFieldValidateHandler = new ContactsFieldUniqueValidate("Home Phone", this.contactsInfoList);
        ValidateHandler combineFieldsValidateHandler = new ContactsFieldsCombineUniqueValidate(this.contactsInfoList, new ArrayList<>(Arrays.asList(new String[]{"Last Name","First Name"})));

        mobileValidateHandler.setValidateHandler(displayNameFieldValidateHandler);
        displayNameFieldValidateHandler.setValidateHandler(homePhoneFieldValidateHandler);
        homePhoneFieldValidateHandler.setValidateHandler(combineFieldsValidateHandler);

        mobileValidateHandler.deliverValidate();

        resultContextMap = ValidateHandler.resultActionMap;

        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        resultContextMap.forEach((x, y) -> {
            System.out.println(String.format("[Validate:%s]", x));
            boolean actionBool = y.actionHandle();
            if(!actionBool){
                atomicBoolean.set(false);
                System.out.println(String.format("[Validate:%s, X]", x));
            }else{
                System.out.println(String.format("[Validate:%s, Y️]", x));
            }

            System.out.println("------------------------------------------------------------");
        });

        this.passStatue = atomicBoolean.get();
    }
}
