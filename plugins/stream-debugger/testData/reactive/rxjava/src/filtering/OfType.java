package filtering;

import io.reactivex.rxjava3.core.Observable;

public class OfType {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just("1", 2, "3", 4, "5")
      .ofType(String.class)
      .subscribe();
  }
}
