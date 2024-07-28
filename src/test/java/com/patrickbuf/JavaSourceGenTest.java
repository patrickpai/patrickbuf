package com.patrickbuf;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;

public class JavaSourceGenTest {
  /**
   * Compiles a Java source file into a .class file, and then loads the class.
   *
   * @param generatedJavaSrc the generated Java source file path
   * @param packageName the package of the Java source file
   * @param className the name of the class defined in the Java source file
   * @return the {@link Class} representing the in-memory template instance
   */
  private static Class<?> compileJavaSrcAndLoadCompiledClass(
      Path generatedJavaSrc, String packageName, String className)
      throws ClassNotFoundException, IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    Path tmpDir = Files.createTempDirectory(/* prefix= */ null);
    if (compiler.run(
            /* in= */ null,
            /* out= */ null,
            /* err= */ null,
            generatedJavaSrc.toString(),
            "-d",
            tmpDir.toString())
        != 0) {
      throw new IllegalArgumentException("javac failed to compile generated Java source file");
    }

    try (URLClassLoader loader = URLClassLoader.newInstance(new URL[] {tmpDir.toUri().toURL()})) {
      return loader.loadClass(String.format("%s.%s", packageName, className));
    }
  }

  private static boolean isPublicInt(java.lang.reflect.Field f) {
    int modifiers = f.getModifiers();
    return Modifier.isPublic(modifiers)
        && !Modifier.isStatic(modifiers)
        && f.getAnnotatedType().getType().getTypeName().equals("int");
  }

  @Test
  void generate_create() throws Exception {
    String templateName = "Date";
    ParsedPbdefn pbdefn =
        ParsedPbdefn.builder()
            .setTemplateName(templateName)
            .addField(Field.create(0, FieldType.INT, "month"))
            .addField(Field.create(1, FieldType.INT, "day"))
            .addField(Field.create(2, FieldType.INT, "year"))
            .build();
    Path tempDir = Files.createTempDirectory(/* prefix= */ null);
    Path generatedJavaSrc = tempDir.resolve("com/patrickbuf/Date.java");

    JavaSourceGen.generate(pbdefn, tempDir);
    Class<?> clazz =
        compileJavaSrcAndLoadCompiledClass(
            /* generatedJavaSrc= */ generatedJavaSrc,
            /* packageName= */ JavaSourceGen.PACKAGE_NAME,
            /* className= */ templateName);

    int monthValue = 6;
    int dayValue = 27;
    int yearValue = 2024;
    Object date =
        clazz
            .getMethod("create", int.class, int.class, int.class)
            .invoke(/* obj= */ null, monthValue, dayValue, yearValue);
    java.lang.reflect.Field month = clazz.getField("month");
    java.lang.reflect.Field day = clazz.getField("day");
    java.lang.reflect.Field year = clazz.getField("year");
    assertThat(isPublicInt(month)).isTrue();
    assertThat(isPublicInt(day)).isTrue();
    assertThat(isPublicInt(year)).isTrue();
    assertThat(month.getInt(date)).isEqualTo(monthValue);
    assertThat(day.getInt(date)).isEqualTo(dayValue);
    assertThat(year.getInt(date)).isEqualTo(yearValue);
  }
}
