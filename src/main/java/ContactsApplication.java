import com.hynial.biz.AbstractReader;
import com.hynial.biz.CsvReader;
import com.hynial.biz.VcfReader;
import com.hynial.biz.buildimpl.CsvBuilder;
import com.hynial.biz.buildimpl.VcfBuilder;
import com.hynial.biz.duplicate.CsvDuplicate;
import com.hynial.biz.ibuild.Builder;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.CommonUtil;

import java.io.File;
import java.util.List;

/**
 * Virtual arguments[-D] should be put before -jar arg.
 * java -Dvcf=input.vcf -Dcsv=output.csv -Dact=v2c -Dlog=(1|0) -jar ContactsApplication.jar
 * java "-Dvcf=/Users/hynial/Downloads/iCloud vCard.vcf" -Dcsv=./1.csv -Dact=v2c -Dlog=0 -jar /Users/hynial/IdeaProjects/ContactConverter/target/ContactConverter-1.0-SNAPSHOT.jar
 */
public class ContactsApplication {
    private static String VCF_TO_CSV = "v2c";
    private static String CSV_TO_VCF = "c2v";
    public static void main(String[] args) {
        String workDir = System.getProperty("user.dir");
        System.out.println("CurrentWorkingDirectory:" + workDir);

        String vcfInputPath = workDir + File.separator + "input.vcf";
        String csvInputPath = workDir + File.separator + "input.csv";
        String vcfOutputPath = workDir + File.separator + "output.vcf";
        String csvOutputPath = workDir + File.separator + "output.csv";

        String vcf = System.getProperty("vcf");
        String csv = System.getProperty("csv");
        String action = System.getProperty("act");
        if(System.getProperty("log") != null) {
            int logSwitch = Integer.parseInt(System.getProperty("log"));
            CommonUtil.logSwitch = logSwitch;
        }

        if(action == null || (!action.equalsIgnoreCase(VCF_TO_CSV) && !action.equalsIgnoreCase(CSV_TO_VCF))){
//            action = VCF_TO_CSV;
            action = CSV_TO_VCF;
        }

        if(vcf != null){
            System.out.println("vcf:" + vcf);
        }

        if(csv != null){
            System.out.println("csv:" + csv);
        }

        CsvDuplicate csvDuplicate = new CsvDuplicate(csvInputPath);
        csvDuplicate.printDuplicates("Display Name");
//        csvDuplicate.printDuplicates("Last Name");
//        csvDuplicate.printDuplicates("First Name");
//        csvDuplicate.printDuplicates("Mobile Phone 2");
//        csvDuplicate.printDuplicates("E-mail 1");

        boolean exec = true;
        exec = false;
        if(exec) {

            if (action.equalsIgnoreCase(VCF_TO_CSV)) {
                String vcfPath = vcfInputPath;
                String csvPath = csvOutputPath;

                if (vcf != null) {
                    vcfPath = vcf;
                }
                if (csv != null) {
                    csvPath = csv;
                }

                vcf2csv(vcfPath, csvPath);
            } else if (action.equalsIgnoreCase(CSV_TO_VCF)) {
                String vcfPath = vcfOutputPath;
                String csvPath = csvInputPath;

                if (vcf != null) {
                    vcfPath = vcf;
                }
                if (csv != null) {
                    csvPath = csv;
                }

                csv2vcf(csvPath, vcfPath);
            }
        }

    }

    private static void vcf2csv(String vcfPath, String csvPath){
        AbstractReader<ContactsInfo> vcfReader = new VcfReader().setInput(vcfPath);
        List<ContactsInfo> contactInfoList = vcfReader.read();

        Builder csvBuilder = csvPath == null ? new CsvBuilder(contactInfoList) : new CsvBuilder(contactInfoList, csvPath);
        csvBuilder.build();
    }

    private static void csv2vcf(String csvPath, String vcfPath){
        AbstractReader<ContactsInfo> csvReader = new CsvReader().setInput(csvPath);
        List<ContactsInfo> contactsInfoList = csvReader.read();

        Builder vcfBuilder = vcfPath == null ? new VcfBuilder(contactsInfoList) : new VcfBuilder(contactsInfoList, vcfPath);
        vcfBuilder.build();
    }
}
