import java.net.*;
import java.security.SignatureException;
import java.security.PublicKey;
import java.util.List;
import java.security.spec.InvalidKeySpecException;

import java.util.Scanner;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.sound.sampled.SourceDataLine;

import java.io.*;
public class MulticastPeer{
    public static void main(String args[]){ 
		// args give message contents and destination multicast group (e.g. "228.5.6.7")
		MulticastSocket s =null;
		//List peers = new List<Peers>();

		try {
			InetAddress group = InetAddress.getByName("228.5.6.7");
			Assinatura assinatura = new Assinatura();
			assinatura.createKeys();
			PublicKey chave = assinatura.getPubKey();

			System.out.println(chave);	


			// String mensagem = "handshake|" + "ident|" + Base64.getEncoder().encodeToString(chave);

			// DatagramPacket messageOut = new DatagramPacket(
			// 		message.getBytes(), 2, group, 6789);
			//   	s.send(messageOut);

			

			// while(true){
			// 	System.Out.Println("Digite uma opção: ");
			//  	System.Out.Println("0 - Ler mensagens");
			//  	System.Out.Println("1 - Digitar uma notícia");
			//  	System.Out.Println("2 - Denunciar uma fake news");
			//  	System.Out.Println("3 - Sair");
			// 	DatagramPacket messageOut = new DatagramPacket(
			// 		message.getBytes(), 2, group, 6789);
			//   	s.send(messageOut);
			// 	byte[] buffer = new byte[1000];
			// 	for(int i=0; i< 4;i++) {		// get messages from others in group
			// 		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			// 		s.receive(messageIn);
			//    	    System.out.println("Received:" + new String(messageIn.getData()));	 
			//     }
			//     break;  
			// }
			// int escolha = 10;
			
			// while(escolha != 3){
			// 	System.Out.Println("Digite uma opção: ");
			// 	System.Out.Println("0 - Ler mensagens");
			// 	System.Out.Println("1 - Digitar uma notícia");
			// 	System.Out.Println("2 - Denunciar uma fake news");
			// 	System.Out.Println("3 - Sair");

				
			// 	Scanner scanner = new Scanner();
			// 	escolha = scanner.scan().nextInt();

			// 	switch(escolha)
			// 	{
			// 		case 0:
			// 			byte[] buffer = new byte[1000];
 			// 			for(int i=0; i< 3;i++) {		// get messages from others in group
 			// 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 			// 				s.receive(messageIn);
			// 				System.out.println("Received:" + new String(messageIn.getData()));	 
			// 			}
			// 			break;  

			// 		case 1:
			// 			System.Out.Println("3 - Sair");
			// 			DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
			// 			s.send(messageOut);
			// 			break;	

			// 		case 2: 

			// 		case 3:
			// 			s.leaveGroup(group);
			// 			break;

			// 		default:
			// 			System.Out.Println("Escolha inválida!");
			// 			break;
			// 	}	
			//}
			 											
		//}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}catch (NoSuchAlgorithmException e){System.out.println("IO: " + e.getMessage());
		}finally {if(s != null) s.close();}
	}		      	
	
}
