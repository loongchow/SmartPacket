package com.feilong.photo.demo;

import android.os.Bundle;

import com.loong.chow.annotation.TypeConverter;

public class BundleConverter {
    @TypeConverter
    public static Bundle convert(Demo demo){
        return null;
    }
    @TypeConverter
    public static Demo revert(Bundle tests){
        return null;
    }
}
