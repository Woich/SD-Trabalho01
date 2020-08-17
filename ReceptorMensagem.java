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


public class ReceptorMensagem extends Thread {
	
	private EmissorMensagem emissor;
	private MulticastSocket socket;
	private InetAddress group;
	private Assinatura assinatura;
	private PublicKey chavePublica;
	private String assinat;
	private String selfIdent;
	private List<MulticastPeerNode> listaNos = new ArrayList<MulticastPeerNode>();
	private List<Noticia> listaNoticias = new ArrayList<Noticia>();
	private boolean isOuvindo;
	
	public ReceptorMensagem(EmissorMensagem emissor, String selfIdent,InetAddress group, int porta) throws IOException{
		//Declara as informa��es do receptor com base as informa��es do n� a qual ele pertence
		this.emissor = emissor;
		this.socket = new MulticastSocket(porta);
		this.assinatura = new Assinatura();
		this.group = group;
		this.socket.joinGroup(this.group);
		this.isOuvindo = true;
		//Identifcador do n� do qual faz parte
		this.selfIdent = selfIdent;
		
		//Deixa o receptor rodando sempre de forma paralela escutando, rodando a fun��o run()
		this.start();
	}
	
	private boolean validaAssiantura(PublicKey pubKey, String mensagem, byte[] assinatura) {
	    
		boolean assinaturaValida = false;
		
		try {
			Signature clientSig = Signature.getInstance("DSA");
		    clientSig.initVerify(pubKey);
		   System.out.println("Verificou a chave");
		    clientSig.update(mensagem.getBytes());
		    System.out.println("Pegou a mensagem");
		    
		    if (clientSig.verify(assinatura)) {
		    	//Mensagem corretamente assinada
		        System.out.println("A Mensagem recebida foi assinada corretamente.");
		        assinaturaValida = true;
		    } else {
		    	//Mensagem n�o pode ser validada
		        System.out.println("A Mensagem recebida N�O pode ser validada.");
		        assinaturaValida = false;
		    }
		}catch (NoSuchAlgorithmException e) {System.out.println("validaAssiantura - NoSuchAlgorithmException: " + e.getMessage());
		}catch (InvalidKeyException e){System.out.println("validaAssiantura - InvalidKeyException: " + e.getMessage());
		}catch (SignatureException e){System.out.println("validaAssiantura - SignatureException: " + e.getMessage() + "\nCausa - " + e.getCause());
		}finally {System.out.println("validaAssiantura - Valida��o Encerrada!"); return assinaturaValida;}
	}
	
    public void addMulticastPeerNode(String ident, int portaUnicast, PublicKey chavePublica) {
    	//Inicializa um novo n� com a chega de um handshake
    	MulticastPeerNode multicastPeerNode = new MulticastPeerNode(ident, portaUnicast, chavePublica);
    	//Adiciona ele na lista de n�s j� existentes
    	listaNos.add(multicastPeerNode);
    }
	
    public PublicKey obterPubKeyById(String ident) {
    	//Caso a identidade e a lista n�o sejam vazias
    	if(ident != null && !listaNos.isEmpty()) {
    		for(MulticastPeerNode node : listaNos) {
        		//Caso ache o n� retorna a chave p�blica do mesmo;
    			if(ident.equals(node.getIdent())) {
    				return node.getChavePublica();
        		}
        	}
    	}
    	
    	//Caso n�o ache o n� retorna uma mensagem de erro ao achar o n�;
    	System.out.println("N� n�o encontrado!");
    	return null;
    }
	
    public void listarNos() {
		if(!listaNos.isEmpty()) {
		    		
		    		for(MulticastPeerNode node : listaNos) {
		    			
		    			System.out.println("N�: " + node.getIdent());
		    			System.out.println("Denuncias: " + node.getDenuncias());
		    			System.out.println("");
		    			
		    		}
		    		
		    		System.out.println("===================================");
		    		
		    	}
    }
    
