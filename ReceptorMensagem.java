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
import java.util.Scanner;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.lang.ClassNotFoundException;


public class ReceptorMensagem extends Thread {
	
	private ControleNos controleNos;
	private EmissorMensagem emissor;
	private MulticastSocket socket;
	private InetAddress group;
	private Assinatura assinatura;
	private PublicKey chavePublica;
	private String assinat;
	private String selfIdent;
	private List<Noticia> listaNoticias = new ArrayList<Noticia>();
	private boolean isOuvindo;
	private DatagramSocket datagramSocket;
	
	public ReceptorMensagem(ControleNos controleNos, EmissorMensagem emissor, String selfIdent,InetAddress group, int porta) throws IOException{
		//Declara as informações do receptor com base as informações do nó a qual ele pertence
		this.controleNos = controleNos;
		this.emissor = emissor;
		this.assinatura = new Assinatura();
		this.group = group;
		
		this.socket = new MulticastSocket(porta);
		this.socket.joinGroup(this.group);
		
		this.isOuvindo = true;
		//Identifcador do nó do qual faz parte
		this.selfIdent = selfIdent;
		
		//Deixa o receptor rodando sempre de forma paralela escutando, rodando a função run()
		this.start();
	}
	
	private boolean validaAssiantura(PublicKey pubKey, String mensagem, byte[] assinatura) {
	    
		boolean assinaturaValida = false;
		
		try {
			String[] mensagemParts = mensagem.split(";");
			String messageToValidate = mensagemParts[3];
			Signature clientSig = Signature.getInstance("DSA");
		    clientSig.initVerify(pubKey);
		    clientSig.update(messageToValidate.getBytes());
		    
		    if (clientSig.verify(assinatura)) {
		    	//Mensagem corretamente assinada
		        //System.out.println("A Mensagem recebida foi assinada corretamente.");
		        assinaturaValida = true;
		    } else {
		    	//Mensagem não pode ser validada
		        //System.out.println("A Mensagem recebida não pode ser validada.");
		        assinaturaValida = false;
		    }
		}catch (NoSuchAlgorithmException e) {System.out.println("validaAssiantura - NoSuchAlgorithmException: " + e.getMessage());
		}catch (InvalidKeyException e){System.out.println("validaAssiantura - InvalidKeyException: " + e.getMessage());
		}catch (SignatureException e){System.out.println("validaAssiantura - SignatureException: " + e.getMessage() + "\nCausa - " + e.getCause());
		}finally {/*System.out.println("validaAssiantura - Validação Encerrada!");*/ return assinaturaValida;}
	}

    public void listarNoticias() {
    	
    	if(!listaNoticias.isEmpty()) {
    		
    		for(Noticia noticia : listaNoticias) {
    			
    			System.out.println("Notícia: " + noticia.getNoticia());
    			System.out.println("Origem: " + noticia.getIdentOrigem());
    			System.out.println("");
    			System.out.println("O que deseja fazer?");
    			System.out.println("0 - Próxima Notícia");
    			System.out.println("1 - Denunciar");
    			
    			Scanner leitor = new Scanner(System.in);
    			int acao = leitor.nextInt();
    			
    			if(acao == 1) {
    				controleNos.disparaDenuncia(noticia.getIdentOrigem());
    			}
    		}
    		
    		System.out.println("Você chegou no final da lista");
    		
    	}
    	
    }
    
	public void run() {
		try {
			
			byte[] buffer = new byte[1000];
			
			while(isOuvindo) {		// get messages from others in group
				
				//Recebe a mensagem e transforma ela em string
				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
				
				this.socket.receive(messageIn);
	 			String mensagem = new String(messageIn.getData());
 				
				 //System.out.println("Message Received:" + mensagem);
				 if (!mensagem.contains(";")){
					System.out.println("Estamos aqui " + mensagem);
				 }
				 
				 //Separa a mensagem em várias partes com base no separador padrão
 				String[] mensagemParts = mensagem.split(";");
 				
 				//Com a primeira parte obtem qual o tipo da mensagem enviada
 				TipoMensagem tipoMensagem = TipoMensagem.findByCodigo(Integer.parseInt(mensagemParts[0]));
 				//ID do nó
 				String ident = mensagemParts[1];
 				
 				//Caso não seja uma mensagem do próprio nó
 				if(!ident.equals(selfIdent)) {
 					
 					if(tipoMensagem == TipoMensagem.HANDSHAKE) {
 	 					//Caso a mensagem seja um handshake (entrando no grupo) separa nos seguintes partes
 	 					
 	 	 				Boolean ehNovo = new Boolean(mensagemParts[2]);//Se é alguém entrando novo no grupo multicast ou alguém que já estava no grupo
 	 	 				String pubKeyString = mensagemParts[3];//Chave pública como string
 	 					
 	 					PublicKey pubKey = assinatura.stringToPublicKey(pubKeyString); //Chave pública como PublicKey
 	 					controleNos.addMulticastPeerNode(ident, pubKey);//Add o novo nó na lista de nós do emissor (usado para as denuncias)
 	 					
 	 					if(ehNovo.booleanValue()) {
 	 						InetAddress address = messageIn.getAddress();
 	 						int portUni  = messageIn.getPort();
 	 						
 	 						String messageBack = emissor.builMensagemHandshake(false);
 	 						System.out.println("ReceptorMensagem - Mensagem:" + messageBack);
 	 						
 	 						DatagramPacket messageOut = new DatagramPacket(messageBack.getBytes(), messageBack.length(), address, portUni);
 	 						socket.send(messageOut);
 	 						
 	 					}
 	 					
 	 				}else if(tipoMensagem == TipoMensagem.NOTICIA) {
 	 					//Caso a mensagem seja uma noticia nova que chegou

 	 					String noticiaString = mensagemParts[3];// A informação da Notícia
 	 					String assinat = mensagemParts[2];// A assinatura da Notícia
 	 					
 	 					//Transforma a assinatura em bytes
 	 					byte[] assinaturaNoticia = Base64.getMimeDecoder().decode(assinat) ;
 	 					
 	 					//Instancia a Notícia
 	 					Noticia noticia = new Noticia(ident, noticiaString, assinaturaNoticia);
 	 					//Obtem a chave do suposto nó ao qual ela faz parte
 	 					PublicKey pubKey = controleNos.obterPubKeyById(ident);
 	 					
 	 					//Caso tenha encontrado o nó
 	 					if(pubKey != null) {
							 //Caso a assinatura esteja correta adiciona a Notícia na lista de Notícias;
 	 						if(validaAssiantura(pubKey, mensagem, assinaturaNoticia)) {
 	 							listaNoticias.add(noticia);
 	 						}
 	 					}
 	 				}
 					
 				}
 			}
			
		}catch (NoSuchAlgorithmException e) {System.out.println("ReceptorMensagem - NoSuchAlgorithmException: " + e.getMessage());
		}catch (IOException e) {System.out.println("ReceptorMensagem - IOException: " + e.getMessage());
		}catch (InvalidKeySpecException e){System.out.println("ReceptorMensagem - InvalidKeySpecException: " + e.getMessage());
		}finally {System.out.println("ReceptorMensagem - Receptor Fechado!");}
	}
	
	public void paraEscuta() {
		this.isOuvindo = false;
	}
	
}