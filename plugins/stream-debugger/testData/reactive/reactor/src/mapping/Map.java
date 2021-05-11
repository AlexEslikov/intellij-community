// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mapping;

import reactor.core.publisher.Flux;

public class Map {
  public static void main(String[] args) {
    // Breakpoint!
    Flux.just(1, 2, 3, 4)
      .map(x -> 0)
      .subscribe();
  }
}
