package com.hynial.biz.reform;

import com.hynial.entity.ContactsInfo;
import com.hynial.util.BizUtil;
import com.hynial.util.CommonUtil;

public class ContactsDetachNameReform implements IReform<ContactsInfo> {

    @Override
    public void reform(ContactsInfo contactsInfo) {
        String lastName = contactsInfo.getLastName();
        String firstName = contactsInfo.getFirstName();
        if (CommonUtil.isEmpty(lastName) && CommonUtil.isNotEmpty(firstName)) {
            // reset first name & last name
            if (!CommonUtil.containsHanScript(firstName)) return;

            if (firstName.length() > 1) {
                String preTwoChar = firstName.substring(0, 2).trim();
                if (BizUtil.getMultiFamilyNames().contains(preTwoChar)) {
                    lastName = preTwoChar;
                    firstName = firstName.substring(2);
                } else if (!BizUtil.notFamilyNames().contains(firstName.substring(0, 1))) {
                    lastName = firstName.substring(0, 1);
                    firstName = firstName.substring(1);
                }
            }
        }

        firstName = removeToNote(firstName, contactsInfo);
        firstName = removeFirstNameSuffix(firstName);

        contactsInfo.setFirstName(firstName);
        contactsInfo.setLastName(lastName);
    }

    private String removeToNote(String firstName, ContactsInfo contactsInfo){
        String[] arr = new String[]{"老师", "体系结构", "宿舍长", "律师", "TC", "科目三", "安溪", "JesseLiu", "~同门"
                , "博士", "机械", "乒乓", "-杭电", "义工", "驾照", "353", "浙", "驾校同学", "：读经班", "心理学", "开三", "湖北黄石"};
        arr = null;
        if(arr == null) return firstName;

        for(String rms : arr) {
            if (firstName.endsWith(rms)) {
                contactsInfo.setNotes((contactsInfo.getNotes() == null ? "" : contactsInfo.getNotes()) + " " + rms);
            }
            firstName = firstName.replaceAll(rms, "");
        }

        return firstName;
    }

    private String removeFirstNameSuffix(String firstName){
//        firstName = firstName.replaceAll("长号|昆明|泉州|深圳|~服装设计|new|电信|胡大侠", "");
        return firstName;
    }
}
