package com.github.mrs.functionaltest.utils.db;

public class DataField {
  private String path;
  private Object value;

  public String getPath() {
    return path;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public DataField(String path, Object value) {
    this.path = path;
    this.value = value;
  }

}
