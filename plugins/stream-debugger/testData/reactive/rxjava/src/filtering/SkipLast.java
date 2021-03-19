package filtering;

import io.reactivex.rxjava3.core.Observable;

public class SkipLast {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4, 5, 6, 7)
      .skipLast(2)
      .subscribe();
  }
}
