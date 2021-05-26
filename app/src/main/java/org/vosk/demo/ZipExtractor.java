package org.vosk.demo;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

  private static String Base_Path = "/storage/emulated/0/Download/";
  private static String File_name_big = "vosk-model-en-us-aspire-0.2";
  private static String File_name_small= "vosk-model-small-en-us-0.15";
    private static String File_name_middle = "vosk-model-en-us-daanzu-20200905-lgraph";

    private static Boolean usingBigModel = true;
  private  static String File_name = File_name_middle;


  public static String full_path = Base_Path + File_name;


    public static void log(String text) {
        Log.d("VOSK LOG", text);
    }
    public static void test(){
      File file22 = new File(full_path);

    //  String ddd = calculateMD5(file22);
      //log("md5 ddd " + ddd);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        walk(new File(path));

        unpackZip(Base_Path, File_name+".zip");

    }

  public static String calculateMD5(File updateFile) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      log( "Exception while getting digest" + e);
      return null;
    }

    InputStream is;
    try {
      is = new FileInputStream(updateFile);
    } catch (FileNotFoundException e) {
      log("Exception while getting FileInputStream" + e);
      return null;
    }

    byte[] buffer = new byte[8192];
    int read;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      String output = bigInt.toString(16);
      // Fill to 32 chars
      output = String.format("%32s", output).replace(' ', '0');
      return output;
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for MD5", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        log( "Exception on closing MD5 input stream" + e);
      }
    }
  }

    public static void walk(File root) {

        File[] list = root.listFiles();
        if(list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                log("" + "Dir: " + f.getAbsoluteFile());
                walk(f);
            }
            else {
                log("" + "File: " + f.getAbsoluteFile());
            }
        }
    }

    public static boolean unpackZip(String path, String zipname) {

      log("unpackZip");



        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Download/" + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;


    }
}
