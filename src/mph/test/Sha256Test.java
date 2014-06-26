package mph.test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

import mph.Sha256;

import org.junit.Test;

public class Sha256Test {
   @Test
   public void test() throws NoSuchAlgorithmException {
      String a = Sha256.hash256_16bytes("michaelyuan", 8);
      String b = Sha256.hash256_16bytes("michaelyuan", 16);
      String c = Sha256.hash256_16bytes("michaelyuan", 32);
      assertEquals(16, a.length());
      assertEquals(32, b.length());
      assertEquals(64, c.length());
   }
}
