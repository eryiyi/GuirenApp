package com.Lbins.GuirenApp.huanxin.domain;

import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;

import java.util.Arrays;

public class EmojiconExampleGroupData {
    
    private static int[] icons = new int[]{
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
    };
    
    private static int[] bigIcons = new int[]{
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
        R.drawable.ee_1,
    };
    
    
    private static final EaseEmojiconGroupEntity DATA = createData();
    
    private static EaseEmojiconGroupEntity createData(){
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], null, Type.BIG_EXPRESSION);
            datas[i].setBigIcon(bigIcons[i]);
            //you can replace this to any you want
            datas[i].setName(GuirenApplication.getInstance().getApplicationContext().getString(R.string.emojicon_test_name)+ (i+1));
            datas[i].setIdentityCode("em"+ (1000+i+1));
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.ee_2);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }
    
    
    public static EaseEmojiconGroupEntity getData(){
        return DATA;
    }
}
