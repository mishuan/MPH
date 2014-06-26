package mph;

import java.security.*;

public class Sha256 {
   public static String hash256_16bytes(String data, int numBytes) throws NoSuchAlgorithmException {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(data.getBytes());
      return bytesToHex(md.digest()).substring(0, numBytes * 2);
   }

   private static String bytesToHex(byte[] bytes) {
      StringBuffer result = new StringBuffer();
      for (byte byt : bytes)
         result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
      return result.toString();
   }
}
