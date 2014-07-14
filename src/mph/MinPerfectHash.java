package mph;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Minimal Perfect Hash
 * 
 * Class to create minimal hash for given input
 * 
 * @author Michael Yuan
 * 
 */
public class MinPerfectHash {
   final int keysPerBucket = 4;
   int bucketCount;
   int m;
   int n;

   Poko[] pokos;
   String[] rawData;
   ArrayList<LinkedList<Integer>> buckets;
   long[] dispTable;
   int[] mappings;

   /**
    * Constructor Using an Input File
    * 
    * @param file file being parsed into string array to be hashed
    * @throws IOException
    */
   public MinPerfectHash(String file) throws IOException {
      rawData = Utility.fileToStringArray(file);
   }

   /**
    * Constructor Using an Input Array
    * 
    * @param dataArray array being hashed
    */
   public MinPerfectHash(String[] dataArray) {
      if (dataArray == null || dataArray.length == 0)
         throw new IllegalArgumentException();
      rawData = dataArray;
   }

   /*
    * Initialize Variables
    */
   protected void init() {
      m = rawData.length;
      bucketCount = m / keysPerBucket + 1;
      n = (int) (m / 0.5d + 1);
      dispTable = new long[bucketCount];
      mappings = new int[bucketCount];
      for (int i = 0; i < bucketCount; i++) {
         mappings[i] = i;
      }
      // set n as closest prime
      if (n % 2 == 0)
         n++;
      while (true) {
         if (Utility.isPseudoPrime(n))
            break;
         n += 2;
      }

      // create empty buckets
      buckets = new ArrayList<LinkedList<Integer>>(bucketCount);
      for (int i = 0; i < bucketCount; i++)
         buckets.add(i, new LinkedList<Integer>());
   }

   /**
    * Create Pokos
    * 
    * Create an array of Pokos using the input data
    * 
    * @return
    * @throws NoSuchAlgorithmException
    */
   protected Poko[] mapPokosAndBuckets() throws NoSuchAlgorithmException {
      Poko[] pokos = new Poko[rawData.length];
      for (int i = 0; i < rawData.length; i++) {
         String hexString = Utility.hash256(rawData[i], 9);
         pokos[i] = new Poko();
         int bucketNum = Integer.valueOf(hexString.subSequence(0, 6).toString(), 16) % bucketCount;
         pokos[i].bucketNum = bucketNum;
         pokos[i].f = Integer.valueOf(hexString.subSequence(6, 12).toString(), 16) % n;
         pokos[i].h = Integer.valueOf(hexString.subSequence(12, 18).toString(), 16) % (n - 1) + 1;
         buckets.get(bucketNum).add(i);
      }
      return pokos;
   }

   /*
    * Search for a mapping that maps all the keys
    */
   protected void searchAndPlace(boolean randomDisp, boolean randomPermutation) throws Exception {
      boolean[] filled = new boolean[n];
      int index = 0;

      // random permutation specific code
      if (randomPermutation) {
         permuteBucketOrder();
      }
      // end

      SecureRandom sr = new SecureRandom();
      long nSquared = (long) n * (long) n;
      for (LinkedList<Integer> list : buckets) {
         int size = list.size();
         if (size == 0)
            continue;

         // random display table related code
         int[] probe = new int[2];
         if (randomDisp) {
            long r = (long) (sr.nextDouble() * nSquared);
            probe[0] = (int) (r % n);
            probe[1] = (int) (r / n);
         }
         // end

         int tries = 0;
         int maxTries = n * n;
         int[] position = new int[size];
         boolean found = false;

         while (tries < maxTries) {
            found = true;
            int k = 0;
            for (int i : list) {
               position[k] = (pokos[i].f + pokos[i].h * probe[0] + probe[1]) % n;
               if (position[k] < 0)
                  position[k] += n;
               if (filled[position[k]]) {
                  found = false;
                  for (int j = 0; j < k; j++)
                     filled[position[j]] = false;
                  break;
               }
               filled[position[k]] = true;
               k++;
            }
            if (found)
               break;
            probe[0]++;
            if (probe[0] >= n) {
               probe[0] -= n;
               probe[1]++;
               if (probe[1] >= n)
                  probe[1] -= n;
            }
            tries++;
         }

         if (tries >= maxTries)
            throw new Exception("Could not complete mapping.  Tried all displacements.");
         dispTable[mappings[index]] = (long) probe[0] + (long) probe[1] * (long) n;
         index++;
      }
   }

   /**
    * Permute the ordering of the buckets
    */
   protected void permuteBucketOrder() {
      Random rng = new Random();
      for (int i = 0; i < bucketCount; i++) {
         int j = rng.nextInt(bucketCount);
         int tempInt = mappings[i];
         LinkedList<Integer> tempList = buckets.get(i);
         buckets.set(i, buckets.get(j));
         buckets.set(j, tempList);
         mappings[i] = mappings[j];
         mappings[j] = tempInt;
      }
   }

   /**
    * Generate Minimal Perfect Hash
    * 
    * Using the given input, generate minimal perfect hash (or perfect hash, undecided yet)
    * 
    * @param random if you need to start at random positions of rho i, then toggle random. Normally
    *        rho i would start at 0
    * @throws NoSuchAlgorithmException
    */

   public void genMPH(boolean randomDisp, boolean randomPermutation) throws Exception {
      init();
      pokos = mapPokosAndBuckets();
      searchAndPlace(randomDisp, randomPermutation);
   }

   /**
    * Map Entries
    * 
    * Map the input using a mapping scheme generated by this classt
    */
   public void mapEntriesUsingScheme(String mapping) {

   }

   /**
    * Write Bin Mappings
    * 
    * Output the theta i for every bin that was used to achieve perfect mapping
    */
   public void writeMphMapping(String filename) {
      try {
         PrintWriter writer = new PrintWriter(filename, "UTF-8");
         for (long i : dispTable) {
            writer.println(i);
         }
         writer.close();
      } catch (IOException e) {
         e.getStackTrace();
      }
   }


   /**
    * Plain Old Key Object
    * 
    * @author Michael Yuan
    * 
    */
   protected class Poko {
      protected int bucketNum;
      protected int f;
      protected int h;
   }
}
