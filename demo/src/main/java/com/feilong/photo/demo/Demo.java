package com.feilong.photo.demo;

import com.loong.chow.annotation.PackFlag;

public class Demo {
    @PackFlag(0)
    int a=0;
    @PackFlag(1)
    byte b =1;
    @PackFlag(2)
    short c=2;
    @PackFlag(3)
    boolean d=true;
    @PackFlag(4)
    long e=11;
    @PackFlag(5)
    float f=1.0f;
    @PackFlag(6)
    double g=3d;

    @PackFlag(7)
    String string="demo";



    @Override
    public boolean equals(Object o){
        if(o ==null){
            return false;
        }
        if( ! (o instanceof Demo)){
            return false;
        }
        Demo demo= (Demo) o;

        if(a!= demo.a){
            return false;
        }else if(b!= demo.b){
            return false;
        }else if(c!= demo.c){
            return false;
        }else if(d!= demo.d){
            return false;
        }else if(e!= demo.e){
            return false;
        }else if(f!= demo.f){
            return false;
        }else if(g!= demo.g){
            return false;
        }else {
            if(string==null&& demo.string!=null){
                return false;
            }else if(string!=null){
                return string.equals(demo.string);
            }
        }
        return true;
    }
}
