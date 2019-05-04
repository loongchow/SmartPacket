package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Long extends Struct {

    public static int createLong(PackHelper builder, java.lang.Long val) {
        if (val == null) {
            return 0;
        }
        builder.prep(8, 8);
        builder.putLong(val);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Long __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public long val() {
        return bb.getLong(bb_pos + 0);
    }
}
