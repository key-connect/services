package app.keyconnect.server.exchanges.services;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FanoutObserver<T> implements Observer<T> {

  private static final Logger logger = LoggerFactory.getLogger(FanoutObserver.class);
  private final Set<Observer<T>> observers;

  @SafeVarargs
  public FanoutObserver(Observer<T>... observers) {
    this.observers = new HashSet<>(observers.length);
    Collections.addAll(this.observers, observers);
  }

  private void executeSafely(Runnable r) {
    try {
      r.run();
    } catch (Throwable t) {
      logger.warn("Exception occurred when executing on observer", t);
    }
  }

  @Override
  public void onSubscribe(@NotNull Disposable d) {
    for (Observer<T> observer : this.observers) {
      executeSafely(() -> observer.onSubscribe(d));
    }
  }

  @Override
  public void onNext(@NotNull T t) {
    for (Observer<T> observer : this.observers) {
      executeSafely(() -> observer.onNext(t));
    }
  }

  @Override
  public void onError(@NotNull Throwable e) {
    for (Observer<T> observer : this.observers) {
      executeSafely(() -> observer.onError(e));
    }
  }

  @Override
  public void onComplete() {
    for (Observer<T> observer : this.observers) {
      executeSafely(observer::onComplete);
    }
  }

  public void addObservable(Observer<T> observable) {
    this.observers.add(observable);
  }
}
