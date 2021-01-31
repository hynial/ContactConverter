package com.hynial.biz.buildimpl;

import com.hynial.biz.ibuild.Builder;
import com.hynial.biz.validate.ValidateContext;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.BizUtil;
import com.hynial.util.CommonUtil;
import com.hynial.visitor.OriginalOrderVisitor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Data
@NoArgsConstructor
public class CsvBuilder implements Builder {
    private List<ContactsInfo> contactsInfoList;
    private String output = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "output.csv";

    public CsvBuilder(List<ContactsInfo> contactsInfoList) {
        this.contactsInfoList = contactsInfoList;
    }

    public CsvBuilder(List<ContactsInfo> contactsInfoList, String output) {
        this.contactsInfoList = contactsInfoList;
        this.output = output;
    }

    private ValidateContext validateContext;

    @Override
    public boolean validateBeforeBuild() {
        validateContext = new ValidateContext(this.contactsInfoList);
        validateContext.validateAction();

        if(!validateContext.isPassStatue()){
            throw new RuntimeException("ValidateExceptionOccurredBeforeBuildCsv");
        }

        return true;
    }

    @Override
    public void build() {
        String titles = BizUtil.getAllHeadTitles();
        OriginalOrderVisitor order = new OriginalOrderVisitor();
        String result = "";
        for (ContactsInfo contactsInfo : contactsInfoList) {
            String line = order.v(contactsInfo);
            result += line + "\n";
        }

        System.out.println("Output:" + output);
        System.out.println("Total deal:" + contactsInfoList.size());
        CommonUtil.writeFileWithBom(output, titles + "\n" + result);
    }
}
