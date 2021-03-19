package mapping;

import io.reactivex.rxjava3.core.Observable;

public class Map {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4, 5, 6).map(x -> 0).subscribe();
  }
}
