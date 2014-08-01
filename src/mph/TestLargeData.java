package mph;


public class TestLargeData {

   public static void main(String[] args) throws Exception {
      long init = System.nanoTime();
      MinPerfectHash mph = new MinPerfectHash("adata.txt");
      mph.genMPH(true, true);
      // for (long i : mph.dispTable) {
      // System.out.println(i);
      // }
      mph.writeDispTable("dispTable");
      mph.writeDataMapping("adataMapped");
      System.out.println(System.nanoTime() - init);

      init = System.nanoTime();
      mph = new MinPerfectHash("bdata.txt");
      mph.mapEntriesUsingScheme("dispTable");
      mph.writeDataMapping("bdatamapped");
      System.out.println(System.nanoTime() - init);
   }
}
