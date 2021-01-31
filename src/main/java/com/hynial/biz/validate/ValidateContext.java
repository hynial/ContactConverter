package com.hynial.biz.validate;

import com.hynial.biz.validate.ivalidate.IResultAction;
import com.hynial.entity.ContactsInfo;
import lombok.Data;

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

        mobileValidateHandler.setValidateHandler(displayNameFieldValidateHandler);
        displayNameFieldValidateHandler.setValidateHandler(homePhoneFieldValidateHandler);

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
                System.out.println(String.format("[Validate:%s, pass]", x));
            }
        });

        this.passStatue = atomicBoolean.get();
    }
}
