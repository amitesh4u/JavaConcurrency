package com.amitesh.concurrency;

import java.io.InputStream;
import java.net.URI;

public class NetworkCaller {

  private final String callName;

  public NetworkCaller(String callName) {
    this.callName = callName;
  }

  public String makeCall(int secs) throws Exception {

    System.out.println(STR."Beginning \{callName} with Thread \{Thread.currentThread()}");

    try {
      URI uri = new URI(STR."http://httpbin.org/delay/\{secs}");
      try (InputStream stream = uri.toURL().openStream()) {
        return new String(stream.readAllBytes());
      }
    } finally {
      System.out.println(STR."Finished \{callName} with Thread \{Thread.currentThread()}");
    }

  }

}
