// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package flatMapping;

import reactor.core.publisher.Flux;
import java.util.Arrays;

public class FlatMap {
  public static void main(String[] args) {
    // Breakpoint!
    Flux.just(1, 2, 3, 4)
      .flatMap(x -> Flux.fromIterable(Arrays.asList("a" + x, "b" + x)))
      .subscribe();
  }
}
