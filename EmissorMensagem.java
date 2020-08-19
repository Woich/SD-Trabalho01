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
import java.net.DatagramSocket;

public class EmissorMensagem {
	
	private String ident;
	private int portaMulticast;
	private int portaUnicast;
	private MulticastSocket socket;
	private InetAddress group;
	private Assinatura assinatura;
	private PublicKey chavePublica;
	
	DatagramPacket messageOut;
	
	public EmissorMensagem(String ident, int portaMulticast, int portaUnicast,
			InetAddress group, Assinatura assinatura, PublicKey chavePublica)  throws IOException{
		//Declara as informa��es do emissor com base as informa��es do n� a qual ele pertence
		this.ident = ident;
		this.portaMulticast = portaMulticast;
		this.portaUnicast = portaUnicast;
		this.group = group;
		this.assinatura = assinatura;
		this.chavePublica = chavePublica;
		
		this.socket = new MulticastSocket(portaMulticast);
		this.socket.joinGroup(group);
	}
    
	public void enviaHandskake(boolean novoNode, int portaUnicastEnvio) {
		
		try {
			
			String mensagem = "";
			//Set para tipo da mensagem como 1(handshake) e a identidade do n� a qual faz parte;
			mensagem = TipoMensagem.HANDSHAKE.getCodigo().toString()  + ";";
			mensagem += ident + ";";
			
			//Set da porta unicast para caso seja necess�rio enviar as informa��es de entrada;
			mensagem += portaUnicast + ";";
			
			if(novoNode) {
				//Caso seja um novo n� entrando no grupo multicast
				mensagem += "true;";
			}else {
				//Caso seja um n� que j� exista no grupo multicast;
				mensagem += "false;";
			}
			
			//Adiciona o chave p�blica do n� para valida��o;
			mensagem += assinatura.publicKeyToString(chavePublica);
			
			if(novoNode) {
				//Gera o datagrama e envia para o grupo;
				DatagramPacket messageOut = new DatagramPacket(mensagem.getBytes(), mensagem.length(), group, 6789);
				socket.send(messageOut);
			}else {
				//Faz o envio por unicast do handshake
				//Abre o socket
				DatagramSocket dataSocket = new DatagramSocket();
				//Gera o datagrama a ser enviado
				DatagramPacket messageOut = new DatagramPacket(mensagem.getBytes(), mensagem.length(), group, portaUnicastEnvio);
				dataSocket.send(messageOut);
				dataSocket.close();
			}
			
		}catch (IOException e){System.out.println("enviaHandskake - IOException: " + e.getMessage());
		}catch (NoSuchAlgorithmException e){System.out.println("enviaHandskake - NoSuchAlgorithmException: " + e.getMessage());
		}catch (InvalidKeySpecException e){System.out.println("enviaHandskake - InvalidKeySpecException: " + e.getMessage());
		}catch (Exception e){System.out.println("enviaHandskake - Exception: " + e.getMessage());
		}finally {if(socket != null) socket.close();}
		
	}
	
	public void enviaNoticia() {
		
		try {
			
			//Abre o socket para enviar a not�cia
			this.socket = new MulticastSocket(portaMulticast);
			this.socket.joinGroup(group);
			
			String mensagem = "";
			
			//Set para tipo da mensagem como 2(not�cia) e a identidade do n� a qual faz parte;
			mensagem = TipoMensagem.NOTICIA.getCodigo().toString()  + ";";
			mensagem += ident + ";";
			
			//Obtem qual � a not�cia;
			System.out.println("Escreva a noticia: \n");
			Scanner leitor = new Scanner(System.in);
			
			String noticia = leitor.next();
			
			mensagem += Base64.getEncoder().encodeToString(assinatura.geraAssinatura(noticia)) + ";" ;

			mensagem += noticia + ";";
			
			//Assina a not�cia;
			
			
			//Gera o datagrama e envia a not�cia para o grupo multicast;
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
					
					//Abre o socket para enviar a not�cia
					this.socket = new MulticastSocket(portaMulticast);
					this.socket.joinGroup(group);
					
					String mensagem = "";
					
					//Set para tipo da mensagem como 2(not�cia) e a identidade do n� a qual faz parte;
					mensagem = TipoMensagem.DENUNCIA.getCodigo().toString()  + ";";
					mensagem += ident + ";";
					
					//Assina a not�cia;
					mensagem += assinatura.geraAssinatura(mensagem);
					
					//Gera o datagrama e envia a not�cia para o grupo multicast;
					DatagramPacket messageOut = new DatagramPacket(mensagem.getBytes(), mensagem.length(), group, 6789);
					socket.send(messageOut);
					
				}catch (IOException e){System.out.println("enviaNoticia - IOException: " + e.getMessage());
				}catch (NoSuchAlgorithmException e){System.out.println("enviaNoticia - NoSuchAlgorithmException: " + e.getMessage());
				}catch (InvalidKeyException e){System.out.println("enviaNoticia - InvalidKeyException: " + e.getMessage());
				}catch (SignatureException e){System.out.println("enviaNoticia - SignatureException: " + e.getMessage());
				}finally {if(socket != null) socket.close();}
		
	}
	
	public void enviaGoodbye() {}
	
}
