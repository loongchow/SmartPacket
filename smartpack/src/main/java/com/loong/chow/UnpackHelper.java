package com.loong.chow;

import com.loong.chow.type.Boolean;
import com.loong.chow.type.Byte;
import com.loong.chow.type.Double;
import com.loong.chow.type.Float;
import com.loong.chow.type.Int;
import com.loong.chow.type.Long;
import com.loong.chow.type.Short;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Collections;

public class UnpackHelper {

    public final static ThreadLocal<Charset> UTF8_CHARSET = new ThreadLocal<Charset>() {
        @Override
        protected Charset initialValue() {
            return Charset.forName("UTF-8");
        }
    };
    public static final int SIZEOF_INT = 4;
    private final static ThreadLocal<CharBuffer> CHAR_BUFFER = new ThreadLocal<CharBuffer>();
    private final static ThreadLocal<CharsetDecoder> UTF8_DECODER = new ThreadLocal<CharsetDecoder>() {
        @Override
        protected CharsetDecoder initialValue() {
            return Charset.forName("UTF-8").newDecoder();
        }
    };
    private ByteBuffer bb;
    private int bb_pos;

    public UnpackHelper(ByteBuffer bb, int bb_pos) {
        this.bb = bb;
        this.bb_pos = bb_pos;
    }

    public UnpackHelper(UnpackHelper helper, int bb_pos) {
        this.bb = helper.bb;
        this.bb_pos = bb_pos;
    }

    public static int __indirect(ByteBuffer bb, int offset) {
        return offset + bb.getInt(offset);
    }


    public ByteBuffer getBb() {
        return bb;
    }

    public int __offset(int vtable_offset) {
        int vtable = bb_pos - bb.getInt(bb_pos);
        return vtable_offset < bb.getShort(vtable) ? bb.getShort(vtable + vtable_offset) : 0;
    }

    public int __indirect(int vtable_offset) {
        int offset = __offset(vtable_offset);
        if (offset <= 0) {
            return 0;
        }

        return __indirect(bb, offset + bb_pos);
    }

    public String pickString(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }

        int location = __indirect(vtable_offset);

