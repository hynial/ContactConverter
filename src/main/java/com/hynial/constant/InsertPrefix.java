package com.hynial.constant;

public class InsertPrefix {
    // replace regex : ''[^{|,]?.*?[^},]?''
    public static final String CONTACT = "INSERT INTO contact (id, current_phone,current_address,current_email,first_name,last_name,display_name,nick_name,job_title,department,organization,notes,birthday,lunar_birthday,wechat,qq,gender,revise_time) VALUES ({0},''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'',''{9}'',''{10}'',''{11}'',''{12}'',''{13}'',''{14}'',''{15}'',''{16}'',''{17}'');";
    public static final String TELEPHONE = "INSERT INTO telephone (id, contact_id,phone_number,phone_type) VALUES ({0},{1},''{2}'',''{3}'');";
    public static final String EMAIL = "INSERT INTO email (id, contact_id,email) VALUES ({0},{1},''{2}'');";
    public static final String RELATED = "INSERT INTO related (id, contact_id,related,related_name) VALUES ({0},{1},''{2}'',''{3}'');";
    public static final String ADDRESS = "INSERT INTO address (id, contact_id,display_address,address_type,latitude,longitude,country_code,street1,street2,district,city,state,country,postal_code) VALUES ({0},{1},''{2}'',''{3}'',{4},{5},''{6}'',''{7}'',''{8}'',''{9}'',''{10}'',''{11}'',''{12}'',''{13}'');";
}
