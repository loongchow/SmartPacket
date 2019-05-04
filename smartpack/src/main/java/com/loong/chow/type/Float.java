package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Float extends Struct {

    public static int createFloat(PackHelper builder, java.lang.Float val) {
        if (val == null) {
            return 0;
        }
        builder.prep(4, 4);
        builder.putFloat(val);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Float __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public float val() {
        return bb.getFloat(bb_pos + 0);
    }
}
