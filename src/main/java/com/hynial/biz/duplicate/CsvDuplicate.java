package com.hynial.biz.duplicate;

import com.hynial.biz.CsvReader;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.shape.ContactsComparator;
import com.hynial.util.CommonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CsvDuplicate {
    public static int minMobileNumber = 5;
    private String input;

    public CsvDuplicate(String input) {
        this.input = input;
    }

    private List<ContactsInfo> read(){
        CsvReader csvReader = new CsvReader().setInput(this.input);
        List<ContactsInfo> contactsInfoList = csvReader.read();
        return contactsInfoList;
    }
    // by column
    public Map<String, List<ContactsInfo>> categoryByAlias(List<ContactsInfo> contactsInfoList, String aliasOpt) {
        return categoryByAlias(contactsInfoList, new ArrayList<>(List.of(aliasOpt)));
    }

    // by columns, except same fields in List
    public Map<String, List<ContactsInfo>> categoryByAlias(List<ContactsInfo> contactsInfoList, List<String> aliasOpts){
        Map<String, List<ContactsInfo>> categoryMap = new HashMap<>();

        List<String> aliasOptCopy = new ArrayList<>(aliasOpts);
        LinkedHashMap<String, Integer> aliasIndexMap = new LinkedHashMap<String, Integer>();
        String regInd = " \\d{1,2}$";

        for (int i = 0; i < aliasOptCopy.size(); i++) {
            String aliasOpt = aliasOptCopy.get(i);
            String[] aliasInd = aliasOpt.trim().split(regInd, -1);
            String alias = aliasOpt;
            int ind = -1;
            if (aliasInd.length > 1){
                alias = aliasInd[0];
                ind = Integer.parseInt(aliasOpt.replace(alias, "").trim());
            }
            aliasOptCopy.set(i, alias);
            aliasIndexMap.put(alias, ind);
        }

        try {
            for(ContactsInfo c : contactsInfoList){
                String valueString = "", separator = ",";
                for (int i = 0; i < aliasOptCopy.size(); i++) {
                    String alias = aliasOptCopy.get(i);
                    if(ContactsInfo.getAliasMap().get(alias) == null){
                        aliasOptCopy.set(i, aliasOpts.get(i));
                    }
                    alias = aliasOptCopy.get(i);

                    Object value = c.getValueByAlias(alias);

                    if (value instanceof String) { // null
                        valueString += value + separator;
                    } else if(value instanceof List<?>){
                        int ind = aliasIndexMap.get(alias).intValue();
                        ParameterizedType parameterizedType = (ParameterizedType) ContactsInfo.getAliasMap().get(alias).getGenericType();
                        Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        if (parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                            List<String> vals = (List<String>) value;
                            if(vals != null && ind != -1 && ind  < vals.size()){
                                String val = vals.get(ind - 1);
                                valueString += val + separator;
                            }
                        } else if (parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)) {
                            List<AddressInfo> vals = (List<AddressInfo>) value;
                            // TODO
                            throw new RuntimeException("TODO-SupportedType:" + AddressInfo.class.getSimpleName());
                        }else{
                            throw new RuntimeException("UnsupportedType");
                        }
                    }else{
                        if(null == value){
                            System.out.println("null value happened!");
                        }
                        throw new RuntimeException("UnsupportedTypeWhenGetValueFromAlias:" + alias);
                    }

                }
                if(valueString.endsWith(separator)) valueString = valueString.substring(0, valueString.length() - 1);
                addValue(categoryMap, valueString, c);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return categoryMap;
    }

    private void addValue(Map<String, List<ContactsInfo>> categoryMap, String val, ContactsInfo c){
        List<ContactsInfo> contactsInfos = categoryMap.get(val);
        if(contactsInfos == null) {
            categoryMap.put(val, new ArrayList<>(List.of(c)));
        }else{
            contactsInfos.add(c);
        }
    }

    public void printDuplicates(String aliasOpt){
        List<ContactsInfo> contactsInfoList = read();
        Map<String, List<ContactsInfo>> categoryMap = categoryByAlias(contactsInfoList, aliasOpt);

        AtomicInteger duplicates = new AtomicInteger();
        categoryMap.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(e -> {
            System.out.println((CommonUtil.isEmpty(e.getKey()) == true ? "Empty" : e.getKey()) + ":" + e.getValue().size());
            duplicates.getAndIncrement();
        });

        System.out.println(String.format("Total Duplicate Field:[%s] Count:%d", aliasOpt, duplicates.get()));
    }

    public List<ContactsInfo> buildUnique(List<ContactsInfo> contactsInfoList, String aliasOpt){
        Map<String, List<ContactsInfo>> categoryMap = categoryByAlias(contactsInfoList, aliasOpt);
        List<Map.Entry<String, List<ContactsInfo>>> entries = categoryMap.entrySet().stream().filter(entry -> entry.getValue().size() > 1).collect(Collectors.toList());
        if(entries == null || entries.size() == 0){
            return null;
        }

        List<ContactsInfo> uniques = new ArrayList<>();
        for(Map.Entry<String, List<ContactsInfo>> entry : entries){
            List<ContactsInfo> contactsInfos = entry.getValue().stream().filter(contactsInfo -> {
                if(contactsInfo.getMobilePhones().size() == 0) return false;
                for (int i = 0; i < contactsInfo.getMobilePhones().size(); i++) {
                    if(contactsInfo.getMobilePhones().get(i).length() < minMobileNumber){
                        // mobile phone number less then 7 digits.
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());

            if(contactsInfos.size() > 1){
                System.out.println("Still Duplicate:" + contactsInfos.get(0).getDisplayName());
            }
            uniques.addAll(contactsInfos);
        }

        List<Map.Entry<String, List<ContactsInfo>>> originalUniqueEntries = categoryMap.entrySet().stream().filter(entry -> entry.getValue().size() < 2).collect(Collectors.toList());
        List<ContactsInfo> less2ContactsInfoList = new ArrayList<>();
        originalUniqueEntries.stream().forEach(e -> {
            for(ContactsInfo c : e.getValue()){
                if(c.getMobilePhones().size() > 0){
                    if(c.getMobilePhones().get(0).length() >= minMobileNumber){
                        less2ContactsInfoList.addAll(e.getValue());
                    }
                }
            }
        });

        List<ContactsInfo> totals = new ArrayList<>();
        totals.addAll(uniques);
        totals.addAll(less2ContactsInfoList);

        //Comparator.comparing(ContactsInfo::getDisplayName);

        return totals.stream().sorted(ContactsComparator.comparator).collect(Collectors.toList());
    }

    public List<ContactsInfo> uniqueByName(List<ContactsInfo> contactsInfoList){
        if(contactsInfoList == null) return null;

        Map<String, ContactsInfo> uniqueMap = new HashMap<>();
        for(ContactsInfo contactsInfo : contactsInfoList){
            String lastName = contactsInfo.getLastName();
            String firstName = contactsInfo.getFirstName();

            String key = ((lastName == null) ? "" : lastName) + ((firstName == null) ? "" : firstName);
            if(CommonUtil.isEmpty(key)){
                throw new RuntimeException("Impossible: lastName & firstName all null");
            }

            if(uniqueMap.get(key) == null){
                uniqueMap.put(key, contactsInfo);
            }else{
                ContactsInfo original = uniqueMap.get(key);
                // merge new to old
                original.merge(contactsInfo);
            }
        }

        return uniqueMap.values().stream().sorted(ContactsComparator.comparator).collect(Collectors.toList());
    }

    // by all mobile numbers find same numbers contact
    public Map<String, List<ContactsInfo>> uniqueByMobileNumber(List<ContactsInfo> contactsInfoList){
        if(contactsInfoList == null || contactsInfoList.size() == 0) return null;

        Map<String, List<ContactsInfo>> result = new HashMap<>();
        for(ContactsInfo contactsInfo : contactsInfoList){
            List<String> mobiles = contactsInfo.getMobilePhones();
            if(mobiles == null) continue;

            for (int i = 0; i < mobiles.size(); i++) {
                if(result.get(mobiles.get(i)) == null){
                    result.put(mobiles.get(i), new ArrayList<>(List.of(contactsInfo)));
                }else{
                    result.get(mobiles.get(i)).add(contactsInfo);
                }
            }
        }

        result = result.entrySet().stream().filter(entry ->{
            List<ContactsInfo> tmp = entry.getValue();
            if(tmp == null) return false;
            if(tmp.size() > 1) return true;
            return false;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if(result == null) return null;

//        result.forEach((x, y) -> {
//            System.out.println("Number:" + x);
//            System.out.println(y.stream().map(contactsInfo -> contactsInfo.getDisplayName()).collect(Collectors.joining(",")));
//        });

        return result;
    }
}
