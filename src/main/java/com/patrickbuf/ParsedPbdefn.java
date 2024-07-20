package com.patrickbuf;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/** Representation of a .pbdefn file, after parsing. */
@AutoValue
abstract class ParsedPbdefn {
  abstract String templateName();

  abstract ImmutableList<Field> fields();

  static Builder builder() {
    return new AutoValue_ParsedPbdefn.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder setTemplateName(String value);

    abstract ImmutableList.Builder<Field> fieldsBuilder();

    final Builder addField(Field value) {
      fieldsBuilder().add(value);
      return this;
    }

    abstract ParsedPbdefn build();
  }
}
