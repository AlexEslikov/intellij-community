package flatMapping;

import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;

public class FlatMap {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4, 5, 6)
      .flatMap(x -> Observable.fromIterable(Arrays.asList(x + 1, x + 2, x + 3)))
      .subscribe();
  }
}
