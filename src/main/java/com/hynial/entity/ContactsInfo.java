package com.hynial.entity;

import com.hynial.annotation.AliasField;
import com.hynial.biz.duplicate.filter.IContactFilter;
import com.hynial.util.BizUtil;
import com.hynial.util.CommonUtil;
import lombok.Data;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ContactsInfo implements Serializable {
    @AliasField(value = "First Name", reg = "[^F]N:[^;]*?;(?<firstName>[^;\\^]*?);")
    private String firstName;

    @AliasField(value = "Last Name", reg = "[^F]N:(?<lastName>[^;\\^]*?);")
    private String lastName;

    @AliasField(value = "Display Name", reg = ".*FN:([^\\^]*)\\^")
    private String displayName;

    @AliasField(value = "Nickname", reg = "NICKNAME:([^\\^]*)\\^")
    private String nickName;

    @AliasField(value = "E-mail", reg = "EMAIL;type=INTERNET(?:;type=(?:pref|HOME|WORK|[a-zA-Z]{1,5})|):(.+?@.+?)\\^")
    private List<String> emails;

    @AliasField(value = "Home Phone", reg = "item1.TEL;type=pref:([\\d| ]*)")
    private String homePhone;

    @AliasField(value = "Business Phone")
    private String businessPhone;

    @AliasField(value = "Home Fax")
    private String homeFax;

    @AliasField(value = "Business Fax")
    private String businessFax;

    @AliasField(value = "Pager")
    private String pager;

    // https://stackoverflow.com/questions/4389644/regex-to-match-string-containing-two-names-in-any-order
    // @AliasField(value = "Mobile Phone", reg = "TEL;type=(?:CELL|HOME|WORK);type=VOICE(?:;type=pref|):([\\d| ]*)")
    @AliasField(value = "Mobile Phone", reg = "TEL;TYPE=(?:CELL|HOME|WORK|mobile)(?:;TYPE=pref|;TYPE=VOICE|)(?:;TYPE=pref|;TYPE=VOICE|):([\\d \\-\\+]*)")
    private List<String> mobilePhones;

    @AliasField(value = "Address", reg = "ADR;type=HOME(?:;type=pref|):([^\\^]+?)\\^")
    private List<AddressInfo> addressInfoList;

    @AliasField(value = "Revise Time", reg = "REV:([^\\^]+?)\\^") // +8 Calendar.toInstant().toString()
    private String reviseTime;

    @AliasField(value = "Related Name", reg = "X-ABLabel:([^\\^]*?)\\^,X-ABRELATEDNAMES(?:;type=pref|):([^\\^]*?)\\^")
    private String relatedName;

    @AliasField(value = "Job Title", reg = "TITLE:([^\\^]+?)\\^")
    private String jobTitle;

    @AliasField(value = "Department", reg = "ORG:(?:[^\\^;]*);([^\\^]*?)\\^")
    private String department;

    @AliasField(value = "Organization", reg = "ORG:([^;\\^]*?)\\^")
    private String organization;

    @AliasField(value = "Notes", reg = "NOTE:([^\\^]*?)\\^")
    private String notes;

    @AliasField(value = "Birthday", reg = "BDAY;value=date:([^\\^]*?)\\^")
    private String birthday;

    @AliasField(value = "Lunar Birthday", reg = "X-ALTBDAY;CALSCALE=chinese:([^\\^]*?)\\^")
    private String lunarBirthday;

    @AliasField(value = "Wechat", reg = "IMPP;X-SERVICE-TYPE=微信号(?:;type=pref|):x-apple:([^\\^]*?)\\^")
    private String wechat;

    @AliasField(value = "QQ", reg = "IMPP;X-SERVICE-TYPE=QQ(?:;type=pref|):x-apple:([^\\^]*?)\\^")
    private String qq;

    @AliasField(value = "Anniversary", reg = "X-ABLabel:([^\\^]*?)\\^,X-ABDATE(?:;type=pref|):([^\\^]*?)\\^") // merge fields
    private String anniversary;

    @AliasField(value = "Gender")
    private String gender;

    @AliasField(value = "Web Page", reg = "X-SOCIALPROFILE(?:;type=(?:[^;]*?)|);x-user=((?:http|https|wap|[a-zA-Z]{1,5}):\\/\\/[^:]*?):")
    private List<String> webPageList;

    @AliasField(value = "Categories")
    private String categories;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            //将对象写到流里
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            //从流里读回来
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            return null;
        }
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lastName).append(",").append(firstName)
                .append(",").append(displayName)
                .append(",").append(nickName);

        if (emails != null) {
            for (String e : emails) {
                stringBuilder.append(",").append(e);
            }
        }
        stringBuilder.append(",").append(homePhone)
                .append(",").append(businessPhone)
                .append(",").append(homeFax)
                .append(",").append(businessFax)
                .append(",").append(pager);

        if(mobilePhones != null) {
            for(String m : mobilePhones){
                stringBuilder.append(",").append(m);
            }
        }

        if(addressInfoList != null) {
            for (AddressInfo addressInfo : addressInfoList) {
                stringBuilder.append(",").append(addressInfo.toString());
            }
        }

        stringBuilder.append(",").append(relatedName)
                .append(",").append(jobTitle)
                .append(",").append(department)
                .append(",").append(organization)
                .append(",").append(notes)
                .append(",").append(birthday)
                .append(",").append(lunarBirthday)
                .append(",").append(wechat)
                .append(",").append(qq)
                .append(",").append(anniversary)
                .append(",").append(gender);

        if(webPageList != null) {
            for (String webPage : webPageList) {
                stringBuilder.append(",").append(webPage);
            }
        }

        stringBuilder.append(",").append(categories).append(",").append(reviseTime);
        return stringBuilder.toString();
    }

    public static Map<String, Field> getAliasMap(){
        Map<String, Field> aliasMap = new HashMap<>();
        Field[] fields = ContactsInfo.class.getDeclaredFields();
        for(Field field : fields){
            AliasField aliasField = field.getAnnotation(AliasField.class);
            if(aliasField != null){
                aliasMap.put(aliasField.value(), field);
            }
        }

        return aliasMap;
    }

    public Object getValueByAlias(String alias) throws IllegalAccessException {
        Field field = getAliasMap().get(alias);
        return field.get(this);
    }

    public String getStringByAlias(String aliasOpt) throws IllegalAccessException {
        String[] aliasInd = aliasOpt.trim().split(BizUtil.REG_INDEX, -1);
        String alias = aliasOpt;
        int ind = -1;
        if (aliasInd.length > 1){
            alias = aliasInd[0];
            ind = Integer.parseInt(aliasOpt.replace(alias, "").trim());
        }

        Object value = this.getValueByAlias(alias);
        if (value instanceof String) { // null
            return (String) value;
        } else if(value instanceof List<?>){
            ParameterizedType parameterizedType = (ParameterizedType) ContactsInfo.getAliasMap().get(alias).getGenericType();
            Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            if (parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                List<String> vals = (List<String>) value;
                if(vals != null && ind != -1 && ind - 1  < vals.size()){
                    String val = vals.get(ind - 1);
                    return val;
                }
            } else if (parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)) {
                List<AddressInfo> vals = (List<AddressInfo>) value;
                // TODO
                throw new RuntimeException("TODO-SupportedType:" + AddressInfo.class.getSimpleName());
            }else{
                throw new RuntimeException("UnsupportedType");
            }
        }else{
            if(null == value){
                System.out.println("null value happened!");
            }
            throw new RuntimeException("UnsupportedTypeWhenGetValueFromAlias:" + alias);
        }

        return null;
    }

    public void merge(ContactsInfo contactsInfo){
        if(CommonUtil.isEmpty(this.firstName)) {
            this.firstName = contactsInfo.getFirstName();
        }

        if(CommonUtil.isEmpty(this.lastName)) {
            this.lastName = contactsInfo.getLastName();
        }

        if(CommonUtil.isEmpty(this.homePhone)) {
            this.homePhone = contactsInfo.getHomePhone();
        }

        if(contactsInfo.getMobilePhones() != null){
            for(String number : contactsInfo.getMobilePhones()){
                if(CommonUtil.isEmpty(number)) continue;

                if(this.mobilePhones == null){
                    this.mobilePhones = new ArrayList<>();
                }

                if(!this.mobilePhones.contains(number)){
                    this.mobilePhones.add(number);
                }
            }
        }

        if(CommonUtil.isEmpty(this.wechat)){
            this.wechat = contactsInfo.getWechat();
        }

        if(CommonUtil.isEmpty(this.qq)){
            this.qq = contactsInfo.getQq();
        }

        if(CommonUtil.isEmpty(this.nickName)){
            this.nickName = contactsInfo.getNickName();
        }

        if(CommonUtil.isEmpty(this.organization)){
            this.organization = contactsInfo.getOrganization();
        }

        if(CommonUtil.isEmpty(this.department)){
            this.department = contactsInfo.getDepartment();
        }

        if(CommonUtil.isEmpty(this.notes)){
            this.notes = contactsInfo.getNotes();
        }

        if(CommonUtil.isEmpty(this.birthday)){
            this.birthday = contactsInfo.getBirthday();
        }

        if(CommonUtil.isEmpty(this.lunarBirthday)){
            this.lunarBirthday = contactsInfo.getLunarBirthday();
        }

        if(CommonUtil.isEmpty(this.jobTitle)){
            this.jobTitle = contactsInfo.getJobTitle();
        }

        if(CommonUtil.isEmpty(this.relatedName)){
            this.relatedName = contactsInfo.getRelatedName();
        }

        if(CommonUtil.isEmpty(this.anniversary)){
            this.anniversary = contactsInfo.getAnniversary();
        }

        if(contactsInfo.getAddressInfoList() != null){
            if(this.getAddressInfoList() == null){
                this.addressInfoList = new ArrayList<>();
            }

            this.addressInfoList.addAll(contactsInfo.getAddressInfoList());
        }

        if(contactsInfo.getEmails() != null){
            for(String email : contactsInfo.getEmails()){
                if(CommonUtil.isEmpty(email)) continue;

                if(this.emails == null){
                    this.emails = new ArrayList<>();
                }

                if(!this.emails.contains(email)){
                    this.emails.add(email);
                }
            }
        }

        if(contactsInfo.getWebPageList() != null){
            for(String page : contactsInfo.getWebPageList()){
                if(CommonUtil.isEmpty(page)) continue;

                if(this.webPageList == null){
                    this.webPageList = new ArrayList<>();
                }

                if(!this.webPageList.contains(page)){
                    this.webPageList.add(page);
                }
            }
        }


    }
}
