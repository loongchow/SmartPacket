package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Int extends Struct {

    public static int createInt(PackHelper builder, Integer val) {
        if (val == null) {
            return 0;
        }
        builder.prep(4, 4);
        builder.putInt(val);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Int __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public int val() {
        return bb.getInt(bb_pos + 0);
    }
}
