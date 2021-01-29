package com.hynial.shape;

import com.hynial.entity.ContactsInfo;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Comparator;

public class ContactsComparator {

    public static Comparator<ContactsInfo> comparator = (ContactsInfo ci1, ContactsInfo ci2) -> {
        String pinyin1 = ci1.getDisplayName();
        String pinyin2 = ci2.getDisplayName();
        try {
            pinyin1 = PinyinHelper.toHanYuPinyinString(ci1.getDisplayName(), new HanyuPinyinOutputFormat(), "", true);
            pinyin2 = PinyinHelper.toHanYuPinyinString(ci2.getDisplayName(), new HanyuPinyinOutputFormat(), "", true);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        // return Collator.getInstance(Locale.CHINESE).compare(pinyin1, pinyin2);
        return pinyin1.compareTo(pinyin2);
    };

}
