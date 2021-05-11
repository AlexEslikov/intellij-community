// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mapping;

import io.reactivex.rxjava3.core.Observable;

public class Cast {
  public static void main(String[] args) {
    // Breakpoint!
    Observable.just(1, 2, 3, 4, 5, 6)
      .cast(Number.class)
      .subscribe();
  }
}
