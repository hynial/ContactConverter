import com.hynial.biz.VcfReader;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class ContactsApplication {
    public static void main(String[] args) {

        VcfReader vcfReader = new VcfReader();
        vcfReader.read();

    }
}
