package com.gimus.permus.api.common;

public class ApiObject {
   public String uid;
   public Object tag;
   public ApiObject() {
       uid=java.util.UUID.randomUUID().toString();
   }
}
