import com.hynial.biz.VcfReader;
import com.hynial.entity.AddressType;
import com.hynial.util.PropertyUtil;

import java.util.Arrays;

public class ContactsApplication {
    public static void main(String[] args) {
        AddressType addressType = AddressType.HOME;
        System.out.println(addressType.name());

        for(AddressType type : addressType.values()){
            System.out.println(type.name());
        }

        System.out.println(PropertyUtil.getValue("headCount"));
        System.out.println(PropertyUtil.getValue("headTitles"));

        String[] headTitles = Arrays.stream(PropertyUtil.getValue("headTitles").split(",")).map(title -> title.trim()).toArray(String[]::new);
        Arrays.sort(headTitles);
        System.out.println(Arrays.toString(headTitles));

        VcfReader vcfReader = new VcfReader();
        vcfReader.read();
    }
}
