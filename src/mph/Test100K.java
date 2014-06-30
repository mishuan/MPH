package mph;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Test100K {

   public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
      long init = System.nanoTime();
      MinPerfectHash mph = new MinPerfectHash("cdata.txt");
      mph.genMPH(false);
      System.out.println(System.nanoTime() - init);
   }
}
