package com.patrickbuf;

import static com.google.common.truth.Truth.assertThat;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JavaSourceGenTest {
  private static Class<?> dateClazz;

  private static boolean isPublicInt(java.lang.reflect.Field f) {
    int mod = f.getModifiers();
    return Modifier.isPublic(mod)
        && !Modifier.isStatic(mod)
        && f.getAnnotatedType().getType().getTypeName().equals("int");
  }

  @BeforeAll
  static void setUp() throws Exception {
    dateClazz = GenerateAndLoadUtil.loadDateClass();
  }

  @Test
  void generate_create() throws Exception {
    int monthValue = 1;
    int dayValue = 1;
    int yearValue = 2000;
    Object date =
        dateClazz
            .getMethod("create", int.class, int.class, int.class)
            .invoke(/* obj= */ null, monthValue, dayValue, yearValue);
    java.lang.reflect.Field month = dateClazz.getField("month");
    java.lang.reflect.Field day = dateClazz.getField("day");
    java.lang.reflect.Field year = dateClazz.getField("year");
    assertThat(isPublicInt(month)).isTrue();
    assertThat(isPublicInt(day)).isTrue();
    assertThat(isPublicInt(year)).isTrue();
    assertThat(month.getInt(date)).isEqualTo(monthValue);
    assertThat(day.getInt(date)).isEqualTo(dayValue);
    assertThat(year.getInt(date)).isEqualTo(yearValue);
  }

  @Ignore
  void generate_writeToDiskAndReadFromDisk() throws Exception {
    Path tmpFile =
        Files.createTempFile(/* prefix= */ "paris_olympics_start_date", /* suffix= */ "pbbinary");
    int monthValue = 7;
    int dayValue = 26;
    int yearValue = 2024;
    Object expected =
        dateClazz
            .getMethod("create", int.class, int.class, int.class)
            .invoke(/* obj= */ null, monthValue, dayValue, yearValue);

    dateClazz
        .getMethod("writeToDisk", Path.class, dateClazz)
        .invoke(/* obj= */ null, tmpFile, expected);
    Object actual =
        dateClazz.getMethod("readFromDisk", Path.class).invoke(/* obj= */ null, tmpFile);

    java.lang.reflect.Field month = dateClazz.getField("month");
    java.lang.reflect.Field day = dateClazz.getField("day");
    java.lang.reflect.Field year = dateClazz.getField("year");
    assertThat(month.getInt(actual)).isEqualTo(month.getInt(expected));
    assertThat(day.getInt(actual)).isEqualTo(day.getInt(expected));
    assertThat(year.getInt(actual)).isEqualTo(year.getInt(expected));
  }

  @Test
  void generate_toString() throws Exception {
    int monthValue = 1;
    int dayValue = 1;
    int yearValue = 2000;
    Object date =
        dateClazz
            .getMethod("create", int.class, int.class, int.class)
            .invoke(/* obj= */ null, monthValue, dayValue, yearValue);

    String str = (String) dateClazz.getMethod("toString").invoke(/* obj= */ date);

    assertThat(str).isEqualTo("Date(month: 1, day: 1, year: 2000)");
  }
}
