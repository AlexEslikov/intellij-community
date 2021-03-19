// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package distinct;

import reactor.core.publisher.Flux;

public class Distinct {
  public static void main(String[] args) {
    // Breakpoint!
    Flux.just(1, 1, 2, 1, 3, 1, 2, 2, 3, 2)
      .distinct()
      .subscribe();
  }
}
