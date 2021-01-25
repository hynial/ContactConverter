package com.hynial.biz.buildimpl;

import com.hynial.biz.ibuild.Builder;
import com.hynial.entity.ContactsInfo;
import com.hynial.shape.VcfFormat;
import com.hynial.util.CommonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;
import java.util.List;

@Data
@NoArgsConstructor
public class VcfBuilder implements Builder {
    private List<ContactsInfo> contactsInfoList;
    private String output = Paths.get(".").toAbsolutePath().normalize().toString();

    public VcfBuilder(List<ContactsInfo> contactsInfoList) {
        this.contactsInfoList = contactsInfoList;
    }

    public VcfBuilder(List<ContactsInfo> contactsInfoList, String output) {
        this.contactsInfoList = contactsInfoList;
        this.output = output;
    }

    @Override
    public void build() {
        VcfFormat vcfFormat = new VcfFormat();
        String result = vcfFormat.shapes(contactsInfoList);
        CommonUtil.writeFile(output, result);
    }
}
