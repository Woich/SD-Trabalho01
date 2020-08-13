
public class ReceptorMensagem extends Thread {
	
	MulticastSocket socket;
	PublicKey chavePublica;
	byte[] assinatura;
	
	public ReceptorMensagem(MulticastSocket socket, PublicKey chavePublica, byte[] assinatura) {
		this.socket = socket;
		this.chavePublica = chavePublica;
		this.assinatura = assinatura;
		this.start();
	}
	
	public void run() {
		try {
			
			byte[] buffer = new byte[1000];
 			
			String mensagem;
			
			for(int i=0; i< 3;i++) {		// get messages from others in group
 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				socket.receive(messageIn);
 				mensagem = new String(messageIn.getData());
 				System.out.println("Received:" + mensagem);	 
 			}
			
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
			
		}catch (NoSuchAlgorithmException e) {System.out.println("NoSuchAlgorithmException: " + e.getMessage());
		}catch (InvalidKeyException e) {System.out.println("InvalidKeyException: " + e.getMessage());
		}catch (SignatureException e) {System.out.println("SignatureException: " + e.getMessage());
		}finally {System.out.println("Validação Encerrada!");}
	}
}