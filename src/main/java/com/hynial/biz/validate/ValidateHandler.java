package com.hynial.biz.validate;

import com.hynial.biz.validate.ivalidate.IResultAction;
import com.hynial.biz.validate.ivalidate.IValidate;

import java.util.*;

public abstract class ValidateHandler implements IValidate {
    // 是否做自己
    protected boolean doSelf = true;

    protected ValidateHandler validateHandler;

    public ValidateHandler getValidateHandler() {
        return validateHandler;
    }

    public void setValidateHandler(ValidateHandler validateHandler) {
        this.validateHandler = validateHandler;
    }

    // 收集结果集
    protected static Map<String, IResultAction> resultActionMap = new LinkedHashMap<>();

    // validate chain action
    protected void deliverValidate(){
        // first do self validate
        if(this.doSelf){
            this.validate();
        }

        if(this.validateHandler != null){
            // next validate
            this.validateHandler.resultActionMap = this.resultActionMap;
            this.validateHandler.deliverValidate();
        }else{
            // chain end

        }
    }
}
