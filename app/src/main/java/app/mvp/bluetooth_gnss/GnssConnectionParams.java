package app.mvp.bluetooth_gnss;

import java.util.HashMap;
import java.util.Map;


public class GnssConnectionParams {
  public String getBdaddr() {
    return bdaddr;
  }

  public void setBdaddr(String bdaddr) {
    this.bdaddr = bdaddr;
  }

  public boolean isSecure() {
    return secure;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  public boolean isReconnect() {
    return reconnect;
  }

  public void setReconnect(boolean reconnect) {
    this.reconnect = reconnect;
  }

  public boolean isLogBtRx() {
    return logBtRx;
  }

  public void setLogBtRx(boolean logBtRx) {
    this.logBtRx = logBtRx;
  }

  public boolean isDisableNtrip() {
    return disableNtrip;
  }

  public void setDisableNtrip(boolean disableNtrip) {
    this.disableNtrip = disableNtrip;
  }

  public boolean isGapMode() {
    return gapMode;
  }

  public void setGapMode(boolean gapMode) {
    this.gapMode = gapMode;
  }

  public Map<String, String> getExtraParams() {
    return extraParams;
  }

  private String bdaddr;
  private boolean secure;
  private boolean reconnect;
  private boolean logBtRx;
  private boolean disableNtrip;
  private boolean gapMode;
  private final Map<String, String> extraParams = new HashMap<>();
}
