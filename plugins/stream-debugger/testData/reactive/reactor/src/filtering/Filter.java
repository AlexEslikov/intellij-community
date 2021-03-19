// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package filtering;

import reactor.core.publisher.Flux;

public class Filter {
  public static void main(String[] args) {
    // Breakpoint!
    Flux.just(1, 2, 3, 4)
      .filter(x -> x > 1)
      .subscribe();
  }
}
