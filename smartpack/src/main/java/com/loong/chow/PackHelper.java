package com.loong.chow;

import com.google.flatbuffersext.FlatBufferBuilder;
import com.google.flatbuffersext.FlatBufferBuilder.ByteBufferFactory;
import com.google.flatbuffersext.Table;
import com.loong.chow.type.Boolean;
import com.loong.chow.type.Byte;
import com.loong.chow.type.Double;
import com.loong.chow.type.Float;
import com.loong.chow.type.Int;
import com.loong.chow.type.Long;
import com.loong.chow.type.Short;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

/**
 * @author feilongzou
 */
public class PackHelper {

    public final static ThreadLocal<Charset> UTF8_CHARSET = new ThreadLocal<Charset>() {
        @Override
        protected Charset initialValue() {
            return Charset.forName("UTF-8");
        }
    };
    public static final int SIZEOF_INT = 4;
    private final static ThreadLocal<CharsetDecoder> UTF8_DECODER = new ThreadLocal<CharsetDecoder>() {
        @Override
        protected CharsetDecoder initialValue() {
            return Charset.forName("UTF-8").newDecoder();
        }
    };
    private final static ThreadLocal<CharBuffer> CHAR_BUFFER = new ThreadLocal<CharBuffer>();
    private FlatBufferBuilder flatBufferBuilder;
//	private ConcurrentHashMap<CharSequence, Integer> stringOffsetCache = new ConcurrentHashMap();

    public PackHelper() {
        this(32 * 1024);
    }

    public PackHelper(int initialSize) {
        this.flatBufferBuilder = new FlatBufferBuilder(initialSize);
    }

    public PackHelper(ByteBuffer existing_bb, ByteBufferFactory bb_factory) {
        this.flatBufferBuilder = new FlatBufferBuilder(existing_bb, bb_factory);
    }

    public int addStringList(List<String> strings) {
        if (strings == null) {
            return 0;
        } else {
            String[] array = strings.toArray(new String[strings.size()]);
            return createStringArray(array);
        }
    }

