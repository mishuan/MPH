package mph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.security.*;

/**
 * Utility
 * 
 * Utility class containing utility functions that MPH uses
 * 
 * @author Michael Yuan
 * 
 */
public class Utility {
   /**
    * hash256
    * 
    * Returns the first numBytes bytes of SHA256 hash of given data
    * 
    * @param data input data
    * @param numBytes number of bytes required from the SHA-256 hash
    * @return
    * @throws NoSuchAlgorithmException
    */
   public static String hash256(String data, int numBytes) throws NoSuchAlgorithmException {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(data.getBytes());
      return bytesToHex(md.digest()).substring(0, numBytes * 2);
   }

   /**
    * Bytes to Hex
    * 
    * Converts byte array to hex string
    * 
    * @param bytes
    * @return
    */
   private static String bytesToHex(byte[] bytes) {
      StringBuffer result = new StringBuffer();
      for (byte byt : bytes)
         result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
      return result.toString();
   }

   /**
    * Hex String to Byte Array
    * 
    * Converts string to byte array
    * 
    * @param s
    * @return
    */
   public static byte[] hexStringToByteArray(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
         data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }
      return data;
   }

   /**
    * File to String Array
    * 
    * Input file path and line count of said file, converts the file into an array with each line
    * being an entry
    * 
    * @param file
    * @param lineCount
    * @return
    * @throws IOException
    */
   public static String[] fileToStringArray(String file) throws IOException {
      LineNumberReader lnr = new LineNumberReader(new FileReader(file));
      lnr.skip(Long.MAX_VALUE);
      int lines = lnr.getLineNumber();
      lnr.close();
      BufferedReader bc_br = new BufferedReader(new FileReader(file));
      String[] data = new String[lines];
      for (int i = 0; i < lines; i++)
         data[i] = bc_br.readLine();
      bc_br.close();
      return data;
   }

   /**
    * Is Pseudo Prime
    * 
    * Takes a number n and determines with very high probability whether it is a prime or not
    * 
    * This method utilizes the well known Miller-Rabin primality test. This specific implementation
    * is provided by Kristian of mathblog.dk.
    * 
    * @param n
    * @return
    */
   public static boolean isPseudoPrime(int n) {
      if (n <= 1)
         return false;
      if (n == 2)
         return true;
      if (n % 2 == 0)
         return false;
      if (n < 9)
         return true;
      if (n % 3 == 0)
         return false;
      if (n % 5 == 0)
         return false;

      int[] ar = new int[] {2, 3};
      for (int i = 0; i < ar.length; i++) {
         if (Witness(ar[i], n))
            return false;
      }
      return true;
   }

   private static boolean Witness(int a, int n) {
      int t = 0;
      int u = n - 1;
      while ((u & 1) == 0) {
         t++;
         u >>= 1;
      }

      long xi1 = ModularExp(a, u, n);
      long xi2;

      for (int i = 0; i < t; i++) {
         xi2 = xi1 * xi1 % n;
         if ((xi2 == 1) && (xi1 != 1) && (xi1 != (n - 1)))
            return true;
         xi1 = xi2;
      }
      if (xi1 != 1)
         return true;
      return false;
   }

   private static long ModularExp(int a, int b, int n) {
      long d = 1;
      int k = 0;
      while ((b >> k) > 0)
         k++;

      for (int i = k - 1; i >= 0; i--) {
         d = d * d % n;
         if (((b >> i) & 1) > 0)
            d = d * a % n;
      }

      return d;
   }
}