        return pickStringByLocation(location);
    }

    public String pickStringByLocation(int location) {
        if (invalid(location)) {
            return null;
        }
        CharsetDecoder decoder = UTF8_DECODER.get();
        decoder.reset();
        ByteBuffer src = bb.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        int length = src.getInt(location);
        src.position(location + SIZEOF_INT);
        src.limit(location + SIZEOF_INT + length);

        int required = (int) ((float) length * decoder.maxCharsPerByte());
        CharBuffer dst = CHAR_BUFFER.get();
        if (dst == null || dst.capacity() < required) {
            dst = CharBuffer.allocate(required);
            CHAR_BUFFER.set(dst);
        }

        dst.clear();

        try {
            CoderResult cr = decoder.decode(src, dst, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
        } catch (CharacterCodingException x) {
            throw new RuntimeException(x);
        }

        return dst.flip().toString();
    }

    public String[] pickStringArray(int vtable_offset) {

        int stringArrayOffset = __offset(vtable_offset);
        if (stringArrayOffset == 0 || stringArrayOffset >= bb.capacity()) {
            return null;
        }
        int[] offsetArray = pickOffsetArray(vtable_offset);
        String[] strings = new String[offsetArray.length];
        int index = 0;
        for (int offset : offsetArray) {
            if (offset == bb.capacity()) {
                strings[index++] = null;
                continue;

            }
            CharsetDecoder decoder = UTF8_DECODER.get();
            decoder.reset();

            ByteBuffer src = bb.duplicate().order(ByteOrder.LITTLE_ENDIAN);
            int length = src.getInt(offset);
            src.position(offset + SIZEOF_INT);
            src.limit(offset + SIZEOF_INT + length);

            int required = (int) ((float) length * decoder.maxCharsPerByte());
            CharBuffer dst = CHAR_BUFFER.get();
            if (dst == null || dst.capacity() < required) {
                dst = CharBuffer.allocate(required);
                CHAR_BUFFER.set(dst);
            }

            dst.clear();

            try {
                CoderResult cr = decoder.decode(src, dst, true);
                strings[index++] = dst.flip().toString();
                if (!cr.isUnderflow()) {
                    cr.throwException();
                }
            } catch (CharacterCodingException x) {
                throw new RuntimeException(x);
            }

        }

        return strings;
    }

    public ArrayList pickStringList(int vtable_offset) {

        String[] array = pickStringArray(vtable_offset);
        if (array == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>(array.length);
        Collections.addAll(list, array);
        return list;
    }

    public byte pickByte(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return 0;
        } else {
            return bb.get(bb_pos + o);
        }
    }

    public java.lang.Byte pickBoxedByte(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Byte b = new Byte();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Byte(b.val());
        }
    }

    public ArrayList<java.lang.Byte> pickBoxedByteList(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int posArray[] = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Byte> bytes = new ArrayList<>(len);
        Byte mByte = new Byte();
        for (int pos : posArray) {
            if (pos == bb.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(mByte.__assign(pos, bb).val());
            }
        }
        return bytes;
    }

    public boolean pickBoolean(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return false;
        } else {
            return 0 != bb.get(bb_pos + o);
        }
    }

    public java.lang.Boolean pickBoxedBoolean(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Boolean b = new Boolean();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Boolean(b.val());
        }
    }

    public ArrayList<java.lang.Boolean> pickBoxedBooleanList(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int posArray[] = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Boolean> bytes = new ArrayList<>(len);
        Boolean box = new Boolean();
        for (int pos : posArray) {
            if (pos == bb.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(box.__assign(pos, bb).val());
            }
        }
        return bytes;
    }

    public short pickShort(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return 0;
        } else {
            return bb.getShort(bb_pos + o);
        }
    }

    public java.lang.Byte pickBoxedShort(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Byte b = new Byte();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Byte(b.val());
        }
    }

    public ArrayList<java.lang.Short> pickBoxedShortList(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int posArray[] = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Short> bytes = new ArrayList<>(len);
        Short box = new Short();
        for (int pos : posArray) {
            if (pos == bb.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(box.__assign(pos, bb).val());
            }
        }
        return bytes;
    }

    public int pickInt(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return 0;
        } else {
            return bb.getInt(bb_pos + o);
        }
    }

    public java.lang.Integer pickBoxedInt(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Int b = new Int();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Integer(b.val());
        }
    }

    public ArrayList<java.lang.Integer> pickBoxedIntList(ByteBuffer byteBuffer, int bb_pos,
                                                         int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int posArray[] = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Integer> bytes = new ArrayList<>(len);
        Int box = new Int();
        for (int pos : posArray) {
            if (pos == byteBuffer.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(box.__assign(pos, byteBuffer).val());
            }
        }
        return bytes;
    }

    public long pickLong(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return 0;
        } else {
            return bb.getLong(bb_pos + o);
        }
    }

    public java.lang.Long pickBoxedLong(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Long b = new Long();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Long(b.val());
        }
    }

    public ArrayList<java.lang.Long> pickBoxedLongList(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int posArray[] = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Long> bytes = new ArrayList<>(len);
        Long box = new Long();
        for (int pos : posArray) {
            if (pos == bb.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(box.__assign(pos, bb).val());
            }
        }
        return bytes;
    }

    public float pickFloat(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return 0;
        } else {
            return bb.getFloat(bb_pos + o);
        }
    }

    public java.lang.Float pickBoxedFloat(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Float b = new Float();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Float(b.val());
        }
    }

    public ArrayList<java.lang.Float> pickBoxedFloatList(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int[] posArray = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Float> bytes = new ArrayList<>(len);
        Float box = new Float();
        for (int pos : posArray) {
            if (pos == bb.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(box.__assign(pos, bb).val());
            }
        }
        return bytes;
    }

    public double pickDouble(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return 0;
        } else {
            return bb.getDouble(bb_pos + o);
        }
    }

    public java.lang.Double pickBoxedDouble(int vtable_offset) {
        int o = __offset(vtable_offset);
        if (o == 0) {
            return null;
        } else {
            Double b = new Double();
            b.__assign(bb_pos + o, bb);
            return new java.lang.Double(b.val());
        }
    }

    public ArrayList<java.lang.Double> pickBoxedDoubleList(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        int posArray[] = pickOffsetArray(vtable_offset);
        ArrayList<java.lang.Double> bytes = new ArrayList<>(len);
        Double box = new Double();
        for (int pos : posArray) {
            if (pos == bb.capacity()) {
                bytes.add(null);
            } else {
                bytes.add(box.__assign(pos, bb).val());
            }
        }
        return bytes;
    }

    /**
     * Get the length of a vector.
     *
     * @param vtable_offset An `int` index into the Table's ByteBuffer.
     * @return Returns the length of the vector whose offset is stored at `offset`.
     */
    public int __vector_len(int vtable_offset) {
        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return 0;
        } else {
            offset += bb_pos;
            offset += bb.getInt(offset);
            return bb.getInt(offset);
        }

    }

    /**
     * Get the start data of a vector.
     *
     * @param vtable_offset An `int` index into the Table's ByteBuffer.
     * @return Returns the start of the vector data whose offset is stored at `offset`.
     */
    public int __vector(int vtable_offset) {

        int offset = __offset(vtable_offset);
        if (offset == 0) {
            return 0;
        } else {
            offset += bb_pos;
            offset += bb.getInt(offset);
            return offset + 4;
        }
    }

    public int[] pickIntArray(int vtable_offset) {
        return pickIntArrayByLocation(__indirect(vtable_offset));

    }

    public int[] pickIntArrayByLocation(int loc) {
        if (invalid(loc)) {
            return null;
        }
        int len = bb.getInt(loc);
        int[] array = new int[len];

        int start = loc + 4;
        for (int i = 0; i < len; i++) {
            int offset = start + 4 * i;
            array[i] = bb.getInt(offset);
        }
        return array;
    }

    public int[] pickOffsetArray(int vtable_offset) {
        return pickOffsetArrayByLocation(__indirect(vtable_offset));

    }

    public int[] pickOffsetArrayByLocation(int location) {

        if (invalid(location)) {
            return null;
        }
        int len = bb.getInt(location);
        int[] array = new int[len];

        int start = location + 4;
        for (int i = 0; i < len; i++) {
            int offset = start + 4 * i;
            array[i] = bb.getInt(offset) + offset;
        }
        return array;

    }

    public byte[] pickByteArray(int vtable_offset) {
        return pickByteArrayByLocation(__indirect(vtable_offset));
    }

    public byte[] pickByteArrayByLocation(int location) {
        if (invalid(location)) {

            return null;
        }
        int len = bb.getInt(location);
        int start = location + 4;
        byte[] array = new byte[len];
        for (int i = 0; i < len; i++) {
            int loc = start + i;
            array[i] = bb.get(loc);
        }
        return array;


    }

    public short[] pickShortArray(int vtable_offset) {
        return pickShortArrayByLocation(__indirect(vtable_offset));

    }

    public short[] pickShortArrayByLocation(int location) {
        if (invalid(location)) {
            return null;
        }
        int len = bb.getInt(location);
        int start = location + 4;
        short[] array = new short[len];
        for (int i = 0; i < len; i++) {
            int loc = start + i * 2;
            array[i] = bb.getShort(loc);
        }
        return array;


    }

    public boolean[] pickBooleanArray(int vtable_offset) {
        return pickBooleanArrayByLocation(__indirect(vtable_offset));

    }

    public boolean[] pickBooleanArrayByLocation(int location) {
        if (invalid(location)) {

            return null;
        }
        int len = bb.getInt(location);
        int start = location + 4;
        boolean[] array = new boolean[len];
        for (int i = 0; i < len; i++) {
            int loc = start + i;
            array[i] = bb.get(loc) != 0;
        }
        return array;


    }

    public long[] pickLongArray(int vtable_offset) {

        return pickLongArrayByLocation(__indirect(vtable_offset));

    }

    public long[] pickLongArrayByLocation(int location) {
        if (invalid(location)) {
            return null;
        }
        int len = bb.getInt(location);
        long[] array = new long[len];

        int start = location + 4;
        for (int i = 0; i < len; i++) {
            int offset = start + 8 * i;
            array[i] = bb.getLong(offset);
        }
        return array;

    }

    public double[] pickDoubleArray(int vtable_offset) {
        return pickDoubleArrayByLocation(__indirect(vtable_offset));

    }

    public double[] pickDoubleArrayByLocation(int location) {
        if (invalid(location)) {
            return null;
        }
        int len = bb.getInt(location);
        double[] array = new double[len];

        int start = location + 4;
        for (int i = 0; i < len; i++) {
            int offset = start + 8 * i;
            array[i] = bb.getDouble(offset);
        }
        return array;

    }

    public float[] pickFloatArray(int vtable_offset) {
        if (__offset(vtable_offset) <= 0) {
            return null;
        }
        int len = __vector_len(vtable_offset);
        float[] array = new float[len];

        int start = __vector(vtable_offset);
        for (int i = 0; i < len; i++) {
            int offset = start + 4 * i;
            array[i] = bb.getFloat(offset);
        }
        return array;

    }

    public float[] pickFloatArrayByLocation(int location) {
        if (invalid(location)) {
            return null;
        }
        int len = bb.getInt(location);
        float[] array = new float[len];

        int start = location + 4;
        for (int i = 0; i < len; i++) {
            int offset = start + 4 * i;
            array[i] = bb.getFloat(offset);
        }
        return array;

    }


    public boolean invalid() {
        if (bb_pos >= bb.limit()) {
            return true;
        }
        return false;
    }

    public boolean invalid(int pos) {
        if (pos < 0 || pos >= bb.limit()) {
            return true;
        } else {
            return false;
        }
    }


}
