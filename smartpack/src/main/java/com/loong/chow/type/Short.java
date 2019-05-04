package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Short extends Struct {

    public static int createShort(PackHelper builder, java.lang.Short val) {
        if (val == null) {
            return 0;
        }
        builder.prep(2, 2);
        builder.putShort(val);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Short __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public short val() {
        return bb.getShort(bb_pos + 0);
    }
}
