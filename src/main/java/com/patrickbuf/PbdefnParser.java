package com.patrickbuf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PbdefnParser {
  private static final String TEMPLATE_NAME_GROUP_NAME = "templateName";
  private static final String FIELD_NUMBER_GROUP_NAME = "fieldNumber";
  private static final String FIELD_NAME_GROUP_NAME = "fieldName";

  private static final Pattern TEMPLATE_NAME_PATTERN =
      Pattern.compile("^(?<templateName>[a-zA-Z]+)$");
  private static final Pattern FIELD_PATTERN =
      Pattern.compile("^(?<fieldNumber>\\d+):int:(?<fieldName>[a-zA-Z_]+)$");

  private PbdefnParser() {}

  /**
   * Parses a file as a pbdefn.
   *
   * @param path the path of the file to parse
   * @throws IOException if an I/O error occurs while reading from the file
   * @throws InvalidPbdefnException if the file exists but is improperly formatted
   */
  static ParsedPbdefn parse(Path path) throws IOException, InvalidPbdefnException {
    List<String> lines = Files.readAllLines(path);

    if (lines.isEmpty()) {
      throw new InvalidPbdefnException("pbdefn file must be non-empty");
    }
    if (lines.size() < 2) {
      throw new InvalidPbdefnException("pbdefn file must be at least 2 lines long");
    }

    ParsedPbdefn.Builder builder = ParsedPbdefn.builder();

    // First line contains the template name
    Matcher m = TEMPLATE_NAME_PATTERN.matcher(lines.get(0));
    if (!m.matches()) {
      throw new InvalidPbdefnException(
          String.format("Invalid template name on first line of pbdefn file: %s", lines.get(0)));
    }
    builder.setTemplateName(m.group(TEMPLATE_NAME_GROUP_NAME));

    // Remaining lines specify fields
    for (String line : lines.subList(1, lines.size())) {
      m = FIELD_PATTERN.matcher(line);
      if (!m.matches()) {
        throw new InvalidPbdefnException(String.format("Invalid field in pbdefn file: %s", line));
      }
      builder.addField(
          Field.create(
              /* number= */ Integer.parseInt(m.group(FIELD_NUMBER_GROUP_NAME)),
              /* type= */ FieldType.INT,
              /* name= */ m.group(FIELD_NAME_GROUP_NAME)));
    }

    return builder.build();
  }
}