    public void addBoxedByte(int voffset,
                             java.lang.Byte val) {
        if (val != null) {
            int offset = Byte.createByte(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    public int addBlobList(byte[][] blobs) {
        if (blobs == null) {
            return 0;
        }

        int[] offsetArray = new int[blobs.length];
        int index = 0;
        for (byte[] blob : blobs) {
            offsetArray[index++] = addByteArray(blob);

        }
        return createVectorOfTables(offsetArray);
    }

    public int addBoxedShortList(
            List<java.lang.Short> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Short val : vals) {
                offsets[index++] = Short.createShort(this, val);
            }
            return createVectorOfTables(offsets);
        }
        return 0;
    }

    public void addBoxedInt(int voffset,
                            java.lang.Integer val) {
        if (val != null) {
            int offset = Int.createInt(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    public int addBoxedIntList(List<java.lang.Integer> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Integer val : vals) {
                offsets[index++] = Int.createInt(this, val);
            }
            int vector_offset = createVectorOfTables(offsets);
            return vector_offset;
        }
        return 0;
    }

    public void addBoxedLong(int voffset,
                             java.lang.Long val) {
        if (val != null) {
            int offset = Long.createLong(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    public int addBoxedLongList(List<java.lang.Long> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Long val : vals) {
                offsets[index++] = Long.createLong(this, val);
            }
            int vector_offset = createVectorOfTables(offsets);
            return vector_offset;
        }
        return 0;
    }

    public void addBoxedFloat(int voffset,
                              java.lang.Float val) {
        if (val != null) {
            int offset = Float.createFloat(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    public int addBoxedFloatList(
            List<java.lang.Float> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Float val : vals) {
                offsets[index++] = Float.createFloat(this, val);
            }
            int vector_offset = createVectorOfTables(offsets);
            return vector_offset;
        }
        return 0;

    }

    public void addBoxedDouble(int voffset,
                               java.lang.Double val) {
        if (val != null) {
            int offset = Double.createDouble(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    public int addBoxedDoubleList(
            List<java.lang.Double> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Double val : vals) {
                offsets[index++] = Double.createDouble(this, val);
            }
            int vector_offset = createVectorOfTables(offsets);
            return vector_offset;
        }
        return 0;
    }

//	public int addUnFixedType(Object object) {
//		return UnFixedType.createObjectUnKownType(this, object);
//	}

//	public int addUnFixedTypeList(List list) {
//		if (list == null) {
//			return 0;
//		} else {
//			int[] offsets = new int[list.size()];
//			int index = 0;
//			for (Object o : list) {
//				offsets[index++] = UnFixedType.createObjectUnKownType(this, o);
//			}
//			return createVectorOfTables(offsets);
//		}
//	}

    public int createStringArray(String[] strings) {
        if (strings != null) {
            int[] offsetArray = new int[strings.length];
            int index = 0;
            for (String s : strings) {
                offsetArray[index++] = createString(s);
            }
            return createVectorOfTables(offsetArray);

        }
        return 0;
    }

    public int createStringArray(List<String> strings) {
        if (strings == null) {
            return 0;
        }
        return createStringArray(strings.toArray(new String[strings.size()]));
    }

    public int addBoxedByteList(List<java.lang.Byte> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Byte val : vals) {
                offsets[index++] = Byte.createByte(this, val);
            }
            return createVectorOfTables(offsets);
        }
        return 0;
    }

    public void addBoxedBoolean(int voffset, java.lang.Boolean val) {
        if (val != null) {
            int offset = Boolean.createBoolean(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    public int addBoxedBooleanList(List<java.lang.Boolean> vals) {
        if (vals != null) {

            int[] offsets = new int[vals.size()];
            int index = 0;
            for (java.lang.Boolean val : vals) {
                offsets[index++] = Boolean.createBoolean(this, val);
            }
            return createVectorOfTables(offsets);
        }
        return 0;
    }

    public void addBoxedShort(int voffset, java.lang.Short val) {
        if (val != null) {
            int offset = Short.createShort(this, val);
            addStruct(voffset, offset, 0);
        }
    }

    /**
     * Reset the FlatBufferBuilder by purging all data that it holds.
     */
    public void clear() {
        flatBufferBuilder.clear();
    }

    /**
     * Offset relative to the end of the buffer.
     *
     * @return Offset relative to the end of the buffer.
     */
    public int offset() {
        return flatBufferBuilder.offset();
    }

    /**
     * Add zero valued bytes to prepare a new entry to be added.
     *
     * @param byte_size Number of bytes to add.
     */
    public void pad(int byte_size) {
        flatBufferBuilder.pad(byte_size);
    }

    /**
     * Prepare to write an element of `size` after `additional_bytes` have been written, e.g. if you
     * write a string, you need to align such the int length field is aligned to {@link
     * Constants#SIZEOF_INT}, and the string data follows it directly.  If all you need to do is
     * alignment, `additional_bytes` will be 0.
     *
     * @param size             This is the of the new element to write.
     * @param additional_bytes The padding size.
     */
    public void prep(int size, int additional_bytes) {
        flatBufferBuilder.prep(size, additional_bytes);
    }

    /**
     * Add a `boolean` to the buffer, backwards from the current location. Doesn't align nor check
     * for space.
     *
     * @param x A `boolean` to put into the buffer.
     */
    public void putBoolean(boolean x) {
        flatBufferBuilder.putBoolean(x);
    }

    /**
     * Add a `byte` to the buffer, backwards from the current location. Doesn't align nor check for
     * space.
     *
     * @param x A `byte` to put into the buffer.
     */
    public void putByte(byte x) {
        flatBufferBuilder.putByte(x);
    }

    /**
     * Add a `short` to the buffer, backwards from the current location. Doesn't align nor check for
     * space.
     *
     * @param x A `short` to put into the buffer.
     */
    public void putShort(short x) {
        flatBufferBuilder.putShort(x);
    }

    /**
     * Add an `int` to the buffer, backwards from the current location. Doesn't align nor check for
     * space.
     *
     * @param x An `int` to put into the buffer.
     */
    public void putInt(int x) {
        flatBufferBuilder.putInt(x);
    }

    /**
     * Add a `long` to the buffer, backwards from the current location. Doesn't align nor check for
     * space.
     *
     * @param x A `long` to put into the buffer.
     */
    public void putLong(long x) {
        flatBufferBuilder.putLong(x);
    }

    /**
     * Add a `float` to the buffer, backwards from the current location. Doesn't align nor check for
     * space.
     *
     * @param x A `float` to put into the buffer.
     */
    public void putFloat(float x) {
        flatBufferBuilder.putFloat(x);
    }

    /**
     * Add a `double` to the buffer, backwards from the current location. Doesn't align nor check
     * for space.
     *
     * @param x A `double` to put into the buffer.
     */
    public void putDouble(double x) {
        flatBufferBuilder.putDouble(x);
    }

    /**
     * Add a `boolean` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `boolean` to put into the buffer.
     */
    public void addBoolean(boolean x) {
        flatBufferBuilder.addBoolean(x);
    }

    /**
     * Add a `byte` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `byte` to put into the buffer.
     */
    public void addByte(byte x) {
        flatBufferBuilder.addByte(x);
    }
    /// @endcond

    /**
     * Add a `short` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `short` to put into the buffer.
     */
    public void addShort(short x) {
        flatBufferBuilder.addShort(x);
    }

    /**
     * Add an `int` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x An `int` to put into the buffer.
     */
    public void addInt(int x) {
        flatBufferBuilder.addInt(x);
    }

    /**
     * Add a `long` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `long` to put into the buffer.
     */
    public void addLong(long x) {
        flatBufferBuilder.addLong(x);
    }

    /**
     * Add a `float` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `float` to put into the buffer.
     */
    public void addFloat(float x) {
        flatBufferBuilder.addFloat(x);
    }

    /**
     * Add a `double` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `double` to put into the buffer.
     */
    public void addDouble(double x) {
        flatBufferBuilder.addDouble(x);
    }

    /**
     * Adds on offset, relative to where it will be written.
     *
     * @param off The offset to add.
     */
    public void addOffset(int off) {
        flatBufferBuilder.addOffset(off);
    }

    /**
     * Start a new array/vector of objects.  Users usually will not call this directly.  The
     * `FlatBuffers` compiler will create a start/end method for vector types in generated code.
     * <p>
     * The expected sequence of calls is:
     * <ol>
     * <li>Start the array using this method.</li>
     * <li>Call {@link #addOffset(int)} `num_elems` number of times to set
     * the offset of each element in the array.</li>
     * <li>Call {@link #endVector()} to retrieve the offset of the array.</li>
     * </ol>
     * <p>
     * For example, to create an array of strings, do:
     * <pre>{@code
     * // Need 10 strings
     * FlatBufferBuilder builder = new FlatBufferBuilder(existingBuffer);
     * int[] offsets = new int[10];
     *
     * for (int i = 0; i < 10; i++) {
     *   offsets[i] = fbb.createString(" " + i);
     * }
     *
     * // Have the strings in the buffer, but don't have a vector.
     * // Add a vector that references the newly created strings:
     * builder.startVector(4, offsets.length, 4);
     *
     * // Add each string to the newly created vector
     * // The strings are added in reverse order since the buffer
     * // is filled in back to front
     * for (int i = offsets.length - 1; i >= 0; i--) {
     *   builder.addOffset(offsets[i]);
     * }
     *
     * // Finish off the vector
     * int offsetOfTheVector = fbb.endVector();
     * }</pre>
     *
     * @param elem_size The size of each element in the array.
     * @param num_elems The number of elements in the array.
     * @param alignment The alignment of the array.
     */
    public void startVector(int elem_size, int num_elems, int alignment) {
        flatBufferBuilder.startVector(elem_size, num_elems, alignment);
    }

    /**
     * Finish off the creation of an array and all its elements.  The array must be created with
     * {@link #startVector(int, int, int)}.
     *
     * @return The offset at which the newly created array starts.
     * @see #startVector(int, int, int)
     */
    public int endVector() {
        return flatBufferBuilder.endVector();
    }

    /// @cond FLATBUFFERS_INTERNAL

    /**
     * Create a new array/vector and return a ByteBuffer to be filled later. Call {@link #endVector}
     * after this method to get an offset to the beginning of vector.
     *
     * @param elem_size the size of each element in bytes.
     * @param num_elems number of elements in the vector.
     * @param alignment byte alignment.
     * @return ByteBuffer with position and limit set to the space allocated for the array.
     */
    public ByteBuffer createUnintializedVector(int elem_size, int num_elems, int alignment) {
        return flatBufferBuilder.createUnintializedVector(elem_size, num_elems, alignment);
    }

    /**
     * Create a vector of tables.
     *
     * @param offsets Offsets of the tables.
     * @return Returns offset of the vector.
     */
    public int createVectorOfTables(int[] offsets) {
        return flatBufferBuilder.createVectorOfTables(offsets);
    }
    /// @endcond

    /**
     * Create a vector of sorted by the key tables.
     *
     * @param obj     Instance of the table subclass.
     * @param offsets Offsets of the tables.
     * @return Returns offset of the sorted vector.
     */
    public <T extends Table> int createSortedVectorOfTables(T obj, int[] offsets) {
        return flatBufferBuilder.createSortedVectorOfTables(obj, offsets);
    }

    /**
     * Encode the string `s` in the buffer using UTF-8.  If {@code s} is already a {@link
     * CharBuffer}, this method is allocation free.
     *
     * @param s The string to encode.
     * @return The offset in the buffer where the encoded string starts.
     */
    public int createString(CharSequence s) {
        if (s == null) {
            return 0;
        }
        int offset = 0;
        offset = flatBufferBuilder.createString(s);
        return offset;

    }

    /**
     * Create a string in the buffer from an already encoded UTF-8 string in a ByteBuffer.
     *
     * @param s An already encoded UTF-8 string as a `ByteBuffer`.
     * @return The offset in the buffer where the encoded string starts.
     */
    public int createString(ByteBuffer s) {
        return flatBufferBuilder.createString(s);
    }

    /**
     * Create a byte array in the buffer.
     *
     * @param arr A source array with data
     * @return The offset in the buffer where the encoded array starts.
     */
    public int createByteVector(byte[] arr) {
        return flatBufferBuilder.createByteVector(arr);
    }

    /**
     * Should not be accessing the final buffer before it is finished.
     */
    public void finished() {

        flatBufferBuilder.finished();
    }

    /**
     * Should not be creating any other object, string or vector while an object is being
     * constructed.
     */
    public void notNested() {
        flatBufferBuilder.notNested();
    }

    /// @cond FLATBUFFERS_INTERNAL

    /**
     * Structures are always stored inline, they need to be created right where they're used. You'll
     * get this assertion failure if you created it elsewhere.
     *
     * @param obj The offset of the created object.
     */
    public void Nested(int obj) {
        flatBufferBuilder.Nested(obj);
    }

    /**
     * Start encoding a new object in the buffer.  Users will not usually need to call this
     * directly. The `FlatBuffers` compiler will generate helper methods that call this method
     * internally.
     * <p>
     * For example, using the "Monster" code found on the "landing page". An object of type
     * `Monster` can be created using the following code:
     *
     * <pre>{@code
     * int testArrayOfString = Monster.createTestarrayofstringVector(fbb, new int[] {
     *   fbb.createString("test1"),
     *   fbb.createString("test2")
     * });
     *
     * Monster.startMonster(fbb);
     * Monster.addPos(fbb, Vec3.createVec3(fbb, 1.0f, 2.0f, 3.0f, 3.0,
     *   Color.Green, (short)5, (byte)6));
     * Monster.addHp(fbb, (short)80);
     * Monster.addName(fbb, str);
     * Monster.addInventory(fbb, inv);
     * Monster.addTestType(fbb, (byte)Any.Monster);
     * Monster.addTest(fbb, mon2);
     * Monster.addTest4(fbb, test4);
     * Monster.addTestarrayofstring(fbb, testArrayOfString);
     * int mon = Monster.endMonster(fbb);
     * }</pre>
     * <p>
     * Here:
     * <ul>
     * <li>The call to `Monster#startMonster(FlatBufferBuilder)` will call this
     * method with the right number of fields set.</li>
     * <li>`Monster#endMonster(FlatBufferBuilder)` will ensure {@link #endObject()} is called.</li>
     * </ul>
     * <p>
     * It's not recommended to call this method directly.  If it's called manually, you must ensure
     * to audit all calls to it whenever fields are added or removed from your schema.  This is
     * automatically done by the code generated by the `FlatBuffers` compiler.
     *
     * @param numfields The number of fields found in this object.
     */
    public void startObject(int numfields) {
        flatBufferBuilder.startObject(numfields);
    }

    /**
     * Add a `boolean` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x A `boolean` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d A `boolean` default value to compare against when `force_defaults` is `false`.
     */
    public void addBoolean(int o, boolean x, boolean d) {
        flatBufferBuilder.addBoolean(o, x, d);
    }

    /**
     * Add a `byte` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x A `byte` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d A `byte` default value to compare against when `force_defaults` is `false`.
     */
    public void addByte(int o, byte x, int d) {
        flatBufferBuilder.addByte(o, x, d);
    }

    /**
     * Add a `short` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x A `short` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d A `short` default value to compare against when `force_defaults` is `false`.
     */
    public void addShort(int o, short x, int d) {
        flatBufferBuilder.addShort(o, x, d);
    }

    /**
     * Add an `int` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x An `int` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d An `int` default value to compare against when `force_defaults` is `false`.
     */
    public void addInt(int o, int x, int d) {
        flatBufferBuilder.addInt(o, x, d);
    }

    /**
     * Add a `long` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x A `long` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d A `long` default value to compare against when `force_defaults` is `false`.
     */
    public void addLong(int o, long x, long d) {
        flatBufferBuilder.addLong(o, x, d);
    }

    /**
     * Add a `float` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x A `float` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d A `float` default value to compare against when `force_defaults` is `false`.
     */
    public void addFloat(int o, float x, double d) {
        flatBufferBuilder.addFloat(o, x, d);
    }

    /**
     * Add a `double` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x A `double` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d A `double` default value to compare against when `force_defaults` is `false`.
     */
    public void addDouble(int o, double x, double d) {
        flatBufferBuilder.addDouble(o, x, d);
    }

    /**
     * Add an `offset` to a table at `o` into its vtable, with value `x` and default `d`.
     *
     * @param o The index into the vtable.
     * @param x An `offset` to put into the buffer, depending on how defaults are handled. If
     *          `force_defaults` is `false`, compare `x` against the default value `d`. If `x` contains the
     *          default value, it can be skipped.
     * @param d An `offset` default value to compare against when `force_defaults` is `false`.
     */
    public void addOffset(int o, int x, int d) {
        flatBufferBuilder.addOffset(o, x, d);
    }

    /**
     * Add a struct to the table. Structs are stored inline, so nothing additional is being added.
     *
     * @param voffset The index into the vtable.
     * @param x       The offset of the created struct.
     * @param d       The default value is always `0`.
     */
    public void addStruct(int voffset, int x, int d) {
        flatBufferBuilder.addStruct(voffset, x, d);
    }

    /**
     * Set the current vtable at `voffset` to the current location in the buffer.
     *
     * @param voffset The index into the vtable to store the offset relative to the end of the
     *                buffer.
     */
    public void slot(int voffset) {
        flatBufferBuilder.slot(voffset);
    }

    /**
     * Finish off writing the object that is under construction.
     *
     * @return The offset to the object inside {@link #dataBuffer()}.
     * @see #startObject(int)
     */
    public int endObject() {
        return flatBufferBuilder.endObject();
    }

    /**
     * Checks that a required field has been set in a given table that has just been constructed.
     *
     * @param table The offset to the start of the table from the `ByteBuffer` capacity.
     * @param field The offset to the field in the vtable.
     */
    public void required(int table, int field) {
        flatBufferBuilder.required(table, field);
    }


    /**
     * Finalize a buffer, pointing to the given `root_table`.
     *
     * @param root_table An offset to be added to the buffer.
     */
    public void finish(int root_table) {
        flatBufferBuilder.finish(root_table);
    }
    /// @endcond

    /**
     * Finalize a buffer, pointing to the given `root_table`, with the size prefixed.
     *
     * @param root_table An offset to be added to the buffer.
     */
    public void finishSizePrefixed(int root_table) {
        flatBufferBuilder.finishSizePrefixed(root_table);
    }


    /**
     * Finalize a buffer, pointing to the given `root_table`.
     *
     * @param root_table      An offset to be added to the buffer.
     * @param file_identifier A FlatBuffer file identifier to be added to the buffer before
     *                        `root_table`.
     */
    public void finish(int root_table, String file_identifier) {
        flatBufferBuilder.finish(root_table, file_identifier);
    }

    /**
     * Finalize a buffer, pointing to the given `root_table`, with the size prefixed.
     *
     * @param root_table      An offset to be added to the buffer.
     * @param file_identifier A FlatBuffer file identifier to be added to the buffer before
     *                        `root_table`.
     */
    public void finishSizePrefixed(int root_table, String file_identifier) {
        flatBufferBuilder.finishSizePrefixed(root_table, file_identifier);
    }

    /**
     * In order to save space, fields that are set to their default value don't get serialized into
     * the buffer. Forcing defaults provides a way to manually disable this optimization.
     *
     * @param forceDefaults When set to `true`, always serializes default values.
     * @return Returns `this`.
     */
    public FlatBufferBuilder forceDefaults(boolean forceDefaults) {
        return flatBufferBuilder.forceDefaults(forceDefaults);
    }

    /**
     * Get the ByteBuffer representing the FlatBuffer. Only call this after you've called
     * `finish()`. The actual data starts at the ByteBuffer's current position, not necessarily at
     * `0`.
     *
     * @return The {@link ByteBuffer} representing the FlatBuffer
     */
    public ByteBuffer dataBuffer() {
        return flatBufferBuilder.dataBuffer();
    }


    /**
     * A utility function to copy and return the ByteBuffer data from `start` to `start` + `length`
     * as a `byte[]`.
     *
     * @param start  Start copying at this offset.
     * @param length How many bytes to copy.
     * @return A range copy of the {@link #dataBuffer() data buffer}.
     * @throws IndexOutOfBoundsException If the range of bytes is ouf of bound.
     */
    public byte[] sizedByteArray(int start, int length) {
        return flatBufferBuilder.sizedByteArray(start, length);
    }

    /**
     * A utility function to copy and return the ByteBuffer data as a `byte[]`.
     *
     * @return A full copy of the {@link #dataBuffer() data buffer}.
     */
    public byte[] sizedByteArray() {
        return flatBufferBuilder.sizedByteArray();
    }

    /**
     * A utility function to return an InputStream to the ByteBuffer data
     *
     * @return An InputStream that starts at the beginning of the ByteBuffer data and can read to
     * the end of it.
     */
    public InputStream sizedInputStream() {
        return flatBufferBuilder.sizedInputStream();
    }


    public int addIntArray(int[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_INT, array.length, Constants.SIZEOF_INT);
            for (int i = array.length - 1; i >= 0; i--) {
                addInt(array[i]);
            }
            return endVector();
        }

        return 0;
    }

    public int addByteArray(byte[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_BYTE, array.length, Constants.SIZEOF_BYTE);
            for (int i = array.length - 1; i >= 0; i--) {
                addByte(array[i]);
            }
            return endVector();
        }

        return 0;
    }


    public int addBooleanArray(boolean[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_BYTE, array.length, Constants.SIZEOF_BYTE);
            for (int i = array.length - 1; i >= 0; i--) {
                addBoolean(array[i]);
            }
            return endVector();
        }
        return 0;
    }

    public int addShortArray(short[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_SHORT, array.length, Constants.SIZEOF_SHORT);
            for (int i = array.length - 1; i >= 0; i--) {
                addShort(array[i]);
            }
            return endVector();
        }
        return 0;
    }

    public int addLongArray(long[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_LONG, array.length, Constants.SIZEOF_LONG);
            for (int i = array.length - 1; i >= 0; i--) {
                addLong(array[i]);
            }
            return endVector();
        }
        return 0;
    }

    public int addFloatArray(float[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_FLOAT, array.length, Constants.SIZEOF_FLOAT);
            for (int i = array.length - 1; i >= 0; i--) {
                addFloat(array[i]);
            }
            return endVector();
        }
        return 0;
    }

    public int addDoubleArray(double[] array) {
        if (array != null) {
            startVector(Constants.SIZEOF_DOUBLE, array.length, Constants.SIZEOF_DOUBLE);
            for (int i = array.length - 1; i >= 0; i--) {
                addDouble(array[i]);
            }
            return endVector();
        }
        return 0;
    }


}
