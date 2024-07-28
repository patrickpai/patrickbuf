package com.patrickbuf;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/** Test util for generating and loading various templates. */
final class GenerateAndLoadUtil {

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

  static Class<?> loadDateClass() throws IOException, ClassNotFoundException {
    String templateName = "Date";
    ParsedPbdefn pbdefn =
        ParsedPbdefn.builder()
            .setTemplateName(templateName)
            .addField(Field.create(0, FieldType.INT, "month"))
            .addField(Field.create(1, FieldType.INT, "day"))
            .addField(Field.create(2, FieldType.INT, "year"))
            .build();
    Path tempDir = Files.createTempDirectory(/* prefix= */ null);
    JavaSourceGen.generate(pbdefn, tempDir);

    Path generatedJavaSrc = tempDir.resolve("com/patrickbuf/Date.java");
    return compileJavaSrcAndLoadCompiledClass(
        /* generatedJavaSrc= */ generatedJavaSrc,
        /* packageName= */ JavaSourceGen.PACKAGE_NAME,
        /* className= */ templateName);
  }
}
