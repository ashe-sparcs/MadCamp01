package com.example.q.madcamp01;

import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

public class ListData {
    /**
     * 리스트 정보를 담고 있을 객체 생성
     */
    // 이름
    public String name;

    // 번호
    public String number;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData mListData_1, ListData mListData_2) {
            return sCollator.compare(mListData_1.name, mListData_2.name);
        }
    };
}
