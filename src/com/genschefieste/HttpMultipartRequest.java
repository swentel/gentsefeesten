package com.genschefieste;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;

public class HttpMultipartRequest {

  static String lineEnd = "\r\n";
  static String twoHyphens = "--";
  static String boundary = "AaB03x87yxdkjnxvi7";

  /**
   * @param ctxt
   *          The context.
   * @param method
   *          The method, either GET or POST.
   * @param getParameters
   *          A collection of get parameters key value pairs to send along.
   * @param getParameters
   *          A collection of post parameters key value pairs to send along.
 need to save or send a session cookie.
   * @param filePath
   *          The full file path on the device.
   * @param fileParameterName
   *          The key to send in the request.
   */
  public static String execute(Context ctxt, String method, HashMap<String, String> getParameters, HashMap<String, String> postParameters, String filePath, String fileParameterName) throws IOException {
    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    FileInputStream fileInputStream = null;
    File file = null;
    String sResponse = "";

    byte[] buffer;
    int maxBufferSize = 20 * 1024;
    try {

      // Open a HTTP connection to the URL
      String newRequestUrl = Event.eventUrl;
      for (String name : getParameters.keySet()) {
        newRequestUrl = newRequestUrl + "&" + name + "=" + getParameters.get(name);
      }
      URL url = new URL(newRequestUrl);
      conn = (HttpURLConnection) url.openConnection();

      // Allow Inputs
      conn.setDoInput(true);
      // Allow Outputs
      conn.setDoOutput(true);
      // Don't use a cached copy.
      conn.setUseCaches(false);
      // Use a post method.
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
      conn.connect();

      dos = new DataOutputStream(conn.getOutputStream());

      // See if we need to upload a file.
      if (filePath.length() > 0) {
        file = new File(filePath);
        fileInputStream = new FileInputStream(file);
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + fileParameterName + "\"; filename=\"" + file.toString() + "\"" + lineEnd);
        dos.writeBytes("Content-Type: text/xml" + lineEnd);
        dos.writeBytes(lineEnd);

        // create a buffer of maximum size
        buffer = new byte[Math.min((int) file.length(), maxBufferSize)];
        int length;
        // read file and write it into form...
        while ((length = fileInputStream.read(buffer)) != -1) {
          dos.write(buffer, 0, length);
        }
      }

      // Post data.
      if (postParameters.size() > 0) {
        for (String name : postParameters.keySet()) {
          dos.writeBytes(lineEnd);
          dos.writeBytes(twoHyphens + boundary + lineEnd);
          dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd);
          dos.writeBytes(lineEnd);
          dos.writeBytes(postParameters.get(name));
        }
      }

      // Send form data necessary after file data.
      dos.writeBytes(lineEnd);
      dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
      dos.flush();
    }
    catch (IOException e) {
      sResponse = "down";
    }
    finally {
      if (fileInputStream != null) {
        fileInputStream.close();
      }
      if (dos != null) {
        dos.close();
      }
    }

    // Read the server response.
    try {
      dis = new DataInputStream(conn.getInputStream());
      StringBuilder response = new StringBuilder();

      String line;
      while ((line = dis.readLine()) != null) {
        response.append(line);
      }

      sResponse = response.toString();
    }
    finally {
      if (dis != null) {
        dis.close();
      }
    }

    return sResponse;
  }
}
