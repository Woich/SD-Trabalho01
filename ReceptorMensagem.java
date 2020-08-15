import java.io.*;
import java.net.*;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;


public class ReceptorMensagem extends Thread {
	
	MulticastSocket socket;
	InetAddress group;
	Assinatura assinatura;
	PublicKey chavePublica;
	byte[] assinat;
	List<MulticastPeerNode> listaNos;
	
	public ReceptorMensagem(InetAddress group, int porta) throws IOException{
		this.socket = new MulticastSocket(porta);
		this.assinatura = new Assinatura();
		this.group = group;
		this.socket.joinGroup(this.group);
		this.listaNos = new ArrayList<MulticastPeerNode>();
		this.start();
	}
	
	private boolean validaAssiantura(PublicKey pubKey, String mensagem, byte[] assinatura)  throws
	   NoSuchAlgorithmException, InvalidKeyException, SignatureException {
	       Signature clientSig = Signature.getInstance("DSA");
	       clientSig.initVerify(pubKey);
	       clientSig.update(mensagem.getBytes());

	       if (clientSig.verify(assinatura)) {
	           //Mensagem corretamente assinada
	          System.out.println("A Mensagem recebida foi assinada corretamente.");
	          return true;
	       } else {
	           //Mensagem não pode ser validada
	          System.out.println("A Mensagem recebida NÃO pode ser validada.");
	          return false;
	       }
	   }
	
	public void run() {
		try {
			
			byte[] buffer = new byte[1000];
			
			for(int i=0; i< 3;i++) {		// get messages from others in group
				String mensagem = "";
				
				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				this.socket.receive(messageIn);
 				mensagem = new String(messageIn.getData());
 				System.out.println("Message Received:" + mensagem);
 				
 				String[] mensagemParts = mensagem.split("|");
 				TipoMensagem tipoMensagem = TipoMensagem.findByCodigo(Integer.parseInt(mensagemParts[0]));
 				String ident = mensagemParts[1];
 				int portaUnicast = 9999;
 				assinat = mensagemParts[2].getBytes();
 				String assinaturaString = assinat.toString();
 				
 				if(tipoMensagem == TipoMensagem.HANDSHAKE) {
 					System.out.println("Bora");
 					PublicKey pubKey = assinatura.stringToPublicKey(assinaturaString);
 					System.out.println("pubKey");
 					listaNos.add(new MulticastPeerNode(ident, portaUnicast, pubKey));
 					System.out.println("Add na lista");
 				}
 			}
			
		}catch (NoSuchAlgorithmException e) {System.out.println("NoSuchAlgorithmException: " + e.getMessage());
		}catch (IOException e) {System.out.println("IOException: " + e.getMessage());
		}catch (InvalidKeySpecException e){System.out.println("InvalidKeySpecException: " + e.getMessage());
		}finally {System.out.println("Validação Encerrada!");}
	}
}