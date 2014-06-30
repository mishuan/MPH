package mph;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;

public class MinPerfectHashTest {
   String[] names = {"1", "2", "3", "4", "5", "6", "7"};
   MinPerfectHash mph;

   @Before
   public void setup() throws NoSuchAlgorithmException {
      mph = new MinPerfectHash(names);
      mph.genMPH(false);
   }

   @Test
   public void mapPokosTest() throws NoSuchAlgorithmException {
      assertEquals(names.length, mph.pokos.length);
      assertEquals(5, mph.buckets.get(0).size());
      assertEquals(2, mph.buckets.get(1).size());
   }

   @Test
   public void initTest() throws NoSuchAlgorithmException {
      assertEquals(7, mph.m);
      assertEquals(4, mph.keysPerBucket);
   }

   @Test
   public void searchAndPlaceTest() {
      mph.searchAndPlace(false);
   }
}
