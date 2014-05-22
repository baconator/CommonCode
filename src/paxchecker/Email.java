package paxchecker;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author SunnyBat
 */
public class Email {

  private static String host, port, username, password, textEmail;
  private static List<String> emailList = new ArrayList<>();
  private static Properties props = System.getProperties();
  private static Session l_session = null;

  public static void init() {
    host = "smtp.mail.yahoo.com";
    port = "587";
    emailSettings();
  }

  public static void setUsername(String user) {
    if (user == null) {
      username = null;
      props.put("mail.smtp.user", username);
      return;
    }
    if (!user.contains("@")) {
      user += "@yahoo.com";
    } else if (user.toLowerCase().contains("@gmail.com")) {
      setHost("smtp.gmail.com");
    } else if (!user.toLowerCase().contains("@yahoo.com")) {
      System.out.println("ERROR: Yahoo or Google email required!");
      System.exit(0);
    }
    System.out.println("Username = " + user);
    username = user;
    props.put("mail.smtp.user", username);
  }

  public static String getUsername() {
    return username;
  }

  public static void setHost(String h) {
    host = h;
  }

  public static void setPassword(String pass) {
    if (pass.length() < 8) {
      System.out.println("Password seems weak, >=8 characters is recommended.");
    }
    password = pass;
    props.put("mail.smtp.password", password);
  }

  /**
   * Sets the email address that will be mailed to. This method defaults to
   *
   * @mms.att.net if no
   * extension is specified. While this can be called at any time, it is recommended to only call
   * during Setup.
   * @param num
   */
  @Deprecated
  public static void setCellNum(String num) {
    if (!num.contains("@")) {
      num += "@mms.att.net";
    }
    textEmail = num;
    System.out.println("textEmail = " + textEmail);
  }

  /**
   * <HTML>Sets {@link #textEmail} to the specified email address. If no [AT] symbol is in {@link num},
   * {@link carrier} is used to add the correct carrier email ending to the number. If an invalid
   * carrier is specified, the method defaults to AT&T.<br>
   * Note that this sets {@link #emailList} to null.</HTML>
   * @param num
   * @param carrier 
   */
  public static void setCellNum(String num, String carrier) {
    if (num == null) {
      textEmail = null;
      return;
    } else if (num.length() == 0) {
      textEmail = null;
      return;
    }
    if (!num.contains("@")) {
      switch (carrier) {
        case "AT&T":
          num += "@mms.att.net";
          break;
        case "Verizon":
          num += "@vtext.com";
          break;
        case "Sprint":
          num += "@messaging.sprintpcs.com";
          break;
        case "T-Mobile":
          num += "@tmomail.net";
          break;
        case "U.S. Cellular":
          num += "@email.uscc.net";
          break;
        default:
          System.out.println("ERROR: Unable to identify carrier. Using default AT&T.");
          setCellNum(num, "AT&T");
          return;
      }
    }
    textEmail = num;
    System.out.println("textEmail = " + textEmail);
    //emailList = null;
  }

  /**
   * <HTML>Sets the current email list to the String specified. This parses every email address by
   * splitting the String by ; (semicolons).<br>
   * Example String: 1234567890[AT]mms.att.net;2345678901[AT]vtext.net;3456789012[AT]carr.ier.com><br>
   * Note that [AT] should be one character. Javadocs are fun.<br>
   * Also note that this sets {@link #textEmail} to null.
   * </HTML>
   * @param parseList
   */
  public static void setCellList(String parseList) {
    try {
      String[] parsed = parseList.split(";");
      for (int a = 0; a < parsed.length; a++) {
        emailList.add(parsed[a]);
      }
    } catch (Exception e) {
      emailList = null;
      ErrorManagement.showErrorWindow("ERROR parsing email addresses", "There was a problem reading the email address list specified. Please restart the program and enter a correct list.\nList provided: " + parseList, e);
    }
    textEmail = null;
  }

  /**
   * The email address to send a message to.
   *
   * @return The email address to send a message to, or null if
   *         {@link #setCellNum(java.lang.String, java.lang.String)} has not been called
   */
  public static String getTextEmail() {
    return textEmail;
  }

  /**
   * Gets the current List of all email addresses that will be emailed when a message is
   * sent.
   *
   * @return The List<string> of all email addresses being emailed, or null if
   *         {@link #setCellList(java.lang.String)} has not been called
   */
  public static List<String> getEmailList() {
    return emailList;
  }

  /**
   * Sets the {@link #props} settings for the the current email address being used. Call every time
   * the email provider (Yahoo, GMail) changes.
   */
  public static void emailSettings() {
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", port);
  }

  /**
   * Gets the current instance of the JavaMail session for {@link #props}. This should be called
   * every time you send an email.
   */
  public static void createSession() {
    l_session = Session.getDefaultInstance(props);
  }

  /**
   * Sends a test email to every number put into the program and prints whether it was successful or
   * not to the Status window.
   */
  public static void testEmail() {
    if (sendMessage("Test", "The test is successful. The PAX Checker is now set up to text your phone when the website updates!")) {
      if (PAXChecker.status != null) {
        PAXChecker.status.setButtonStatusText("Text message successfully sent!");
      }
    } else {
      if (PAXChecker.status != null) {
        PAXChecker.status.setButtonStatusText("There was an error sending your text message.");
      }
    }
  }

  /**
   * Sends an email to the provided number(s) using the supplied login information. This should only
   * be called once {@link #setUsername(java.lang.String)}, {@link #setPassword(java.lang.String)},
   * and ({@link #setCellNum(java.lang.String, java.lang.String)} or
   * {@link #setCellList(java.lang.String)}) have been called.
   *
   * @param subject
   * @param msg
   * @return
   */
  public static boolean sendMessage(String subject, String msg) {
    if (username == null || (textEmail == null && emailList == null)) {
      return false;
    }
    createSession();
    try {
      MimeMessage message = new MimeMessage(l_session);
      message.setFrom(new InternetAddress(username));
      if (textEmail != null) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(textEmail));
      } else if (emailList != null) {
        ListIterator<String> lI = emailList.listIterator();
        while (lI.hasNext()) {
          message.addRecipient(Message.RecipientType.BCC, new InternetAddress(lI.next()));
        }
      }
      message.setSubject(subject);
      message.setText(msg);
      Transport transport = l_session.getTransport("smtp");
      transport.connect(host, username, password);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      System.out.println("Message Sent");
    } catch (MessagingException mex) {
      mex.printStackTrace();
      ErrorManagement.showErrorWindow("ERROR", "The message was unable to be sent.", mex);
      return false;
    } catch (Exception e) {
      ErrorManagement.showErrorWindow("ERROR", "An unknown error has occurred while attempting to send the message.", e);
      e.printStackTrace();
      return false;
    }//end catch block
    return true;
  }

  public static class multiThread extends Thread {

    private String s, m;

    public multiThread(String subject, String msg) {
      s = subject;
      m = msg;
    }

    @Override
    public void run() {
      sendMessage(s, m);
    }
  }
}