    public void listarNoticias() {
    	
    	if(!listaNoticias.isEmpty()) {
    		
    		for(Noticia noticia : listaNoticias) {
    			
    			System.out.println("Not�cia: " + noticia.getNoticia());
    			System.out.println("Origem: " + noticia.getIdentOrigem());
    			System.out.println("");
    			System.out.println("O que deseja fazer?");
    			System.out.println("0 - Pr�xima not�cia");
    			System.out.println("1 - Denunciar");
    			
    			Scanner leitor = new Scanner(System.in);
    			int acao = leitor.nextInt();
    			
    			if(acao == 1) {
    				disparaDenuncia(noticia.getIdentOrigem());
    			}
    		}
    		
    		System.out.println("Voc� chegou no final da lista");
    		
    	}
    	
    }
    
    public void disparaDenuncia(String ident) {
    	if(ident != null && !listaNos.isEmpty()) {
    		for(MulticastPeerNode node : listaNos) {
        		//Caso ache o n� retorna a chave p�blica do mesmo;
    			if(ident.equals(node.getIdent())) {
    				node.addDenuncia();
    				emissor.enviaDenuncia(ident);
        		}
        	}
    	}
    }
    
	public void run() {
		try {
			
			byte[] buffer = new byte[1000];
			
			while(isOuvindo) {		// get messages from others in group
				String mensagem = "";
				
				//Recebe a mensagem e transfora ele em string
				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				this.socket.receive(messageIn);
 				mensagem = new String(messageIn.getData());
 				
 				System.out.println("Message Received:" + mensagem);
 				
 				//Separa a mensagem em v�rias partes com base no separador padr�o
 				String[] mensagemParts = mensagem.split(";");
 				
 				//Com a primeira parte obtem qual o tipo da mensagem enviada
 				TipoMensagem tipoMensagem = TipoMensagem.findByCodigo(Integer.parseInt(mensagemParts[0]));
 				//ID do n�
 				String ident = mensagemParts[1];
 				
 				//Caso n�o seja uma mensagem do pr�prio n�
 				if(!ident.equals(selfIdent)) {
 					
 					if(tipoMensagem == TipoMensagem.HANDSHAKE) {
 	 					//Caso a mensagem seja um handshake (entrando no grupo) separa nos seguintes partes
 	 					
 	 	 				Integer portaUnicast = new Integer(mensagemParts[2]);//Porta unicast para enviar a p�prias informa��es
 	 	 				Boolean ehNovo = new Boolean(mensagemParts[3]);//Se � algu�m entrando novo no grupo multicast ou algu�m que j� estava no grupo
 	 	 				String pubKeyString = mensagemParts[4];//Chave p�blica como string
 	 					
 	 					PublicKey pubKey = assinatura.stringToPublicKey(pubKeyString); //Chave p�blica como PublicKey
 	 					addMulticastPeerNode(ident, portaUnicast, pubKey);//Add o novo n� na lista de n�s do emissor (usado para as denuncias)
 	 					
 	 				}else if(tipoMensagem == TipoMensagem.NOTICIA) {
 	 					//Caso a mensagem seja uma noticia nova que chegou

 	 					String noticiaString = mensagemParts[2];// A informa��o da not�cia
 	 					String assinat = mensagemParts[3];// A assinatura da not�cia
 	 					
 	 					//Transforma a assinatura em bytes
 	 					byte[] assinaturaNoticia = assinat.getBytes();
 	 					
 	 					//Instancia a not�cia
 	 					Noticia noticia = new Noticia(ident, noticiaString, assinaturaNoticia);
 	 					//Obtem a chave do suposto n� ao qual ela faz parte
 	 					PublicKey pubKey = obterPubKeyById(ident);
 	 					
 	 					listaNoticias.add(noticia);
 	 					
 	 					//Caso tenha encontrado o n�
 	 					/*if(pubKey != null) {
 	 						//Caso a assinatura esteja correta adiciona a not�cia na lista de not�cias;
 	 						if(validaAssiantura(pubKey, mensagem, assinaturaNoticia)) {
 	 							listaNoticias.add(noticia);
 	 						}
 	 					}*/
 	 				}
 					
 				}
 			}
			
		}catch (NoSuchAlgorithmException e) {System.out.println("ReceptorMensagem - NoSuchAlgorithmException: " + e.getMessage());
		}catch (IOException e) {System.out.println("ReceptorMensagem - IOException: " + e.getMessage());
		}catch (InvalidKeySpecException e){System.out.println("ReceptorMensagem - InvalidKeySpecException: " + e.getMessage());
		}finally {System.out.println("ReceptorMensagem - Receptor Fechado!");}
	}
}