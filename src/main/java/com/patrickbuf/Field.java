package com.patrickbuf;

import com.google.auto.value.AutoValue;

/** Representation of a field, after parsing from protodefn. */
@AutoValue
abstract class Field {
  static Field create(int number, FieldType type, String name) {
    return new AutoValue_Field(number, type, name);
  }

  abstract int number();

  abstract FieldType type();

  abstract String name();
}
