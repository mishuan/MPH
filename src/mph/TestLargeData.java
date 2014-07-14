package mph;


public class TestLargeData {

   public static void main(String[] args) throws Exception {
      long init = System.nanoTime();
      MinPerfectHash mph = new MinPerfectHash("cdata.txt");
      mph.genMPH(true, true);
//      for (long i : mph.dispTable) {
//         System.out.println(i);
//      }
      mph.writeMphMapping("testing123");
      System.out.println(System.nanoTime() - init);
   }
}
