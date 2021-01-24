package com.hynial.shape;

import com.hynial.entity.ContactsInfo;

import java.util.ArrayList;
import java.util.List;

public class VcfFormat {
    private static final String LINE_SEPARATOR = "\n";
    private static final String START = "BEGIN:VCARD";
    private static final String VERSION = "VERSION:3.0";
    private static final String PRODID = "PRODID:-//Apple Inc.//iOS 13.6//EN";
    private static final String END = "END:VCARD";

    private static String wrapStart = START + LINE_SEPARATOR + VERSION + LINE_SEPARATOR + PRODID + LINE_SEPARATOR;

    public String shape(ContactsInfo contactsInfo){
        StringBuilder stringBuilder = new StringBuilder(wrapStart);
        List<String> lineFields = new ArrayList<>();
        String name = String.format("N:%s;%s;;;", contactsInfo.getLastName(), contactsInfo.getFirstName());
        lineFields.add(name);
        String fullName = String.format("FN:%s", contactsInfo.getDisplayName());
        lineFields.add(fullName);

//        NICKNAME:昵称
//        ORG:公司;部门
//        TITLE:职位
        lineFields.add(String.format("NICKNAME:%s", contactsInfo.getNickName()));
        lineFields.add(String.format("ORG:%s;%s", contactsInfo.getOrganization(), contactsInfo.getDepartment()));
        lineFields.add(String.format("TITLE:%s", contactsInfo.getJobTitle()));

//        item1.EMAIL;type=INTERNET;type=pref:657696155@qq.com
//        item1.X-ABLabel:邮箱
        int itemIndex = 1;
        if(contactsInfo.getEmails() != null && contactsInfo.getEmails().size() > 0){
            for(int i = 0; i < contactsInfo.getEmails().size(); i++){
                lineFields.add(String.format("item%d.EMAIL;type=INTERNET" + (i == 0? ";type=pref" : "") + ":%s", itemIndex, contactsInfo.getEmails().get(i)));
                lineFields.add(String.format("item%d.X-ABLabel:邮箱", itemIndex));
                itemIndex++;
            }
        }

        // TEL;type=CELL;type=VOICE;type=pref:188 5715 3387
        if(contactsInfo.getMobilePhones() != null && contactsInfo.getMobilePhones().size() > 0) {
            for (int i = 0; i < contactsInfo.getMobilePhones().size(); i++) {
                lineFields.add(String.format("TEL;type=CELL;type=VOICE" + (i == 0? ";type=pref" : "") + ":%s", contactsInfo.getMobilePhones().get(i)));
            }
        }

//        item3.ADR;type=HOME;type=pref:;;街道1\nJiedao2;杭州市;浙江省;362401;中国大陆
//        item3.X-ABLabel:联系地址
//        item3.X-ABADR:cn
//        item3.X-APPLE-SUBLOCALITY:江干区

        joinStringBuilder(stringBuilder, lineFields, LINE_SEPARATOR);
        stringBuilder.append(END);
        return stringBuilder.toString();
    }

    private void joinStringBuilder(StringBuilder stringBuilder, List<String> fields, String joinChar){
        for(int i = 0; i < fields.size(); i++) {
            stringBuilder.append(fields.get(i)).append(joinChar);
        }
    }

    public String shapes(List<ContactsInfo> contactsInfoList){
        String result = "";

        for (ContactsInfo contactsInfo : contactsInfoList) {
            String item = this.shape(contactsInfo);
            result += item + LINE_SEPARATOR;
        }

        return result;
    }
}
