package us.aldwin.test;

import org.junit.jupiter.api.Test;

public class PlaceholderTwoJavaTest {
  @Test
  public void itReturnsTheExpectedInformation() {
    assert "Placeholder: Initial Version (2)".equals(PlaceholderTwo.placeholder());
  }

  @SuppressWarnings("AccessStaticViaInstance")
  @Test
  public void itReturnsTheExpectedInformationUsingInstance() {
    assert "Placeholder: Initial Version (2)".equals(PlaceholderTwo.INSTANCE.placeholder());
  }

  @Test
  public void itReturnsTheExpectedInformationFromTheOriginalPlaceholder() {
    assert "Placeholder: Initial Version".equals(PlaceholderTwo.originalPlaceholder());
  }

  @SuppressWarnings("AccessStaticViaInstance")
  @Test
  public void itReturnsTheExpectedInformationFromTheOriginalPlaceholderUsingInstance() {
    assert "Placeholder: Initial Version".equals(PlaceholderTwo.INSTANCE.originalPlaceholder());
  }
}
