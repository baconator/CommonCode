package paxchecker;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author SunnyBat
 */
public class Browser {

  private static boolean checkPAXWebsite;
  private static boolean checkShowclix;
  private static int lastShowclixEventID = 3852445;
  private static final String updateLink = "https://dl.dropboxusercontent.com/u/16152108/PAXChecker.jar";
  private static final String patchNotesLink = "https://dl.dropboxusercontent.com/u/16152108/PAXCheckerUpdates.txt";
  private static String Expo;
  private static String websiteLink;
  private static URL updateURL;
  private static URL patchNotesURL;
  private static long updateSize;
  private static volatile String versionNotes;

  public static void init() {
    try {
      updateURL = new URL(updateLink);
      patchNotesURL = new URL(patchNotesLink);
    } catch (Exception e) {
      System.out.println("Unable to make a new URL?");
    }
  }

  public static boolean isPAXWebsiteUpdated() {
    if (!checkPAXWebsite) {
      return false;
    }
    String lineText = getCurrentButtonLinkLine();
    if (lineText == null) {
      if (PAXChecker.status != null) {
        PAXChecker.status.setWebsiteLink("ERROR connecting to the PAX website!");
      }
      return false;
    } else if (lineText.equals("IOException") || lineText.equals("NoConnection")) {
      if (PAXChecker.status != null) {
        PAXChecker.status.setWebsiteLink("Unable to connect to the PAX website!");
      }
      return false;
    } else if (lineText.equals("NoFind")) {
      if (PAXChecker.status != null) {
        PAXChecker.status.setWebsiteLink("Unable to find the Register Online button!");
      }
      return false;
    } else if (!lineText.contains("\"" + websiteLink + "\"")) {
      System.out.println("OMG IT'S UPDATED: " + lineText);
      return true;
    } else {
      if (PAXChecker.status != null) {
        PAXChecker.status.setWebsiteLink(parseHRef(lineText));
      }
      return false;
    }
  }

