package com.patrickbuf;

import static com.google.common.truth.Truth.assertThat;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

public class JavaSourceGenTest {

  private static boolean isPublicInt(java.lang.reflect.Field f) {
    int modifiers = f.getModifiers();
    return Modifier.isPublic(modifiers)
        && !Modifier.isStatic(modifiers)
        && f.getAnnotatedType().getType().getTypeName().equals("int");
  }

  @Test
  void generate_create() throws Exception {
    Class<?> clazz = GenerateAndLoadUtil.loadDateClass();

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
