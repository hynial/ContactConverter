import com.hynial.contactconverter.biz.AbstractReader;
import com.hynial.contactconverter.biz.VcfReader;
import com.hynial.contactconverter.entity.ContactsInfo;
import com.hynial.contactconverter.util.CommonUtil;

import java.util.List;

public class VcfReaderApplication {
    public static void main(String[] args) {
        CommonUtil.logSwitch = 1;

        String vcfPath = "/Users/hynial/Downloads/刘VX.vcf";
        AbstractReader<ContactsInfo> vcfReader = new VcfReader().setInput(vcfPath);
        List<ContactsInfo> contactsInfoList = vcfReader.read();
        System.out.println("Done");
    }
}
