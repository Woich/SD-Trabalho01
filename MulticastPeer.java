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

public class MulticastPeer{
	
	public static void main(String args[]){ 
		// args give message contents and destination multicast group (e.g. "228.5.6.7")
		MulticastSocket s =null;
		
		try {
			int escolha = 0;
			int portaMulticast = 6789;
			int portaUnicast = 9876;
			
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
			
			System.out.println("Chave Pública:");	
			System.out.println(chavePublica);	
			
			//Inicializa o emissor de mensagens
			EmissorMensagem emissorMensagem = new EmissorMensagem(ident, portaMulticast, portaUnicast,group, assinatura, chavePublica);
			//Envia via multicast um handshake para informar que entrou no grupo
			emissorMensagem.enviaHandskake(true);
			
			//Inicializa o receptor de mensagens
			ReceptorMensagem receptor = new ReceptorMensagem(emissorMensagem, ident, group, portaMulticast);
			
			
			
			 while(escolha != -1){
				 System.out.println("Digite uma opção: ");
				 System.out.println("1 - Digitar uma notícia");
				 System.out.println("2 - Ler notícias recebidas");
				 System.out.println("3 - Listar nós");
				 System.out.println("-1 - Sair");
				 
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
				 	case -1:
						s.leaveGroup(group);
						receptor.stop();
						break;
					default:
						break;
				 }
			 }
			 											
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}catch (NoSuchAlgorithmException e){System.out.println("IO: " + e.getMessage());
		}finally {if(s != null) s.close();}
	}
	
}
