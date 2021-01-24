package com.hynial.entity;

import com.hynial.annotation.AliasField;
import lombok.Data;

import java.io.*;
import java.util.List;

@Data
public class ContactsInfo implements Serializable {
    @AliasField(value = "First Name", reg = ".*[^F]N:[^;]*?;(?<firstName>[^;]*?);")
    private String firstName;

    @AliasField(value = "Last Name", reg = ".*[^F]N:(?<lastName>[^;]*?);")
    private String lastName;

    @AliasField(value = "Display Name", reg = ".*FN:([^\\^]*)\\^")
    private String displayName;

    @AliasField(value = "Nickname")
    private String nickName;

    @AliasField(value = "E-mail", reg = "EMAIL;type=INTERNET(?:;type=(?:pref|HOME|WORK)|):(.+?@.+?)\\^")
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

    @AliasField(value = "Mobile Phone", reg = "TEL;type=CELL;type=VOICE;type=pref:([\\d| ]*)")
    private String mobilePhone;

    @AliasField(value = "Address", reg = "ADR;type=HOME(?:;type=pref|):([^\\^]+?)\\^")
    private List<AddressInfo> addressInfoList;

//    @AliasField(value = "Country Code")
//    private String countryCode;

    @AliasField(value = "Related Name")
    private String relatedName;

    @AliasField(value = "Job Title")
    private String jobTitle;

    @AliasField(value = "Department")
    private String department;

    @AliasField(value = "Organization", reg = "ORG:([^;]*);")
    private String organization;

    @AliasField(value = "Notes")
    private String notes;

    @AliasField(value = "Birthday")
    private String birthday;

    @AliasField(value = "Anniversary")
    private String anniversary;

    @AliasField(value = "Gender")
    private String gender;

    @AliasField(value = "Web Page")
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
                .append(",").append(pager)
                .append(",").append(mobilePhone);

        if(addressInfoList != null) {
            for (AddressInfo addressInfo : addressInfoList) {
                stringBuilder.append(",").append(addressInfo.toString());
            }
        }

//        stringBuilder.append(",").append(countryCode)
        stringBuilder.append(",").append(relatedName)
                .append(",").append(jobTitle)
                .append(",").append(department)
                .append(",").append(organization)
                .append(",").append(notes)
                .append(",").append(birthday)
                .append(",").append(anniversary)
                .append(",").append(gender);

        if(webPageList != null) {
            for (String webPage : webPageList) {
                stringBuilder.append(",").append(webPage);
            }
        }

        stringBuilder.append(",").append(categories);
        return stringBuilder.toString();
    }
}
