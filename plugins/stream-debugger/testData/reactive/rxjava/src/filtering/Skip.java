package filtering;

import io.reactivex.rxjava3.core.Observable;

public class Skip {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4, 5, 6, 7)
      .skip(2)
      .subscribe();
  }
}
