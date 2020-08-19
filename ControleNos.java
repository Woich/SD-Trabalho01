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

public class ControleNos {
	
	public List<MulticastPeerNode> listaNos = new ArrayList<MulticastPeerNode>();
	
	 public void addMulticastPeerNode(String ident, PublicKey chavePublica) {
	    	//Inicializa um novo nó com a chega de um handshake
	    	MulticastPeerNode multicastPeerNode = new MulticastPeerNode(ident, chavePublica);
	    	//Adiciona ele na lista de nós já existentes
	    	listaNos.add(multicastPeerNode);
	    }

	    public void listarNos() {
			if(!listaNos.isEmpty()) {
			    		
			    		for(MulticastPeerNode node : listaNos) {
			    			System.out.println("---------------------------------------------------------------------------");
			    			System.out.println("");
			    			System.out.println("Nó: " + node.getIdent());
			    			System.out.println("Denuncias: " + node.getDenuncias());
			    			System.out.println("");
			    		}
			    		
			    	}
	    }

	    
	    public PublicKey obterPubKeyById(String ident) {
	    	//Caso a identidade e a lista não sejam vazias
	    	if(ident != null && !listaNos.isEmpty()) {
	    		for(MulticastPeerNode node : listaNos) {
	        		//Caso ache o nó retorna a chave pública do mesmo;
	    			if(ident.equals(node.getIdent())) {
	    				return node.getChavePublica();
	        		}
	        	}
	    	}
	    	
	    	//Caso não ache o nó retorna uma mensagem de erro ao achar o nó;
	    	System.out.println("Nó não encontrado!");
	    	return null;
	    }
	    
	    public void disparaDenuncia(String ident) {
	    	if(ident != null && !listaNos.isEmpty()) {
	    		for(MulticastPeerNode node : listaNos) {
	        		//Caso ache o nó retorna a chave pública do mesmo;
	    			if(ident.equals(node.getIdent())) {
	    				node.addDenuncia();
	    				//emissorMensagem.enviaDenuncia(ident);
	        		}
	        	}
	    	}
	    }
	
}
