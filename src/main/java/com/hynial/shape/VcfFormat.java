package com.hynial.shape;

import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.CommonUtil;

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
        String name = String.format("N:%s;%s;;;", contactsInfo.getLastName() == null? "" : contactsInfo.getLastName(), contactsInfo.getFirstName() == null ? "" : contactsInfo.getFirstName());
        lineFields.add(name);
        String fullName = String.format("FN:%s", contactsInfo.getDisplayName() == null ? "" : contactsInfo.getDisplayName());
        lineFields.add(fullName);

        if(contactsInfo.getNickName() != null) {
            lineFields.add(String.format("NICKNAME:%s", contactsInfo.getNickName()));
        }
        if(contactsInfo.getOrganization() != null || contactsInfo.getDepartment() != null) {
            lineFields.add(String.format("ORG:%s;%s", contactsInfo.getOrganization() == null ? "" : contactsInfo.getOrganization(), contactsInfo.getDepartment() == null ? "" : contactsInfo.getDepartment()));
        }
        if(contactsInfo.getJobTitle() != null) {
            lineFields.add(String.format("TITLE:%s", contactsInfo.getJobTitle()));
        }

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
        List<AddressInfo> addressInfoList = contactsInfo.getAddressInfoList();
        if(addressInfoList != null && addressInfoList.size() > 0){
            for (int i = 0; i < addressInfoList.size(); i++) {
                lineFields.add(String.format("item%d.ADR;type=HOME" + (i == 0? ";type=pref" : "") + ":;;%s\\n%s;%s;%s;%s;%s", itemIndex,
                        addressInfoList.get(i).getStreet1() == null ? "" : addressInfoList.get(i).getStreet1(), addressInfoList.get(i).getStreet2(),
                        addressInfoList.get(i).getCity(), addressInfoList.get(i).getState(),
                        addressInfoList.get(i).getPostalCode(), addressInfoList.get(i).getCountry()));
                lineFields.set(lineFields.size() - 1, lineFields.get(lineFields.size() - 1).replaceAll("null", ""));
                if(addressInfoList.get(i).getStreet1() == null || addressInfoList.get(i).getStreet2() == null){
                    lineFields.set(lineFields.size() - 1, lineFields.get(lineFields.size() - 1).replace("\\n", "")); // replaceAll not work, regex not recognize \n
                }
                if(addressInfoList.get(i).getAddressType() != null) {
                    lineFields.add(String.format("item%d.X-ABLabel:%s", itemIndex, addressInfoList.get(i).getAddressType()));
                }
                if(addressInfoList.get(i).getCountryCode() != null) {
                    lineFields.add(String.format("item%d.X-ABADR:%s", itemIndex, addressInfoList.get(i).getCountryCode()));
                }
                if(addressInfoList.get(i).getDistrict() != null) {
                    lineFields.add(String.format("item%d.X-APPLE-SUBLOCALITY:%s", itemIndex, addressInfoList.get(i).getDistrict()));
                }
                itemIndex++;
            }
        }

        lineFields.add(String.format("NOTE:%s", contactsInfo.getNotes() == null ? "" : contactsInfo.getNotes()));
        if(contactsInfo.getBirthday() != null) {
            lineFields.add(String.format("BDAY;value=date:%s", contactsInfo.getBirthday()));
        }
        if(contactsInfo.getLunarBirthday() != null) {
            lineFields.add(String.format("X-ALTBDAY;CALSCALE=chinese:%s", CommonUtil.getLunarBirthdayLong(contactsInfo.getLunarBirthday())));
        }

        if(contactsInfo.getWechat() != null) {
            lineFields.add(String.format("item%d.IMPP;X-SERVICE-TYPE=微信号" + ";type=pref" + ":x-apple:%s", itemIndex, contactsInfo.getWechat()));
            lineFields.add(String.format("item%d.X-ABLabel:微信号", itemIndex++));
        }

        if(contactsInfo.getQq() != null){
            lineFields.add(String.format("item%d.IMPP;X-SERVICE-TYPE=QQ:x-apple:%s", itemIndex, contactsInfo.getQq()));
            lineFields.add(String.format("item%d.X-ABLabel:QQ", itemIndex++));
        }

