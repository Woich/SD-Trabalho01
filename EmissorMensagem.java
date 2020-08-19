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
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class EmissorMensagem {
	
	private ControleNos controleNos;
	private String ident;
	private int portaMulticast;
	private MulticastSocket socket;
	private InetAddress group;
	private Assinatura assinatura;
	private PublicKey chavePublica;
	
	DatagramPacket messageOut;
	
	public EmissorMensagem(ControleNos controleNos, String ident, int portaMulticast,
			InetAddress group, Assinatura assinatura, PublicKey chavePublica)  throws IOException{
		//Declara as informações do emissor com base as informações do nó a qual ele pertence
		this.controleNos = controleNos;
		this.ident = ident;
		this.portaMulticast = portaMulticast;
		this.group = group;
		this.assinatura = assinatura;
		this.chavePublica = chavePublica;
		
		this.socket = new MulticastSocket();
		//this.socket.joinGroup(group);
	}
    
	public void enviaHandskake(boolean novoNode) {
		
		try {
			
			String mensagem = builMensagemHandshake(novoNode);
			
			DatagramPacket messageOut = new DatagramPacket(mensagem.getBytes(), mensagem.length(), group, 6789);
			socket.send(messageOut);
			
			byte[] buffer = new byte[1000];
			
			try {
				while(true) {
					//Fica escutando na porta unicast
					DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);;
					socket.setSoTimeout(1000);
					socket.receive(messageIn);
					parseMensagem(messageIn);
					System.out.println("enviaHandskake - Escutei:" + new String(messageIn.getData()));
				}
			}catch (Exception e) {
				System.out.println("enviaHandskake - Exception:" + e.getMessage());
			}
			
		}catch (Exception e){System.out.println("enviaHandskake - Exception: " + e.getMessage());
		}finally {if(socket != null) socket.close();}
		
	}
	
	public void enviaNoticia() {
		
		try {
			
			//Abre o socket para enviar a notícia
			this.socket = new MulticastSocket(portaMulticast);
			this.socket.joinGroup(group);
			
			String mensagem = "";
			
			//Set para tipo da mensagem como 2(notícia) e a identidade do nó a qual faz parte;
			mensagem = TipoMensagem.NOTICIA.getCodigo().toString()  + ";";
			mensagem += ident + ";";
			
			//Obtem qual é a notícia;
			System.out.println("Escreva a noticia: \n");
			Scanner leitor = new Scanner(System.in);
			
			String noticia = leitor.next();
			
			mensagem += Base64.getEncoder().encodeToString(assinatura.geraAssinatura(noticia)) + ";" ;

			mensagem += noticia + ";";
			
			//Assina a notícia;
			
			
			//Gera o datagrama e envia a notícia para o grupo multicast;
			DatagramPacket messageOut = new DatagramPacket(mensagem.getBytes(), mensagem.length(), group, 6789);
			socket.send(messageOut);
			
		}catch (IOException e){System.out.println("enviaNoticia - IOException: " + e.getMessage());
		}catch (NoSuchAlgorithmException e){System.out.println("enviaNoticia - NoSuchAlgorithmException: " + e.getMessage());
		}catch (InvalidKeyException e){System.out.println("enviaNoticia - InvalidKeyException: " + e.getMessage());
		}catch (SignatureException e){System.out.println("enviaNoticia - SignatureException: " + e.getMessage());
		}finally {if(socket != null) socket.close();}
		
	}
	
	public void enviaDenuncia(String identOrigem) {
		
		try {
					
					//Abre o socket para enviar a notícia
					this.socket = new MulticastSocket(portaMulticast);
					this.socket.joinGroup(group);
					
					String mensagem = "";
					
					//Set para tipo da mensagem como 2(notícia) e a identidade do nó a qual faz parte;
					mensagem = TipoMensagem.DENUNCIA.getCodigo().toString()  + ";";
					mensagem += ident + ";";
					
					//Assina a notícia;
					mensagem += assinatura.geraAssinatura(mensagem);
					
					//Gera o datagrama e envia a notícia para o grupo multicast;
					DatagramPacket messageOut = new DatagramPacket(mensagem.getBytes(), mensagem.length(), group, 6789);
					socket.send(messageOut);
					
				}catch (IOException e){System.out.println("enviaNoticia - IOException: " + e.getMessage());
				}catch (NoSuchAlgorithmException e){System.out.println("enviaNoticia - NoSuchAlgorithmException: " + e.getMessage());
				}catch (InvalidKeyException e){System.out.println("enviaNoticia - InvalidKeyException: " + e.getMessage());
				}catch (SignatureException e){System.out.println("enviaNoticia - SignatureException: " + e.getMessage());
				}finally {if(socket != null) socket.close();}
		
	}
	
	public String builMensagemHandshake(boolean novoNode) {
		String mensagem = "";
		try {
			
			//Set para tipo da mensagem como 1(handshake) e a identidade do nó a qual faz parte;
			mensagem = TipoMensagem.HANDSHAKE.getCodigo().toString()  + ";";
			mensagem += ident + ";";
			
			if(novoNode) {
				//Caso seja um novo nó entrando no grupo multicast
				mensagem += "true;";
			}else {
				//Caso seja um nó que já exista no grupo multicast;
				mensagem += "false;";
			}
			
			//Adiciona o chave pública do nó para validação;
			mensagem += assinatura.publicKeyToString(chavePublica);
			
		}catch (NoSuchAlgorithmException e){System.out.println("enviaHandskake - NoSuchAlgorithmException: " + e.getMessage());
		}catch (Exception e){System.out.println("enviaHandskake - Exception: " + e.getMessage());}
		
		return mensagem;
	}

	public void parseMensagem(DatagramPacket messageIn) {
		
		try {
			String mensagem = new String(messageIn.getData());
			
			//Separa a mensagem em várias partes com base no separador padrão
			String[] mensagemParts = mensagem.split(";");
			
			//Com a primeira parte obtem qual o tipo da mensagem enviada
			TipoMensagem tipoMensagem = TipoMensagem.findByCodigo(Integer.parseInt(mensagemParts[0]));
			//ID do nó
			String identNode = mensagemParts[1];
			
			if(!identNode.equals(ident)) {
				Boolean ehNovo = new Boolean(mensagemParts[2]);//Se é alguém entrando novo no grupo multicast ou alguém que já estava no grupo
				String pubKeyString = mensagemParts[3];//Chave pública como string
				
				PublicKey pubKey = assinatura.stringToPublicKey(pubKeyString); //Chave pública como PublicKey
				controleNos.addMulticastPeerNode(identNode, pubKey);//Add o novo nó na lista de nós do emissor (usado para as denuncias)
			}
		}catch (NoSuchAlgorithmException e){System.out.println("enviaHandskake - NoSuchAlgorithmException: " + e.getMessage());
		}catch (Exception e){System.out.println("enviaHandskake - Exception: " + e.getMessage());}
		
	}
}
