package flatMapping;

import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;

public class ConcatMap {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4)
      .concatMap(x -> Observable.fromIterable(Arrays.asList("a" + x, "b" + x)))
      .subscribe();
  }
}
