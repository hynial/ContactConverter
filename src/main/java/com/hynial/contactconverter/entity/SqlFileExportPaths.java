package com.hynial.contactconverter.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SqlFileExportPaths {
    private String sqlPathContact;
    private String sqlPathAddress;
    private String sqlPathEmail;
    private String sqlPathRelated;
    private String sqlPathTelephone;

    public SqlFileExportPaths(String sqlPathContact, String sqlPathAddress, String sqlPathEmail, String sqlPathRelated, String sqlPathTelephone) {
        this.sqlPathContact = sqlPathContact;
        this.sqlPathAddress = sqlPathAddress;
        this.sqlPathEmail = sqlPathEmail;
        this.sqlPathRelated = sqlPathRelated;
        this.sqlPathTelephone = sqlPathTelephone;
    }
}
