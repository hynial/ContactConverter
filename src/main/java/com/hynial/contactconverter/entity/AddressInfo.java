package com.hynial.contactconverter.entity;

import com.hynial.contactconverter.annotation.AliasField;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddressInfo implements Serializable {
    public static final String ADDRESS_ATTR_SEPARATOR = "/";
    private String addressType;

    @AliasField(value = "Country Code", reg = "")
    private String countryCode;
    private String street1; // 街道1
    private String street2; // 街道2
    private String district; // 地区
    private String city; // 城市
    private String state; // province
    private String country; // 国家
    private String postalCode; // 邮编

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(addressType == null ? "" : addressType).append(ADDRESS_ATTR_SEPARATOR)
                .append(country == null ? "" : country).append(ADDRESS_ATTR_SEPARATOR)
                .append(state == null ? "" : state).append(ADDRESS_ATTR_SEPARATOR)
                .append(city == null ? "" : city).append(ADDRESS_ATTR_SEPARATOR)
                .append(district == null ? "" : district).append(ADDRESS_ATTR_SEPARATOR)
                .append(street1 == null ? "" : street1).append(ADDRESS_ATTR_SEPARATOR)
                .append(street2 == null ? "" : street2).append(ADDRESS_ATTR_SEPARATOR)
                .append(postalCode == null ? "" : postalCode).append(ADDRESS_ATTR_SEPARATOR)
                .append(countryCode == null ? "" : countryCode);

        return stringBuilder.toString();
    }

    /*
    First Name,Last Name,Display Name,Nickname,E-mail Address,E-mail 2 Address,E-mail 3 Address,Home Phone,Business Phone,Home Fax,Business Fax,Pager,Mobile Phone,
    Home Street,
    Home Address 2,Home City,Home State,Home Postal Code,   Home Country,Business Address,
    Business Address 2,Business City,Business State,Business Postal Code,   Business Country,Country Code,Related name,Job Title,Department,Organization,Notes,Birthday,Anniversary,Gender,Web Page,Web Page 2,Categories
    * */
}
