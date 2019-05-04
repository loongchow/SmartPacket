package com.feilong.photo.demo;

import com.loong.chow.annotation.TypeConverter;

import java.util.ArrayList;
public class TypeConvert {
    @TypeConverter
    public static Demo[] convert(ArrayList<Demo> demos){
        return null;
    }
    @TypeConverter
    public static ArrayList<Demo> revert(Demo[] demos){
        return null;
    }
}
