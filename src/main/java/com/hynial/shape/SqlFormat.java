package com.hynial.shape;

import com.hynial.constant.InsertPrefix;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.entity.SqlFileExportPaths;
import com.hynial.util.CommonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;

@Data
@NoArgsConstructor
public class SqlFormat {
    public static final String LINE_SEPARATOR = "\n";

    private List<ContactsInfo> contactsInfoList;
    private SqlFileExportPaths sqlFileExportPaths;

    public SqlFormat(List<ContactsInfo> contactsInfoList, SqlFileExportPaths sqlFileExportPaths) {
        this.contactsInfoList = contactsInfoList;
        this.sqlFileExportPaths = sqlFileExportPaths;
    }

    public void shape() {
        if(Files.notExists(Paths.get(this.sqlFileExportPaths.getSqlPathContact()).getParent())){
            new File(this.sqlFileExportPaths.getSqlPathContact()).getParentFile().mkdirs();
        }
        CommonUtil.writeFile(this.sqlFileExportPaths.getSqlPathContact(), "");
        CommonUtil.writeFile(this.sqlFileExportPaths.getSqlPathTelephone(), "");
        CommonUtil.writeFile(this.sqlFileExportPaths.getSqlPathEmail(), "");
        CommonUtil.writeFile(this.sqlFileExportPaths.getSqlPathAddress(), "");
        CommonUtil.writeFile(this.sqlFileExportPaths.getSqlPathRelated(), "");

        int initialContactId = 10;
        int initialEmailId = 10;
        int initialRelatedId = 10;
        int initialAddressId = 10;
        int initialTelephoneId = 10;
        for (ContactsInfo c : this.contactsInfoList) {
            // Contact
            String contact = MessageFormat.format(InsertPrefix.CONTACT + "",
                    initialContactId,
                    c.getMobilePhones() != null && c.getMobilePhones().size() > 0 ? c.getMobilePhones().get(0) : null,
                    c.getAddressInfoList() != null && c.getAddressInfoList().size() > 0 ? c.getAddressInfoList().get(0).toString().replaceAll("//", "") : null,
                    c.getEmails() != null && c.getEmails().size() > 0 ? c.getEmails().get(0) : null,
                    c.getFirstName(),
                    c.getLastName(),
                    c.getDisplayName(),
                    c.getNickName(),
                    c.getJobTitle(),
                    c.getDepartment(),
                    c.getOrganization(),
                    c.getNotes(),
                    c.getBirthday(),
                    c.getLunarBirthday(),
                    c.getWechat(),
                    c.getQq(),
                    c.getGender() != null ? (c.getGender().equals("男") ? "M" : "F") : null,
                    c.getReviseTime());

            contact = contact.replaceAll("'null'", "null");
            contact += LINE_SEPARATOR;

            CommonUtil.appendFile(this.sqlFileExportPaths.getSqlPathContact(), contact);

            // Telephone
            List<String> mobilePhones = c.getMobilePhones();
            if (mobilePhones != null && mobilePhones.size() > 0) {
                for (String telephone : mobilePhones) {
                    String phoneType = "M"; // Mobile
                    if (telephone.startsWith("0")) {
                        phoneType = "H"; // HOME
                    }
                    if(telephone.length() <= 6){
                        phoneType = "S"; // Short number
                    }
                    String insertTele = MessageFormat.format(InsertPrefix.TELEPHONE,
                            initialTelephoneId,
                            initialContactId,
                            telephone,
                            phoneType);

                    insertTele += LINE_SEPARATOR;
                    insertTele = insertTele.replaceAll("'null'", "null");
                    CommonUtil.appendFile(this.sqlFileExportPaths.getSqlPathTelephone(), insertTele);
                    initialTelephoneId++;
                }
            }

            // Related
            String related = c.getRelatedName();
            if (CommonUtil.isNotEmpty(related)) {
                String[] relatedArray = related.replaceAll("\"", "").split("\\n");
                for (int i = 0; i < relatedArray.length; i++) {
                    String[] keyValue = relatedArray[i].split(":");
                    if (keyValue != null && keyValue.length > 1) {
                        String relatedInsert = MessageFormat.format(InsertPrefix.RELATED,
                                initialRelatedId,
                                initialContactId,
                                keyValue[0],
                                keyValue[1]);
                        relatedInsert += LINE_SEPARATOR;
                        relatedInsert = relatedInsert.replaceAll("'null'", "null");
                        CommonUtil.appendFile(this.sqlFileExportPaths.getSqlPathRelated(), relatedInsert);

                        initialRelatedId++;
                    }
                }
            }

            // Email
            List<String> emails = c.getEmails();
            if (emails != null && emails.size() > 0) {
                for (String email : emails) {
                    String insertEmail = MessageFormat.format(InsertPrefix.EMAIL,
                            initialEmailId,
                            initialContactId,
                            email);
                    insertEmail += LINE_SEPARATOR;
                    insertEmail = insertEmail.replaceAll("'null'", "null");
                    CommonUtil.appendFile(this.sqlFileExportPaths.getSqlPathEmail(), insertEmail);

                    initialEmailId++;
                }
            }

            // Address
            List<AddressInfo> addressInfoList = c.getAddressInfoList();
            if (addressInfoList != null && addressInfoList.size() > 0) {
                for (AddressInfo addressInfo : addressInfoList) {
                    String address;
                    // display_address,address_type,latitude,longitude,country_code,street1,street2,district,city,state,country,postal_code
                    String displayAddress = addressInfo.getState() + "" + addressInfo.getCity() + addressInfo.getDistrict() + addressInfo.getStreet1() + addressInfo.getStreet2();
                    String addressType = "H"; // home, Rent, Organization
                    address = MessageFormat.format(InsertPrefix.ADDRESS,
                            initialAddressId,
                            initialContactId,
                            displayAddress,
                            addressType,
                            null, null,
                            addressInfo.getCountryCode(),
                            addressInfo.getStreet1(),
                            addressInfo.getStreet2(),
                            addressInfo.getDistrict(),
                            addressInfo.getCity(),
                            addressInfo.getState(),
                            addressInfo.getCountry(),
                            addressInfo.getPostalCode());

                    address += LINE_SEPARATOR;
                    address = address.replaceAll("'null'", "null");
                    CommonUtil.appendFile(this.sqlFileExportPaths.getSqlPathAddress(), address);

                    initialAddressId++;
                }
            }

            initialContactId++;
        }
    }

}
