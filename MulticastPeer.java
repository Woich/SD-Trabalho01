import java.net.*;
import java.security.SignatureException;
import java.security.PublicKey;
import java.util.List;
import java.security.spec.InvalidKeySpecException;

import java.util.Scanner;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.sound.sampled.SourceDataLine;
import java.util.UUID;
import java.io.*;
import java.util.Random;

public class MulticastPeer{
	
	public static void main(String args[]){ 
		MulticastSocket s =null;
		
		try {
			int escolha = 0;
			int portaMulticast = 6789;
			
			//Gera lista de n�meros de 1024  at� 9999
			Numeros listaPortas = new Numeros(1024, 9999);
			//Obtem um n�mero aleat�rio da lista para ser a porta unicast
			int portaUnicast = listaPortas.sortear();
			System.out.println("Porta Unicast: " + portaUnicast);
			//Gera uma id aleat�rio para o n� 
			UUID uuid = UUID.randomUUID();
			String ident = uuid.toString();
			
			//Declara��o do grupo e do socket multicast sendo inicializado no grupo
			InetAddress group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(portaMulticast);
			s.joinGroup(group);
			
			//Gera as assinaturas
			Assinatura assinatura = new Assinatura();
			assinatura.createKeys();
			PublicKey chavePublica = assinatura.getPubKey();
			
			/*System.out.println("Chave P�blica:");	
			System.out.println(chavePublica);	*/
			
			//Inicializa o emissor de mensagens
			EmissorMensagem emissorMensagem = new EmissorMensagem(ident, portaMulticast, portaUnicast,group, assinatura, chavePublica);
			
			//Envia via multicast um handshake para informar que entrou no grupo
			emissorMensagem.enviaHandskake(true, 0);
			
			//Inicializa o receptor de mensagens
			ReceptorMensagem receptor = new ReceptorMensagem(emissorMensagem, portaUnicast, ident, group, portaMulticast);
			
			
			
			 while(escolha != 10){
				 System.out.println("Digite uma op��o: ");
				 System.out.println("1 - Digitar uma not�cia");
				 System.out.println("2 - Ler not�cias recebidas");
				 System.out.println("3 - Listar n�s");
				 System.out.println("10 - Sair");
				 
				 Scanner scanner = new Scanner(System.in);
				 escolha = scanner.nextInt();
				 
				 switch(escolha) {
				 	case 1: 
				 		emissorMensagem.enviaNoticia(); 
				 		break;
				 	case 2:
				 		receptor.listarNoticias(); 
				 		break;
				 	case 3:
				 		receptor.listarNos(); 
				 		break;
				 	case 10:
						s.leaveGroup(group);
						receptor.paraEscuta();
						receptor.stop();
						break;
					default:
						break;
				 }
			 }
			 											
		}catch (IOException e){System.out.println("Multicastpeer - IOException: " + e.getMessage());
		}catch (NoSuchAlgorithmException e){System.out.println("Multicastpeer - NoSuchAlgorithmException: " + e.getMessage());
		}catch (IllegalStateException e){System.out.println("Multicastpeer - IllegalStateException: " + e.getMessage());
		}finally {if(s != null) s.close();}
	}
	
}
