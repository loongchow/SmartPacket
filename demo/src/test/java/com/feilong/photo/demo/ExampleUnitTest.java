package com.feilong.photo.demo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Example local unit demo, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(new int[]{1,2,3}, new int[]{1,2,3});
//        ReflectEqualUtils.equal(new Sample(),new Sample());
    }
    Sample sample=null;

    @Before
    public void initSample(){

        Demo test = new Demo();
        test.a=1;
        test.b=2;
        test.c=4;
        test.d=true;
        test.e=100000000;
        test.f=1.0f;
        test.g=3.0;
        test.string="test";

        sample = new Sample();
        sample.demo=test;
        sample.a=11;
        sample.b=123;
        sample.booleans=new Boolean[]{true,false,null};
        sample.map2=new HashMap<>();
        sample.map2.put("test",test);
        sample.map2.put("test1",test);
        sample.map2.put(null,test);
        sample.map2.put("test3",null);
        sample.demoArrayList=new ArrayList<>();
        sample.demoArrayList.add(test);
        sample.strings=new String[]{"test","test1","test2"};
        sample.demos=new Demo[]{test};
        sample.array=new int[]{0,1,3,-2,10000};
        sample.arraystest=new Integer[]{null,null,100,-100,0};
        sample.map3=new HashMap<>();
        sample.map3.put(null,test);
        sample.map3.put(1,null);
        sample.map3.put(3,test);

    }



    @Test
    public void testSample()
    {
        byte[] buffers=SampleHelper.toBytes(sample);
        Sample s = SampleHelper.fromBytes(buffers);

        assertEquals(sample,s);
    }



}