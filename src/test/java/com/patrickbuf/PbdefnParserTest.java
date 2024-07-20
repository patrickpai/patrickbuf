package com.patrickbuf;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class PbdefnParserTest {
  @Test
  void parse_realFile() throws IOException, InvalidPbdefnException {
    Path path = Paths.get("src/test/java/com/patrickbuf/valid.pbdefn");

    ParsedPbdefn result = PbdefnParser.parse(path);

    assertThat(result.templateName()).isEqualTo("Date");
    assertThat(result.fields()).hasSize(3);
    assertThat(result.fields())
        .containsExactly(
            Field.create(0, FieldType.INT, "month"),
            Field.create(1, FieldType.INT, "day"),
            Field.create(2, FieldType.INT, "year"));
  }
}
