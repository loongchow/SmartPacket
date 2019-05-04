package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Boolean extends Struct {

    public static int createBoolean(PackHelper builder, java.lang.Boolean val) {
        if (val == null) {
            return 0;
        }
        builder.prep(1, 1);
        builder.putByte((byte) (val ? 1 : 0));
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Boolean __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public boolean val() {
        return bb.get(bb_pos + 0) != 0;
    }
}
