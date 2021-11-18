package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;

public class AppStateListenerWrapper implements AppStateListener {

  private AppStartListener startListener;
  private AppStopListener stopListener;

  public AppStateListenerWrapper(AppStartListener startListener) {
    this(startListener, null);
  }

  public AppStateListenerWrapper(AppStopListener stopListener) {
    this(null, stopListener);
  }

  public AppStateListenerWrapper(AppStartListener startListener, AppStopListener stopListener) {
    this.startListener = startListener;
    this.stopListener = stopListener;
  }

  @Override
  public void onAppStart(ApplicationReadyEvent event) {
    AppStartListener sl = this.startListener;
    if (sl != null) {
      try {
        sl.onAppStart(event);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onAppStop(ContextClosedEvent event) throws Exception {
    AppStopListener sl = this.stopListener;
    if (sl != null) {
      try {
        sl.onAppStop(event);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
