package com.patrickbuf;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class JavaSourceGenTest {
  @Test
  void generate() throws IOException {
    ParsedPbdefn pbdefn =
        ParsedPbdefn.builder()
            .setTemplateName("Date")
            .addField(Field.create(0, FieldType.INT, "month"))
            .addField(Field.create(1, FieldType.INT, "day"))
            .addField(Field.create(2, FieldType.INT, "year"))
            .build();
    Path tempDir = Files.createTempDirectory("tempDir");

    assertThat(JavaSourceGen.generate(pbdefn, tempDir)).isTrue();
  }
}
