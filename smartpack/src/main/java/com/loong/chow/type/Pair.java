package com.loong.chow.type;

import com.google.flatbuffersext.Struct;
import com.loong.chow.PackHelper;

import java.nio.ByteBuffer;

public class Pair extends Struct {

    public static int createPair(PackHelper builder, int key, int value) {

        builder.prep(4, 8);
        builder.addOffset(value);
        builder.addOffset(key);
        return builder.offset();
    }

    public void __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
    }

    public Pair __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public int key() {
        return bb.getInt(bb_pos + 0) + bb_pos;
    }

    public int value() {
        return bb.getInt(bb_pos + 4) + bb_pos + 4;
    }
}