  public static String getCurrentButtonLinkLine() {
    URL url;
    InputStream is = null;
    BufferedReader br;
    String line;
    try {
      url = new URL(websiteLink + "/registration");
      is = url.openStream();
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.contains("class=\"btn red\"") && line.contains("title=\"Register Online\"")) {
          return line;
        }
      }
    } catch (UnknownHostException | MalformedURLException uhe) {
      return "NoConnection";
    } catch (IOException ioe) {
      return "IOException";
    } catch (Exception e) {
      ErrorManagement.showErrorWindow("ERROR", "An unknown error has occurred while attempting to read the PAX website.", e);
      System.out.println("ERROR");
      return null;
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ioe) {
        // nothing to see here
        System.out.println("Note: Unable to close InputStream for getCurrentButtonLinkLine()");
        ioe.printStackTrace();
      }
    }
    System.out.println("NULL");
    return "NoFind";
  }

  public static String parseHRef(String parse) {
    try {
      parse = parse.trim(); // Remove white space
      parse = parse.substring(parse.indexOf("href=") + 6); // Get index of link
      parse = parse.substring(0, parse.indexOf("\"")); // Remove everything after the link (hopefully this works for the Showclix link)
      if (parse.startsWith("\"") && parse.endsWith("\"")) {
        parse = parse.substring(1, parse.length() - 1);
      } else if (parse == null || parse.length() < 10) {
        System.out.println("Unable to correctly parse link from button HTML.");
        return websiteLink;
      }
      //System.out.println("Link parsed from Register Online button: " + parse);
      return parse.trim(); // PAX Aus currently has a space at the end of the registration button link... It doesn't sit well with Browser.java
    } catch (Exception e) {
      System.out.println("ERROR: Unable to parse link from button");
      e.printStackTrace();
      return websiteLink;
    }
  }

  public static boolean isShowclixUpdated() {
    if (!checkShowclix) {
      return false;
    }
    int currEvent = getLatestShowclixID();
    if (currEvent == -1) {
      if (PAXChecker.status != null) {
        PAXChecker.status.setShowclixLink("Unable to to connect to the Showclix website!");
      }
      return false;
    }
    String eventUrl = "https://showclix.com/event/" + currEvent;
    if (PAXChecker.status != null) {
      PAXChecker.status.setShowclixLink(eventUrl);
    }
    if (currEvent != lastShowclixEventID) {
      return true;
    }
    return false;
  }

  public static int getLatestShowclixID() {
    try {
      URL url = new URL("http://api.showclix.com/Seller/16886/events");
      HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
      httpCon.addRequestProperty("User-Agent", "Mozilla/4.0");
      BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
      String jsonText = "";
      String line = null;
      while ((line = reader.readLine()) != null) {
        jsonText += line;
      }
      reader.close();
      JSONParser mP = new JSONParser();
      JSONObject obj = (JSONObject) mP.parse(jsonText);

      int maxId = 0;
      for (String s : (Iterable<String>) obj.keySet()) {
        maxId = Math.max(maxId, Integer.parseInt((String) s));
      }
      return maxId;
    } catch (Exception e) {
//      ErrorManagement.showErrorWindow("ERORR checking the Showclix website for updates!", e);
      e.printStackTrace();
      return -1;
    }
  }

  public static String getShowclixLink() {
    try {
      return "http://showclix.com/event/" + getLatestShowclixID();
    } catch (Exception e) {
      ErrorManagement.showErrorWindow("ERORR checking the Showclix website for updates!", e);
      return null;
    }
  }

  public static void openLinkInBrowser(String link) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      if (link.startsWith("/") || link.startsWith("\\")) {
        link = websiteLink + link;
      }
      try {
        desktop.browse(new URI(link));
      } catch (Exception e) {
        ErrorManagement.showErrorWindow("ERROR opening browser window", "Unable to open link in browser window!", e);
      }
    } else {
      System.out.println("Unable to open link in default browser.");
      ErrorManagement.showErrorWindow("ERROR", "Unable to open link in default browser.", null);
    }
  }

  public static void setExpo(String e) {
    Expo = e;
  }

  public static String getExpo() {
    return Expo;
  }

  public static void setShowclixID(int ID) {
    if (ID == -1) {
      System.out.println("Unable to set most recent Showclix Event ID -- invalid ShowclixID!");
      return;
    }
    lastShowclixEventID = ID;
  }

  /**
   * Sets the website link used for the program. Note that this does NOT check for invalid links,
   * and therefore should be used with caution. Invalid links will result in failure to check the
   * proper PAX website. Ex: http://prime.paxsite.com
   *
   * @param link The FULL address (http:// as well!) to check for updates.
   */
  public static void setWebsiteLink(String link) {
    websiteLink = link;
  }

  /**
   * Returns the HTTP address of the given PAX Expo. Be sure to only use the name of the expo (ex:
   * prime) OR the full name (ex: pax prime) as the argument.
   *
   * @param expo The PAX expo to get the website link for
   * @return The website link of the specified expo, or the PAX Prime link if invalid.
   */
  public static String getWebsiteLink(String expo) {
    if (expo == null) {
      return "http://prime.paxsite.com";
    }
    switch (expo.toLowerCase()) { // toLowerCase to lower the possibilities (and readability)
      case "prime":
      case "pax prime":
        return "http://prime.paxsite.com";
      case "east":
      case "pax east":
        return "http://east.paxsite.com";
      case "aus":
      case "pax aus":
        return "http://aus.paxsite.com";
      case "dev":
      case "pax dev":
        return "http://dev.paxsite.com";
      default:
        return "http://prime.paxsite.com";
    }
  }

  public static void enablePaxWebsiteChecking() {
    checkPAXWebsite = true;
  }

  public static void enableShowclixWebsiteChecking() {
    checkShowclix = true;
  }

  public static boolean isCheckingPaxWebsite() {
    return checkPAXWebsite;
  }

  public static boolean isCheckingShowclix() {
    return checkShowclix;
  }

  public static String getVersionNotes(String version) { // TODO: Utilize getVersionNotes() instead of copying code and adding 3 lines
    String versNotes = getVersionNotes();
    if (versNotes == null) {
      return null;
    }
    try {
      versNotes = versNotes.substring(0, versNotes.indexOf("~~~" + version)).trim();
    } catch (Exception e) {
    }
    return versNotes;
  }

  public static String getVersionNotes() {
    return versionNotes;
  }

  public static void loadVersionNotes() {
    URLConnection inputConnection;
    InputStream textInputStream;
    BufferedReader myReader = null;
    try {
      inputConnection = patchNotesURL.openConnection();
      textInputStream = inputConnection.getInputStream();
      myReader = new BufferedReader(new InputStreamReader(textInputStream));
      String line;
      String lineSeparator = System.getProperty("line.separator", "\n");
      String allText = "Patch Notes:" + lineSeparator;
      while ((line = myReader.readLine()) != null) {
        line = line.trim();
        if (line.startsWith("TOKEN:")) {
          try {
            String d = line.substring(6);
            if (d.startsWith("SETSHOWCLIXID:")) {
              String load = d.substring(14);
              System.out.println("Load = " + load);
              setShowclixID(Integer.parseInt(load));
            } //else if (d.startsWith("")) {
//              String load = d.substring(0);
//              System.out.println("Load = " + load);
//              setShowclixID(Integer.parseInt(load));
//            }
          } catch (NumberFormatException numberFormatException) {
            System.out.println("Unable to set token: " + line);
          }
        } else {
          allText += line + lineSeparator;
        }
      }
      versionNotes = allText.trim();
    } catch (Exception e) {
      System.out.println("Unable to load version notes!");
    } finally {
      try {
        if (myReader != null) {
          myReader.close();
        }
      } catch (IOException e) {
        // nothing to see here
      }
    }
  }

  public static long getUpdateSize() {
    return updateSize;
  }

  /**
   * Checks whether or not an update to the program is available. Note that this compares the file
   * sizes between the current file and the file on the Dropbox server. This means that if ANY
   * modification is made to the JAR file, it's likely to trigger an update.
   * This THEORETICALLY works well. We'll find out whether or not it will actually work in
   * practice.
   *
   * @return True if an update is available, false if not.
   */
  public static boolean updateAvailable() {
    try {
      File mF = new File(PAXChecker.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
      long fileSize = mF.length();
      if (fileSize == 4096) { // No, I do NOT want to update when I'm running in Netbeans
        return false;
      }
      URLConnection conn = updateURL.openConnection();
      updateSize = conn.getContentLengthLong();
      System.out.println("Updatesize = " + updateSize + " -- Filesize = " + fileSize);
      if (updateSize == -1) {
        ErrorManagement.showErrorWindow("ERROR checking for updates!", "PAX Checker was unable to check for updates.", null);
        return false;
      } else if (updateSize != fileSize) {
        System.out.println("Update available!");
        return true;
      }
    } catch (Exception e) {
      System.out.println("ERROR updating program!");
      ErrorManagement.showErrorWindow("ERROR updating program!", "The program was unable to check for new updates.", e);
    }
    return false;
  }

  /**
   * Downloads the latest JAR file from the Dropbox server. Note that this automatically closes
   * the
   * program once finished. Also note that once this is run, the program WILL eventually close,
   * either through finishing the update or failing to properly update.
   */
  public static void updateProgram() {
    try {
      URLConnection conn = updateURL.openConnection();
      InputStream inputStream = conn.getInputStream();
      long remoteFileSize = conn.getContentLength();
      System.out.println("Downloding file...\nUpdate Size(compressed): " + remoteFileSize + " Bytes");
      String path = PAXChecker.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      BufferedOutputStream buffOutputStream = new BufferedOutputStream(new FileOutputStream(new File(path.substring(0, path.lastIndexOf(".jar")) + ".temp.jar")));
      byte[] buffer = new byte[32 * 1024];
      int bytesRead = 0;
      int in = 0;
      int prevPercent = 0;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        in += bytesRead;
        buffOutputStream.write(buffer, 0, bytesRead);
        if (PAXChecker.update != null) {
          if ((int) (((in * 100) / remoteFileSize)) != prevPercent) {
            prevPercent = (int) (((in * 100) / remoteFileSize));
            PAXChecker.update.updateProgress(prevPercent);
          }
        }
      }
      buffOutputStream.flush();
      buffOutputStream.close();
      inputStream.close();
      if (PAXChecker.update != null) {
        PAXChecker.update.setStatusLabelText("Finishing up...");
      }
      try { // Code to make a copy of the current JAR file
        File inputFile = new File(path.substring(0, path.lastIndexOf(".jar")) + ".temp.jar");
        InputStream fIn = new BufferedInputStream(new FileInputStream(inputFile));
        File outputFile = new File(path);
        buffOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        buffer = new byte[32 * 1024];
        bytesRead = 0;
        in = 0;
        while ((bytesRead = fIn.read(buffer)) != -1) {
          in += bytesRead;
          buffOutputStream.write(buffer, 0, bytesRead);
        }
        buffOutputStream.flush();
        buffOutputStream.close();
        fIn.close();
        inputFile.delete();
      } catch (Exception e) {
        ErrorManagement.showErrorWindow("ERROR updating", "Unable to complete update -- unable to copy temp JAR file to current JAR file.", e);
        ErrorManagement.fatalError();
      }
      System.out.println("Download Complete!");
      PAXChecker.startNewProgramInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR updating program!");
      ErrorManagement.showErrorWindow("ERROR updating the program", "The program was unable to successfully download the update. If the problem continues, please manually download the latest version at " + updateURL.getPath(), e);
      ErrorManagement.fatalError();
    }
  }
}
