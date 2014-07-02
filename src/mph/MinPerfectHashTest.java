package mph;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class MinPerfectHashTest {
   String[] names = {"1", "2", "3", "4", "5", "6", "7"};
   MinPerfectHash mph;

   @Before
   public void setup() throws Exception {
      mph = new MinPerfectHash(names);

   }

   @Test
   public void mapPokosTest() throws Exception {
      mph.genMPH(false, false);
      assertEquals(names.length, mph.pokos.length);
      assertEquals(5, mph.buckets.get(0).size());
      assertEquals(2, mph.buckets.get(1).size());
   }

   @Test
   public void initTest() throws Exception {
      mph.genMPH(false, false);
      assertEquals(7, mph.m);
      assertEquals(4, mph.keysPerBucket);
   }

   @Test
   public void searchAndPlaceTest() throws Exception {
      mph.genMPH(false, false);
      mph.searchAndPlace(false, false);
   }

   @Test
   public void secureVsNormalRandom() throws Exception {
      mph.genMPH(false, false);
      SecureRandom sr = new SecureRandom();
      Random r = new Random();
      long init = System.nanoTime();
      sr.nextInt();
      System.out.println(System.nanoTime() - init);
      init = System.nanoTime();
      r.nextInt();
      System.out.println(System.nanoTime() - init);
   }

   @Test
   public void permuteBucketOrderTest() throws Exception {
      mph.genMPH(false, true);
      for (int i = 0; i < mph.mappings.length; i++) {
         System.out.println("bucket " + i + " mapped to: " + mph.mappings[i] + " ,size: "
               + mph.buckets.get(mph.mappings[i]).size() + " ,with rho i: " + mph.dispTable[i]);
      }
   }
}
