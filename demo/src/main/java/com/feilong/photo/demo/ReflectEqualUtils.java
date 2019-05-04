//package com.feilong.photo.demo;
//
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//
//import java.lang.reflect.Field;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//public class ReflectEqualUtils {
//    public static boolean equal(Object a, Object b){
//        if(a==b){
//            return true;
//        }else if(a==null&&b==null){
//            return true;
//        }else if(a==null&&b!=null){
//            return false;
//        }else if(a!=null&&b==null){
//            return true;
//        }else if(a.equals(b)){
//            return true;
//        }
//        else{
//            Field[] fields=a.getClass().getDeclaredFields();
//
//
//            for(Field f:fields){
//
//            }
//        }
//        return false;
//    }
//
//
//    public static boolean equal(Field f, Object a, Object b) throws IllegalAccessException {
//        Class type=f.getType();
//        f.setAccessible(true);
//
//
//
//        if(type==int.class){
//                return f.getInt(a)==f.getInt(b);
//        }else if(type==boolean.class){
//            return f.getBoolean(a)==f.getBoolean(b);
//        }else if(type==short.class){
//            return f.getShort(a)==f.getShort(b);
//        }else if(type==int.class){
//            return f.getInt(a)==f.getInt(b);
//        }else if(type==long.class){
//            return f.getLong(a)==f.getLong(b);
//        }else if(type==float.class){
//            return f.getFloat(a)==f.getFloat(b);
//        }else if(type==double.class){
//            return f.getDouble(a)==f.getDouble(b);
//        }else
//        {
//            Object fiedlA=f.get(a);
//            Object fieldB=f.get(b);
//            if(fiedlA==fieldB){
//                return true;
//            } else if(fiedlA ==null&&fieldB!=null ) {
//                return false;
//            }else if(fiedlA!=null&&fieldB==null)
//            {
//                return false;
//            }
//            else if(type==String.class){
//                return fiedlA.equals(fieldB);
//            }
//            else if(fiedlA instanceof List){
//                List alist= (List) fiedlA;
//                List blist=(List)fieldB;
//                return listEqual(alist,blist);
//            }else if(fiedlA instanceof Map){
//                Map mapA= (Map) fiedlA;
//                Map mapB= (Map) fieldB;
//                return mapEqual(mapA,mapB);
//            }else {
//                return fiedlA.equals(fieldB);
//            }
//
//        }
//
//
//    }
//
//    public static boolean mapEqual(Map aMap,Map bMap)
//    {
//        if(aMap==bMap){
//            return true;
//        }
//        if(aMap!=null&&bMap==null) {
//            return false;
//        }else if(aMap==null&&bMap!=null){
//            return false;
//        } else{
//            if(aMap.size()!=bMap.size()){
//                return false;
//            }else {
//
//
//                for(Object key:aMap.keySet()){
//                    if()
//                }
//            }
//        }
//    }
//
//    public static boolean listEqual(List alist, List blist){
//        if(alist==blist){
//            return true;
//        }
//        if(alist!=null&&blist==null) {
//            return false;
//        }else if(alist==null&&blist!=null){
//            return false;
//        }else
//        {
//            if(alist.size()!=blist.size())
//            {
//                return false;
//            }
//
//            for (int j=0;j<alist.size();j++)
//            {
//                if(!ReflectEqualUtils.equal(alist.get(j),blist.get(j))){
//                    return false;
//                }
//            }
//            return false;
//        }
//    }
//
//}
