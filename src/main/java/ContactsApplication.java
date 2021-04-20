import com.hynial.contactconverter.biz.AbstractReader;
import com.hynial.contactconverter.biz.CsvReader;
import com.hynial.contactconverter.biz.VcfReader;
import com.hynial.contactconverter.biz.build.buildimpl.CsvBuilder;
import com.hynial.contactconverter.biz.build.buildimpl.VcfBuilder;
import com.hynial.contactconverter.biz.duplicate.PureDataContext;
import com.hynial.contactconverter.biz.build.Builder;
import com.hynial.contactconverter.biz.reform.ContactsReformContext;
import com.hynial.contactconverter.entity.ContactsInfo;
import com.hynial.contactconverter.entity.SqlFileExportPaths;
import com.hynial.contactconverter.shape.SqlFormat;
import com.hynial.contactconverter.util.CommonUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Virtual arguments[-D] should be put before -jar arg.
 * java -Dvcf=input.vcf -Dcsv=output.csv -Dact=v2c -Dlog=(1|0) -jar ContactsApplication.jar
 * java "-Dvcf=/Users/hynial/Downloads/iCloud vCard.vcf" -Dcsv=./1.csv -Dact=v2c -Dlog=0 -jar /Users/hynial/IdeaProjects/ContactConverter/target/ContactConverter-1.0-SNAPSHOT.jar
 */
public class ContactsApplication {
    private static String VCF_TO_CSV = "v2c";
    private static String CSV_TO_VCF = "c2v";
    private static String CSV_TO_CSV = "c2c";
    private static String VCF_TO_VCF = "v2v";
    private static String VCF_TO_SQL = "v2s";

    private static String workDir = System.getProperty("user.dir");

    private static String vcfInputPath = workDir + File.separator + "input.vcf";
    private static String csvInputPath = workDir + File.separator + "input.csv";
    private static String vcfOutputPath = workDir + File.separator + "output.vcf";
    private static String csvOutputPath = workDir + File.separator + "output.csv";

