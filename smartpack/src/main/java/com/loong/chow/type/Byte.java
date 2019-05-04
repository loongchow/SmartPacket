package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Byte extends Struct {

    public static int createByte(PackHelper builder, java.lang.Byte val) {
        if (val == null) {
            return 0;
        }
        builder.prep(1, 1);
        builder.putByte(val);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Byte __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public byte val() {
        return bb.get(bb_pos + 0);
    }
}
