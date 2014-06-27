package mph;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

import mph.MinPerfectHash.Poko;

import org.junit.Before;
import org.junit.Test;

public class MinPerfectHashTest {
   String[] names = {"1", "2", "3", "4", "5", "6", "7"};
   MinPerfectHash mph;

   @Before
   public void setup() {
      mph = new MinPerfectHash(names);
   }

   @Test
   public void createPokosTest() throws NoSuchAlgorithmException {
      Poko[] pk = mph.createPokos();
      assertEquals(names.length, pk.length);
   }

   @Test
   public void initTest() throws NoSuchAlgorithmException {
      assertEquals(mph.m, 7);
      assertEquals(mph.keysPerBin, 4);
      System.out.println(mph.n);
   }
}