//        X-SOCIALPROFILE;type=sinaweibo;x-user=http://2.com:http://weibo.com/n/http%
//        3A//2.com
//        X-SOCIALPROFILE;type=twitter;x-user=http://www.twittercom:http://twitter.co
//        m/http%3A//www.twittercom
//        X-SOCIALPROFILE;x-user=https://m.baidu.com:x-apple:https%3A//m.baidu.com
        List<String> webpages = contactsInfo.getWebPageList();
        if(webpages != null && webpages.size() > 0){
            for (int i = 0; i < webpages.size(); i++) {
                lineFields.add(String.format("X-SOCIALPROFILE;x-user=%s:x-apple:%s", webpages.get(i), webpages.get(i).replaceAll(":", "%3A")));
            }
        }

//        item7.X-ABDATE;type=pref:1604-01-24
//        item7.X-ABLabel:_$!<Anniversary>!$_
//        item8.X-ABDATE:1604-01-25
//        item8.X-ABLabel:_$!<Other>!$_
//        String anniversary = contactsInfo.getAnniversary();
//        List<String> interior = List.of("Anniversary", "Other");
//        if(anniversary != null && anniversary.length() > 0){
//            String[] ans = anniversary.replaceAll("\"", "").split("\\n");
//            if(ans != null && ans.length > 0){
//                boolean firstFlag = true;
//                for (int i = 0; i < ans.length; i++) {
//                    if(ans[i] == null) continue;
//                    String[] tmps = ans[i].split(":");
//                    if(tmps != null && tmps.length > 1){
//                        if(firstFlag) {
//                            lineFields.add(String.format("item%d.X-ABDATE;type=pref:%s", itemIndex, tmps[1]));
//                            firstFlag = false;
//                        }else{
//                            lineFields.add(String.format("item%d.X-ABDATE:%s", itemIndex, tmps[1]));
//                        }
//                        lineFields.add(String.format("item%d.X-ABLabel:%s", itemIndex++, interior.contains(tmps[1]) ? "_$!<" + tmps[1] + "!$_" : tmps[1]));
//                    }
//                }
//            }
//        }
        itemIndex = mergeIntoVcf(lineFields, List.of("Anniversary", "Other"), contactsInfo.getAnniversary(), "item%d.X-ABLabel:%s", "item%d.X-ABDATE;type=pref:%s", "item%d.X-ABDATE:%s", itemIndex);
//        item8.X-ABRELATEDNAMES;type=pref:芳
//        item8.X-ABLabel:_$!<Father>!$_
//        item9.X-ABRELATEDNAMES:谁
//        item9.X-ABLabel:_$!<Mother>!$_
        itemIndex = mergeIntoVcf(lineFields, List.of("Father", "Mother"), contactsInfo.getRelatedName(), "item%d.X-ABLabel:%s", "item%d.X-ABRELATEDNAMES;type=pref:%s", "item%d.X-ABRELATEDNAMES:%s", itemIndex);

        joinStringBuilder(stringBuilder, lineFields, LINE_SEPARATOR);
        stringBuilder.append(END);
        return stringBuilder.toString();
    }

    private int mergeIntoVcf(List<String> lineFields, List<String> interiors, String textBlock, String key, String value1, String value2, int itemIndex){
        if(textBlock != null && textBlock.length() > 0){
            String[] ans = textBlock.replaceAll("\"", "").split("\\n");
            if(ans != null && ans.length > 0){
                boolean firstFlag = true;
                for (int i = 0; i < ans.length; i++) {
                    if(ans[i] == null) continue;
                    String[] tmps = ans[i].split(":");
                    if(tmps != null && tmps.length > 1){
                        if(firstFlag) {
                            lineFields.add(String.format(value1, itemIndex, tmps[1]));
                            firstFlag = false;
                        }else{
                            lineFields.add(String.format(value2, itemIndex, tmps[1]));
                        }
                        lineFields.add(String.format(key, itemIndex++, interiors.contains(tmps[0]) ? "_$!<" + tmps[0] + "!$_" : tmps[0]));
                    }
                }
            }
        }

        return itemIndex;
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
