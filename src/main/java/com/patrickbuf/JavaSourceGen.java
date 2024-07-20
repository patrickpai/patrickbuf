package com.patrickbuf;

import com.squareup.javapoet.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

final class JavaSourceGen {

  /** Generates a Java source file from the {@code pbdefn} and writes it to {@code out}. */
  static boolean generate(ParsedPbdefn pbdefn, Path out) throws IOException {
    // Define the class
    TypeSpec.Builder templateClass =
        TypeSpec.classBuilder(pbdefn.templateName())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(JavaSourceGen.privateConstructor(pbdefn))
            .addMethod(JavaSourceGen.create(pbdefn));

    // Define instance variables
    for (Field field : pbdefn.fields()) {
      FieldSpec fieldSpec =
          FieldSpec.builder(TypeName.INT, field.name()).addModifiers(Modifier.PUBLIC).build();
      templateClass.addField(fieldSpec);
    }

    // Generate the Java source code
    JavaFile javaFile = JavaFile.builder("com.example.helloworld", templateClass.build()).build();
    javaFile.writeTo(out);

    return true;
  }

  private static MethodSpec privateConstructor(ParsedPbdefn pbdefn) {
    MethodSpec.Builder privateConstructor =
        MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

    pbdefn.fields().stream()
        .forEach(
            field -> {
              privateConstructor.addParameter(TypeName.INT, field.name());
              privateConstructor.addStatement("this.$L = $L", field.name(), field.name());
            });

    return privateConstructor.build();
  }

  private static MethodSpec create(ParsedPbdefn pbdefn) {
    MethodSpec.Builder create =
        MethodSpec.methodBuilder("create")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("", pbdefn.templateName()));

    pbdefn.fields().stream().forEach(field -> create.addParameter(TypeName.INT, field.name()));
    create.addStatement(
        "return new $L($L)",
        pbdefn.templateName(),
        JavaSourceGen.getCanonicalParameterList(pbdefn));

    return create.build();
  }

  /**
   * Returns the list of parameters suitably ordered for passing into a private constructor
   * invocation.
   */
  private static String getCanonicalParameterList(ParsedPbdefn pbdefn) {
    return pbdefn.fields().stream().map(Field::name).collect(Collectors.joining(", "));
  }
}
