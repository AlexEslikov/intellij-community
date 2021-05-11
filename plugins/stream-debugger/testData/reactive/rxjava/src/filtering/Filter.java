package filtering;

import io.reactivex.rxjava3.core.Observable;

public class Filter {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4, 5)
      .filter(x -> x % 2 == 0)
      .subscribe();
  }
}
