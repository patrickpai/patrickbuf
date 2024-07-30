package com.patrickbuf;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import net.magik6k.bitbuffer.BitBuffer;

/** Encoding and decoding utility for pbbinary. */
final class Endecoder {

  /** Encodes the {@code templateInstance} to pbbinary. */
  static byte[] encode(Object templateInstance) throws Exception {
    BitBuffer buffer = BitBuffer.allocateDynamic(/* bits= */ Integer.SIZE);

    // ====== HEADER ======
    // Reserve first 4 bytes to store the number of meaningful bits that follow it, because
    // encoding is not guaranteed to align with byte boundaries.
    buffer.putInt(0);
    long positionAfterWritingHeader = buffer.position();

    // ======  BODY  ======
    Class<?> clazz = templateInstance.getClass();
    ImmutableList<Field> fieldNumbers =
        Arrays.stream(clazz.getDeclaredFields())
            .filter(Endecoder::isFieldNumber)
            .collect(toImmutableList());
    for (Field fieldNumber : fieldNumbers) {
      fieldNumber.setAccessible(true); // Intentionally access private field
      Field field = Endecoder.getFieldFromFieldNumber(clazz, fieldNumber);

      switch (field.getType().getName()) {
        case "int":
          buffer.putInt(/* number= */ fieldNumber.getInt(templateInstance), /* bits= */ 4);
          buffer.putInt(/* number= */ FieldType.INT.ordinal(), /* bits= */ 3);
          buffer.putInt(field.getInt(templateInstance));
          break;
        default:
          throw new Exception(
              String.format(
                  "Encountered unsupported type %s while encoding", field.getType().getName()));
      }
    }

    long endPosition = buffer.position();
    // Go back and fill in parts of the header that are only known at the end of encoding.
    int numBitsInBody = (int) (endPosition - positionAfterWritingHeader);
    buffer.setPosition(0);
    buffer.putInt(numBitsInBody);
    // Set the position back so the bit buffer knows where our data ends.
    buffer.setPosition(endPosition);

    return buffer.asByteArray();
  }

  /** Decodes the pbbinary {@code data} into the skeleton template instance {@code obj}. */
  static void decode(byte[] data, Object obj) throws Exception {
    BitBuffer buffer = BitBuffer.wrap(data);

    // ====== HEADER ======
    // First 4 bytes indicates the number of meaningful bits that follow it.
    int numBitsInBody = buffer.getInt();
    long positionAfterReadingHeader = buffer.position();

    // ======  BODY  ======
    while (buffer.position() < positionAfterReadingHeader + numBitsInBody) {
      int fieldNumber = buffer.getInt(/* bits= */ 4);
      FieldType fieldType = FieldType.fromInteger(buffer.getInt(/* bits= */ 3));

      Field field = getField(obj, fieldNumber);
      switch (fieldType) {
        case INT:
          field.setInt(obj, buffer.getInt());
          break;
        default:
          throw new Exception(
              String.format("Encountered unsupported type %s while decoding", fieldType));
      }
    }
  }

  private static Field getField(Object obj, int fieldNumber) throws Exception {
    for (Field f : obj.getClass().getDeclaredFields()) {
      if (!Endecoder.isFieldNumber(f)) {
        continue;
      }

      f.setAccessible(true);
      if (f.getInt(obj) == fieldNumber) {
        // Lop off the "Fn" suffix
        return obj.getClass().getField(f.getName().substring(0, f.getName().length() - 2));
      }
    }
    throw new Exception("");
  }

  private static Field getFieldFromFieldNumber(Class<?> clazz, Field fieldNumber)
      throws NoSuchFieldException {
    // Name of field number ends with "Fn"
    String nameOfField = fieldNumber.getName().substring(0, fieldNumber.getName().length() - 2);
    return clazz.getField(nameOfField);
  }

  private static boolean isFieldNumber(Field f) {
    int mod = f.getModifiers();
    return Modifier.isPrivate(mod)
        && f.getAnnotatedType().getType().getTypeName().equals("int")
        && f.getName().endsWith("Fn");
  }
}
