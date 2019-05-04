package com.feilong.photo.demo;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ObjectsCompat;

import com.loong.chow.annotation.PackFlag;
import com.loong.chow.annotation.TypeConverters;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjLongConsumer;

public class Sample {
    @PackFlag(0)
    Demo demo;

    @PackFlag(1)
    Demo[] demos;

//    @TypeConverters({TypeConvert.class})
    @PackFlag(2)
    ArrayList<Demo> demoArrayList;

    @PackFlag(3)
    String[] strings;

    @PackFlag(4)
    Boolean[] booleans;

    @PackFlag(5)
    Map<String, Demo> map2;


    @PackFlag(6)
    int a=9;
    @PackFlag(7)
    Integer b=10;

    @PackFlag(8)
    int[] array;

    @PackFlag(9)
    Integer[] arraystest;
    @PackFlag(10)
    Map<Integer,Demo> map3;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        if(! (obj instanceof Sample)){
            return false;
        }
        Sample other= (Sample) obj;
        if(!Objects.equals(demo,other.demo)){
            return false;
        }else if(!Objects.deepEquals(demos,other.demos)){
            return false;
        }else if(!Objects.equals(demoArrayList,other.demoArrayList))
        {
            return false;
        }else if(!Objects.deepEquals(strings,other.strings)){
            return false;
        }else if(!Objects.deepEquals(booleans,other.booleans)){
            return false;
        }else if(!Objects.equals(map2,other.map2)) {
            return false;
        }else if(a!=other.a) {
            return false;
        }else if(!Objects.equals(b,other.b)){
            return false;
        }else if(!Objects.deepEquals(array,other.array)){
            return false;
        }else if(!Objects.deepEquals(arraystest,other.arraystest))
        {
            return false;
        }
        else if(!Objects.deepEquals(map3,other.map3))
        {
            return false;
        }
        return true;
    }
}
