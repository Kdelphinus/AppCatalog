package com.appcatalog.error;

public class TargetNotFoundException extends RuntimeException {
  public TargetNotFoundException(String message) {
    super(message);
  }
}
