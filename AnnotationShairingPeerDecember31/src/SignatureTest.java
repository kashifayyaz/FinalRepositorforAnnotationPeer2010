

import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Signature;

public class SignatureTest {
    //public PublicKey pubKey = null;
    SignatureTest() throws Exception {
    
   }
  public static byte[] sign(String[] data, PrivateKey prvKey,
      String sigAlg) throws Exception {
    Signature sig = Signature.getInstance(sigAlg);
    sig.initSign(prvKey);
    byte[] dataBytes = new byte[1024];
    dataBytes = data.toString().getBytes();
    int nread = dataBytes.length;
    while (nread > 0) {
      sig.update(dataBytes, 0, nread);
      nread --;
    };
    return sig.sign();
  }
   static boolean verify(String[] datafile, PublicKey pubKey, String sigAlg, byte[] sigbytes) throws Exception {
    Signature sig = Signature.getInstance(sigAlg);
    sig.initVerify(pubKey);
    byte[] dataBytes = new byte[1024];
    dataBytes = datafile.toString().getBytes();
    int nread = dataBytes.length;
    while (nread > 0) {
      sig.update(dataBytes, 0, nread);
      nread --;
    };
    return sig.verify(sigbytes);
  }
  
}
