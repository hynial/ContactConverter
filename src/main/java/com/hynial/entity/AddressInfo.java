package com.hynial.entity;

import com.hynial.annotation.AliasField;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddressInfo implements Serializable {
    private AddressType addressType = AddressType.HOME;

    @AliasField(value = "Country Code")
    private String countryCode;
    private String street;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(addressType.name()).append(" Address: ")
                .append(country).append(state).append(city).append(street).append(address)
                .append(",PostalCode: ").append(postalCode);
        return stringBuilder.toString();
    }

    /*
    First Name,Last Name,Display Name,Nickname,E-mail Address,E-mail 2 Address,E-mail 3 Address,Home Phone,Business Phone,Home Fax,Business Fax,Pager,Mobile Phone,
    Home Street,
    Home Address 2,Home City,Home State,Home Postal Code,   Home Country,Business Address,
    Business Address 2,Business City,Business State,Business Postal Code,   Business Country,Country Code,Related name,Job Title,Department,Organization,Notes,Birthday,Anniversary,Gender,Web Page,Web Page 2,Categories
    * */
}
