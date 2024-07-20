package com.patrickbuf;

/** Exception thrown when a .pbdefn file exists, but whose format is invalid. */
public class InvalidPbdefnException extends Exception {
  public InvalidPbdefnException(String message) {
    super(message);
  }
}
