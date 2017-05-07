package com.github.sunnybat.paxchecker.status;

import java.util.Scanner;

/**
 *
 * @author SunnyBat
 */
public class StatusCLI implements Status {

  private Scanner input;
  private ACTION_TYPE actionRequested;
  private boolean twitterEnabled = false;
  private boolean twitterConnected = false;
  private boolean forceCheckEnabled = true;
  private boolean email = false;
  private boolean alarm = false;

  public StatusCLI() {
    input = new Scanner(System.in);
    Thread scanThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          processCommand(input.nextLine());
        }
      }
    });
    scanThread.setDaemon(true); // So PAXChecker will terminate when all other Threads are dead
    scanThread.start();
  }

  @Override
  public void enableEmail() {
    email = true;
  }

  @Override
  public void enableAlarm() {
    alarm = true;
  }

  @Override
  public void enableTwitter() {
    twitterEnabled = true;
  }

  @Override
  public void setTwitterStatus(boolean enabled) {
    if (enabled) {
      setTwitterStatus("Connected");
    } else {
      setTwitterStatus("Disconnected");
    }
    twitterConnected = enabled;
  }

  @Override
  public void setTwitterStatus(String status) {
    System.out.println("Twitter Status: " + status);
  }

  @Override
  public void twitterStreamKilled() {
    System.out.println("WARNING: The Twitter stream has been killed.");
  }

  @Override
  public void setInformationText(String text) {
    System.out.println(text);
  }

  @Override
  public void setLastCheckedText(String text) {
  }

  @Override
  public void setLastCheckedText(int seconds) {
  }

  @Override
  public void setForceCheckEnabled(boolean enabled) {
    forceCheckEnabled = enabled;
  }

  @Override
  public void setDataUsageText(double dataUsed) {
  }

  @Override
  public void setDataUsageText(String dataUsed) {
  }

  @Override
  public ACTION_TYPE getActionRequested() {
    return actionRequested;
  }

  @Override
  public void resetButtonPressed() {
    actionRequested = null;
  }

  private void actionRequested(ACTION_TYPE button) {
    synchronized (this) {
      this.actionRequested = button;
      this.notify();
    }
  }

  private void processCommand(String command) {
    switch (command.toLowerCase()) {
      case "refresh":
        if (forceCheckEnabled) {
          actionRequested(ACTION_TYPE.FORCE_CHECK);
        } else {
          System.out.println("Force checking is currently disabled (probably checking right now)");
        }
        break;
      case "testtext":
        if (email) {
          actionRequested(ACTION_TYPE.TEST_TEXT);
        } else {
          System.out.println("Email is disabled");
        }
        break;
      case "testalarm":
        if (alarm) {
          actionRequested(ACTION_TYPE.TEST_ALARM);
        } else {
          System.out.println("Alarm is disabled.");
        }
        break;
      case "reconnecttwitter":
        if (twitterEnabled) {
          if (twitterConnected) {
            System.out.println("Twitter is already enabled!");
          } else {
            actionRequested(ACTION_TYPE.RECONNECT_TWITTER); // CHECK: Should I enable this right now?
          }
        } else {
          System.out.println("Twitter is disabled.");
        }
        break;
      case "exit":
      case "closeprogram":
        System.exit(0);
        break;
      default:
        System.out.println("==========Current Commands==========");
        System.out.println("refresh           -- Forces a refresh of websites");
        System.out.println("testtext          -- Sends a test text if email is enabled");
        System.out.println("testalarm         -- Plays the alarm sound if the alarm is enabled");
        System.out.println("reconnecttwitter  -- Reconnects to the Twitter stream if it's set up properly and disconnected");
        System.out.println("exit              -- Exits the PAXChecker");
        System.out.println("closeprogram      -- Exits the PAXChecker");
        System.out.println("====================================");
        break;
    }
  }

}
