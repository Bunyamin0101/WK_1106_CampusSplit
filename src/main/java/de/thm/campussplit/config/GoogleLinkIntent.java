package de.thm.campussplit.config;

public record GoogleLinkIntent(String email, java.time.Instant expires, String state)
    implements java.io.Serializable {
  public static final String KEY = "campussplit.google.link";
}
