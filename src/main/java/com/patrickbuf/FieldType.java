package com.patrickbuf;

/** Representation of a field type, after parsing from protodefn. */
enum FieldType {
  INT;

  static FieldType fromInteger(int i) {
    FieldType[] values = FieldType.values();
    if (i < 0 || i >= values.length) {
      throw new IndexOutOfBoundsException(
          String.format("Tried to get FieldType with value %d, which does not exist", i));
    }
    return values[i];
  }
}
