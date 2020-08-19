import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;


public class Assinatura {

     private PublicKey pubKey;
     private PrivateKey priKey;

     public PublicKey getPubKey() {

           return pubKey;
     }

     public PrivateKey getPriKey() {

            return priKey;
      }

     public PublicKey stringToPublicKey(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
            byte[] publicBytes = Base64.getMimeDecoder().decode(string);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            return pubKey;
     } 
     public String publicKeyToString(PublicKey chave) throws NoSuchAlgorithmException, InvalidKeySpecException {            
            return Base64.getEncoder().encodeToString(chave.getEncoded());
      } 

     public void setPubKey(PublicKey pubKey) {
           this.pubKey = pubKey;
     }
     public void setPriKey(PrivateKey priKey) {
            this.priKey = priKey;
      }
     public void createKeys() throws NoSuchAlgorithmException{
            //Gera��o das chaves p�blicas e privadas
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            SecureRandom secRan = new SecureRandom();
            kpg.initialize(512, secRan);
            KeyPair keyP = kpg.generateKeyPair();
            setPubKey(keyP.getPublic());
            setPriKey(keyP.getPrivate());
     }


     public byte[] geraAssinatura(String mensagem) throws NoSuchAlgorithmException,
     InvalidKeyException, SignatureException {
         Signature sig = Signature.getInstance("DSA");
         //Inicializando Obj Signature com a Chave Privada
         sig.initSign(this.priKey);

         //Gerar assinatura
         sig.update(mensagem.getBytes());
         byte[] assinatura = sig.sign();

         return assinatura;
     }

}