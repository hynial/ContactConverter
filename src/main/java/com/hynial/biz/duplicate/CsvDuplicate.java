package com.hynial.biz.duplicate;

import com.hynial.biz.CsvReader;
import com.hynial.entity.AddressInfo;
import com.hynial.entity.ContactsInfo;
import com.hynial.util.CommonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
public class CsvDuplicate {
    private String input;

    public CsvDuplicate(String input) {
        this.input = input;
    }

    public Map<String, List<ContactsInfo>> categoryByAlias(String aliasOpt){
        CsvReader csvReader = new CsvReader().setInput(this.input);
        List<ContactsInfo> contactsInfoList = csvReader.read();

        Map<String, List<ContactsInfo>> categoryMap = new HashMap<>();

        String regInd = " \\d{1,2}$";
        String[] aliasInd = aliasOpt.trim().split(regInd, -1);
        String alias = aliasOpt;
        int ind = -1;
        if (aliasInd.length > 1){
            alias = aliasInd[0];
            ind = Integer.parseInt(aliasOpt.replace(alias, "").trim());
        }

        try {
            for(ContactsInfo c : contactsInfoList){
                if(ContactsInfo.getAliasMap().get(alias) == null){
                    alias = aliasOpt;
                }
                Object value = c.getValueByAlias(alias);
                if (value instanceof String) { // null
                    String val = (String) value;
                    addValue(categoryMap, val, c);
                } else if(value instanceof List<?>){
                    ParameterizedType parameterizedType = (ParameterizedType) ContactsInfo.getAliasMap().get(alias).getGenericType();
                    Class<?> parameterizedTypeActualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    if (parameterizedTypeActualTypeArgument.isAssignableFrom(String.class)) {
                        List<String> vals = (List<String>) value;
                        if(vals != null && ind != -1 && ind  < vals.size()){
                            String val = vals.get(ind - 1);
                            addValue(categoryMap, val, c);
                        }
                    } else if (parameterizedTypeActualTypeArgument.isAssignableFrom(AddressInfo.class)) {
                        List<AddressInfo> vals = (List<AddressInfo>) value;
                        // TODO
                    }else{
                        throw new RuntimeException("UnsupportedType");
                    }
                }else{
                    throw new RuntimeException("UnsupportedType");
                }
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
        Map<String, List<ContactsInfo>> categoryMap = categoryByAlias(aliasOpt);

        AtomicInteger duplicates = new AtomicInteger();
        categoryMap.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(e -> {
            System.out.println((CommonUtil.isEmpty(e.getKey()) == true ? "Empty" : e.getKey()) + ":" + e.getValue().size());
            duplicates.getAndIncrement();
        });

        System.out.println(String.format("Total Duplicate Field:[%s] Count:%d", aliasOpt, duplicates.get()));
    }
}
