package com.patrickbuf;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class EndecoderTest {

  @Test
  void encodeThenDecode() throws Exception {
    Class<?> clazz = GenerateAndLoadUtil.loadDateClass();
    int arbitraryMonth = 6;
    int arbitraryDay = 30;
    int arbitraryYear = 2024;
    Object objToEncode =
        clazz
            .getMethod("create", int.class, int.class, int.class)
            .invoke(/* obj= */ null, arbitraryMonth, arbitraryDay, arbitraryYear);

    byte[] data = Endecoder.encode(objToEncode);
    Object decodedObj =
        clazz.getMethod("create", int.class, int.class, int.class).invoke(/* obj= */ null, 0, 0, 0);
    Endecoder.decode(data, decodedObj);

    java.lang.reflect.Field month = clazz.getField("month");
    java.lang.reflect.Field day = clazz.getField("day");
    java.lang.reflect.Field year = clazz.getField("year");
    assertThat(month.getInt(decodedObj)).isEqualTo(arbitraryMonth);
    assertThat(day.getInt(decodedObj)).isEqualTo(arbitraryDay);
    assertThat(year.getInt(decodedObj)).isEqualTo(arbitraryYear);
  }
}
