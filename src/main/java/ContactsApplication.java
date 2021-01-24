import com.hynial.biz.VcfReader;
import com.hynial.entity.AddressType;
import com.hynial.util.PropertyUtil;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactsApplication {
    public static void main(String[] args) {
//        AddressType addressType = AddressType.HOME;
//        System.out.println(addressType.name());
//
//        for(AddressType type : addressType.values()){
//            System.out.println(type.name());
//        }
//
//        System.out.println(PropertyUtil.getValue("headCount"));
//        System.out.println(PropertyUtil.getValue("headTitles"));
//
//        String[] headTitles = Arrays.stream(PropertyUtil.getValue("headTitles").split(",")).map(title -> title.trim()).toArray(String[]::new);
//        Arrays.sort(headTitles);
//        System.out.println(Arrays.toString(headTitles));

        VcfReader vcfReader = new VcfReader();
        vcfReader.read();

//        Pattern pattern = Pattern.compile("EMAIL;type=INTERNET(?:;type=(?:pref|HOME|WORK)|):(.+?@.+?)\\^", Pattern.CASE_INSENSITIVE);
//        String reg = "^^^item1.EMAIL;type=INTERNET;type=pref:657696155@qq.com^^^item1.X-ABLabel:邮箱^^^EMAIL;type=INTERNET;type=HOME:657696155@qq.com^^^EMAIL;type=INTERNET;type=WORK:sj@qq.com^^^item2.EMAIL;type=INTERNET:ss@s.com^^^item2.X-ABLabel:_$!<School>!$_^^^item3.EMAIL;type=INTERNET:i@i.con^^^item3.X-ABLabel:iCloud^^^item4.EMAIL;type=INTERNET:hj@s.com^^^item4.X-ABLabel:_$!<Other>!$_";
//        Matcher matcher = pattern.matcher(reg);
//        while(matcher.find()){
//            System.out.println(matcher.groupCount());
//            System.out.println(matcher.group(0));
//            System.out.println(matcher.group(1));
//        }
    }
}
