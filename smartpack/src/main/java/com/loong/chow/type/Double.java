package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Double extends Struct {

    public static int createDouble(PackHelper builder, java.lang.Double val) {
        if (val == null) {
            return 0;
        }
        builder.prep(8, 8);
        builder.putDouble(val);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Double __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public double val() {
        return bb.getDouble(bb_pos + 0);
    }
}
