package com.appcatalog.error;

public class JobNotFoundException extends RuntimeException {
  public JobNotFoundException(String message) {
    super(message);
  }
}
