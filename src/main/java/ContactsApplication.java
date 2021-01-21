import com.hynial.entity.AddressType;
import com.hynial.util.PropertyUtil;

public class ContactsApplication {
    public static void main(String[] args) {
        AddressType addressType = AddressType.HOME;
        System.out.println(addressType.name());

        for(AddressType type : addressType.values()){
            System.out.println(type.name());
        }

        System.out.println(PropertyUtil.getValue("headCount"));
        System.out.println(PropertyUtil.getValue("headTitles"));
    }
}
