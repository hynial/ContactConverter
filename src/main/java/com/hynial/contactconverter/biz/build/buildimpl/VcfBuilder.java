package com.hynial.contactconverter.biz.build.buildimpl;

import com.hynial.contactconverter.biz.build.Builder;
import com.hynial.contactconverter.biz.validate.ValidateContext;
import com.hynial.contactconverter.entity.ContactsInfo;
import com.hynial.contactconverter.shape.VcfFormat;
import com.hynial.contactconverter.util.CommonUtil;
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

    private ValidateContext validateContext;

    @Override
    public boolean validateBeforeBuild() {
        validateContext = new ValidateContext(this.contactsInfoList);
        validateContext.validateAction();

        if(!validateContext.isPassStatue()){
            throw new RuntimeException("ValidateExceptionOccurredBeforeBuildVcf");
        }

        return true;
    }

    @Override
    public void build() {
        VcfFormat vcfFormat = new VcfFormat();
        String result = vcfFormat.shapes(contactsInfoList);

        System.out.println("Output:" + output);
        System.out.println("Total deal:" + contactsInfoList.size());
        CommonUtil.writeFile(output, result);
    }
}
