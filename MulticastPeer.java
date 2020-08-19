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
	//Lista global de nós
	public static EmissorMensagem emissorMensagem;
	
	public static void main(String args[]){ 
		MulticastSocket s =null;
		
		try {
			int escolha = 0;
			int portaMulticast = 6789;
			
			//Gera uma id aleatório para o nó 
			UUID uuid = UUID.randomUUID();
			String ident = uuid.toString();
			
			//Declaração do grupo e do socket multicast sendo inicializado no grupo
			InetAddress group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(portaMulticast);
			s.joinGroup(group);
			
			//Gera as assinaturas
			Assinatura assinatura = new Assinatura();
			assinatura.createKeys();
			PublicKey chavePublica = assinatura.getPubKey();
			
			//Isntancia a lista de nós
			ControleNos controleNos = new ControleNos();
			
			//Inicializa o emissor de mensagens
			emissorMensagem = new EmissorMensagem(controleNos, ident, portaMulticast,group, assinatura, chavePublica);
			
			//Inicializa o receptor de mensagens
			ReceptorMensagem receptor = new ReceptorMensagem(controleNos, emissorMensagem, ident, group, portaMulticast);
			
			//Envia via multicast um handshake para informar que entrou no grupo
			emissorMensagem.enviaHandskake(true);
			
			 while(escolha != 10){
				 System.out.println("=========================================");
				 System.out.println("Digite uma opção: ");
				 System.out.println("1 - Digitar uma notícia");
				 System.out.println("2 - Ler notícias recebidas");
				 System.out.println("3 - Listar nós");
				 System.out.println("10 - Sair");
				 System.out.println("=========================================");
				 
				 Scanner scanner = new Scanner(System.in);
				 escolha = scanner.nextInt();
				 System.out.println("=========================================");
				 
				 switch(escolha) {
				 	case 1: 
				 		emissorMensagem.enviaNoticia(); 
				 		break;
				 	case 2:
				 		receptor.listarNoticias(); 
				 		break;
				 	case 3:
				 		controleNos.listarNos(); 
				 		break;
				 	case 10:
						System.exit(0);
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
