import java.security.PublicKey;

public class MulticastPeerNode {
	
	private String ident;
	private int portaUnicast;
	private PublicKey chavePublica;
	private int denuncias;
	
	public MulticastPeerNode(String ident, int portaUnicast, PublicKey chavePublica) {
		this.ident = ident;
		this.portaUnicast = portaUnicast;
		this.chavePublica = chavePublica;
	}
	
	public void addDenuncia() {
		denuncias += 1;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public int getPortaUnicast() {
		return portaUnicast;
	}
	
	public PublicKey getChavePublica() {
		return chavePublica;
	}
	
	public int getDenuncias() {
		return denuncias;
	}
}
