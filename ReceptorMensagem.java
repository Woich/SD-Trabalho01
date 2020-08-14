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
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;


public class ReceptorMensagem extends Thread {
	
	MulticastSocket socket;
	PublicKey chavePublica;
	byte[] assinatura;
	
	public ReceptorMensagem(MulticastSocket socket, PublicKey chavePublica) {
		this.socket = socket;
		this.chavePublica = chavePublica;
		this.start();
	}
	
	public void run() {
		try {
			
			byte[] buffer = new byte[1000];
 			
			String mensagem = "";
			
			for(int i=0; i< 3;i++) {		// get messages from others in group
 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				socket.receive(messageIn);
 				mensagem = new String(messageIn.getData());
 				System.out.println("Received:" + mensagem);	 
 			}
			
			String[] mensagemParts = mensagem.split("|");
			TipoMensagem tipoMensagem = TipoMensagem.findByCodigo(Integer.parseInt(mensagemParts[0]));
			String ident = mensagemParts[1];
			byte[] assinatura = mensagemParts[2].getBytes();
			
			if(tipoMensagem == TipoMensagem.HANDSHAKE) {
				Signature clientSig = Signature.getInstance("DSA");
				clientSig.initVerify(chavePublica);
				clientSig.update(mensagem.getBytes());

				if (clientSig.verify(assinatura)) {
					//Mensagem corretamente assinada
					System.out.println("A Mensagem recebida foi assinada corretamente.");
				} else {
					//Mensagem não pode ser validada
					System.out.println("A Mensagem recebida NÃO pode ser validada.");
				}
			}
			
		}catch (NoSuchAlgorithmException e) {System.out.println("NoSuchAlgorithmException: " + e.getMessage());
		}catch (InvalidKeyException e) {System.out.println("InvalidKeyException: " + e.getMessage());
		}catch (SignatureException e) {System.out.println("SignatureException: " + e.getMessage());
		}catch (IOException e) {System.out.println("IOException: " + e.getMessage());
		}finally {System.out.println("Validação Encerrada!");}
	}
}