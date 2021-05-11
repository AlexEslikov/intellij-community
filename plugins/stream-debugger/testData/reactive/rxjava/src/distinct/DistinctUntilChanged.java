package distinct;

import io.reactivex.rxjava3.core.Observable;

public class DistinctUntilChanged {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 1, 2, 1, 3, 1, 2, 2, 3, 2)
      .distinctUntilChanged()
      .subscribe();
  }
}
