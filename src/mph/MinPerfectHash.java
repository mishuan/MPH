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
 * Class to create perfect hash for given input (title is slightly misleading, not minimal) This is
 * an implementatino of the CHD algorithm. Please refer to
 * http://cmph.sourceforge.net/papers/esa09.pdf for the original research paper
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

   /**
    * Get Raw Data
    * 
    * Get the data array with mappings constructed by the MPH
    * 
    * @return
    */
   public String[] getRawData() {
      return rawData;
   }

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
      m = rawData.length;
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
      m = rawData.length;
   }

   /*
    * Initialize Variables
    */
   protected void init() {

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
         String hexString = Utility.hash256(rawData[i].split(",")[0], 9);
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

      // random permutation specific code
      if (randomPermutation) {
         permuteBucketOrder();
      }
      // end

      SecureRandom sr = new SecureRandom();
      long nSquared = (long) n * (long) n;
      for (int index = 0; index < bucketCount; index++) {
         LinkedList<Integer> list = buckets.get(index);
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
               // if a position is filled, backtrack and try out new combination of probe0, probe1
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
            throw new Exception("Could not complete mapping.  Tried all displacements. Maybe try spliting the data.");
         int k = 0;
         for (int i : list) {
            rawData[i] = i + "," + rawData[i].split(",")[0] + "," + position[k];
            k++;
         }
         dispTable[mappings[index]] = (long) probe[0] + (long) probe[1] * (long) n;
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
    * @param randomDisp if you need to start at random positions of rho i, then toggle randomDisp.
    *        Normally rho i would start at 0
    * @param randomPermutation if you need random permutation of bucket order placement, toggle this
    * 
    * @throws Exception
    */
   public void genMPH(boolean randomDisp, boolean randomPermutation) throws Exception {
      init();
      pokos = mapPokosAndBuckets();
      searchAndPlace(randomDisp, randomPermutation);
   }

   /**
    * Map Entries
    * 
    * Map the input using a mapping scheme generated by this class
    * 
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
   public void mapEntriesUsingScheme(String dispTableFile) throws Exception {
      String[] dispTable = Utility.fileToStringArray(dispTableFile);
      bucketCount = dispTable.length - 1;
      n = Integer.valueOf(dispTable[bucketCount]);
      for (int i = 0; i < rawData.length; i++) {
         String hexString = Utility.hash256(rawData[i].split(",")[1], 9);
         int bucketNum = Integer.valueOf(hexString.subSequence(0, 6).toString(), 16) % bucketCount;
         int f = Integer.valueOf(hexString.subSequence(6, 12).toString(), 16) % n;
         int h = Integer.valueOf(hexString.subSequence(12, 18).toString(), 16) % (n - 1) + 1;
         long probe0 = Long.parseLong(dispTable[bucketNum], 10) % n;
         long probe1 = Long.parseLong(dispTable[bucketNum], 10) / n;
         int position = (f + (h * (int) probe0) + (int) probe1) % n;
         if (position < 0)
            position += n;
         rawData[i] = rawData[i] + "," + position;
      }
   }

   /**
    * Write Display Table
    * 
    * Output the theta i for every bin that was used to achieve perfect mapping
    * 
    * @param name of file where the out resides
    */
   public void writeDispTable(String filename) {
      try {
         PrintWriter writer = new PrintWriter(filename, "UTF-8");
         for (long i : dispTable) {
            writer.println(i);
         }
         writer.println(n);
         writer.close();
      } catch (IOException e) {
         e.getStackTrace();
      }
   }

   /**
    * Write Data Mapping
    * 
    * Output the data file with the key's position in the perfect hash table. The output format is
    * i,string,p i being the index of the string, p being the position of the string in the perfect
    * hash table
    * 
    * @param name of file where the out resides
    */
   public void writeDataMapping(String filename) {
      try {
         PrintWriter writer = new PrintWriter(filename, "UTF-8");
         for (String s : rawData) {
            writer.println(s);
         }
         writer.close();
      } catch (IOException e) {
         e.getStackTrace();
      }
   }

   /**
    * Plain Old Key Object (Get it?)
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
