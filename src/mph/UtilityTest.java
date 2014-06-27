package mph;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class UtilityTest {
   @Test
   public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {
      String a = Utility.hash256_16bytes("michaelyuan", 8);
      String b = Utility.hash256_16bytes("michaelyuan", 16);
      String c = Utility.hash256_16bytes("michaelyuan", 32);
      assertEquals(16, a.length());
      assertEquals(32, b.length());
      assertEquals(64, c.length());
      System.out.println(b);
      System.out.println(b.subSequence(0, 8));
      System.out.println(b.subSequence(8, 16));
      System.out.println(b.subSequence(16, 24));
      System.out.println(b.subSequence(24, 32));
      System.out.println(Integer.parseInt(b.subSequence(0, 8).toString(), 16));

   }
}