    public static void main(String[] args) {
        System.out.println("CurrentWorkingDirectory:" + workDir);

        String vcf = System.getProperty("vcf");
        String csv = System.getProperty("csv");
        String action = System.getProperty("act");
        if(System.getProperty("log") != null) {
            int logSwitch = Integer.parseInt(System.getProperty("log"));
            CommonUtil.logSwitch = logSwitch;
        }

        if(action == null || (!action.equalsIgnoreCase(VCF_TO_CSV) && !action.equalsIgnoreCase(CSV_TO_VCF))){
//            action = VCF_TO_CSV;
//            action = CSV_TO_VCF;
//            action = VCF_TO_VCF;
//            action = CSV_TO_CSV;
            action = VCF_TO_SQL;
        }

        if(vcf != null){
            System.out.println("vcf:" + vcf);
        }

        if(csv != null){
            System.out.println("csv:" + csv);
        }

        boolean exec = true;
//        exec = false;
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
            } else if(action.equalsIgnoreCase(VCF_TO_VCF)){
                String vcfPath = vcfInputPath;
                if (vcf != null) {
                    vcfPath = vcf;
                }

                vcf2vcf(vcfPath);
            } else if(action.equalsIgnoreCase(CSV_TO_CSV)){
                String csvPath = csvInputPath;
                if (csv != null) {
                    csvPath = csv;
                }

                csv2csv(csvPath);
            } else if(action.equalsIgnoreCase(VCF_TO_SQL)){
                String vcfPath = vcfInputPath;
                String sqlPathContact = workDir + File.separator + "sql-gen" + File.separator + "Contact.sql";
                String sqlPathAddress = workDir + File.separator + "sql-gen" + File.separator + "Address.sql";
                String sqlPathEmail = workDir + File.separator + "sql-gen" + File.separator + "Email.sql";
                String sqlPathRelated = workDir + File.separator + "sql-gen" + File.separator + "Related.sql";
                String sqlPathTelephone = workDir + File.separator + "sql-gen" + File.separator + "Telephone.sql";

                if (vcf != null) {
                    vcfPath = vcf;
                }

                SqlFileExportPaths sqlFileExportPaths = new SqlFileExportPaths(sqlPathContact, sqlPathAddress, sqlPathEmail, sqlPathRelated, sqlPathTelephone);
                vcf2sql(vcfPath, sqlFileExportPaths);
            }
        }

    }

    private static void vcf2csv(String vcfPath, String csvPath){
        AbstractReader<ContactsInfo> vcfReader = new VcfReader().setInput(vcfPath);
        List<ContactsInfo> contactsInfoList = vcfReader.read();

        Builder csvBuilder = csvPath == null ? new CsvBuilder(contactsInfoList) : new CsvBuilder(contactsInfoList, csvPath);
        csvBuilder.build();
    }

    private static void csv2vcf(String csvPath, String vcfPath){
        AbstractReader<ContactsInfo> csvReader = new CsvReader().setInput(csvPath);
        List<ContactsInfo> contactsInfoList = csvReader.read();

        // reform
        ContactsReformContext contactsReformContext = new ContactsReformContext(contactsInfoList);
        contactsInfoList = contactsReformContext.reformContext();

        PureDataContext pureDataContext = new PureDataContext(contactsInfoList);
        contactsInfoList = pureDataContext.pureData();

        Builder vcfBuilder = vcfPath == null ? new VcfBuilder(contactsInfoList) : new VcfBuilder(contactsInfoList, vcfPath);
//        vcfBuilder.build(); // not check validate
        vcfBuilder.buildLogic(); // check validate
    }

    private static void vcf2vcf(String vcfPath){
        AbstractReader<ContactsInfo> vcfReader = new VcfReader().setInput(vcfPath);
        List<ContactsInfo> contactsInfoList = vcfReader.read();

        // reform
        ContactsReformContext contactsReformContext = new ContactsReformContext(contactsInfoList);
        contactsInfoList = contactsReformContext.reformContext();

        // merge duplicates
        PureDataContext pureDataContext = new PureDataContext(contactsInfoList);
        contactsInfoList = pureDataContext.pureData();

        Builder vcfBuilder = vcfOutputPath == null ? new VcfBuilder(contactsInfoList) : new VcfBuilder(contactsInfoList, vcfOutputPath);
//        vcfBuilder.build();
        vcfBuilder.buildLogic();
    }

    private static void csv2csv(String csvPath){
        AbstractReader<ContactsInfo> csvReader = new CsvReader().setInput(csvPath);
        List<ContactsInfo> contactsInfoList = csvReader.read();

        // reform
        ContactsReformContext contactsReformContext = new ContactsReformContext(contactsInfoList);
        contactsInfoList = contactsReformContext.reformContext();

        PureDataContext pureDataContext = new PureDataContext(contactsInfoList);
        contactsInfoList = pureDataContext.pureData();

        Builder csvBuilder = csvOutputPath == null ? new CsvBuilder(contactsInfoList) : new CsvBuilder(contactsInfoList, csvOutputPath);
        csvBuilder.buildLogic();
    }

    private static void vcf2sql(String vcfPath, SqlFileExportPaths sqlFileExportPaths){
        AbstractReader<ContactsInfo> vcfReader = new VcfReader().setInput(vcfPath);
        List<ContactsInfo> contactsInfoList = vcfReader.read();

        // reform
        ContactsReformContext contactsReformContext = new ContactsReformContext(contactsInfoList);
        contactsInfoList = contactsReformContext.reformContext();

        // merge duplicates
        PureDataContext pureDataContext = new PureDataContext(contactsInfoList);
        contactsInfoList = pureDataContext.pureData();

        System.out.println("Total Contact:" + contactsInfoList.size());

        SqlFormat sqlFormat = new SqlFormat(contactsInfoList, sqlFileExportPaths);
        sqlFormat.shape();
    }
}
