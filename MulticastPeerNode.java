import java.security.PublicKey;

/*
 * 
 * Autores:
 * Lucas Shoiti (lucastakahashi@alunos.utfpr.edu.br)
 * Pedro Henrique Woiciechovski (minewoichbr@gmail.com)
 * 
 * */
public class MulticastPeerNode {
	
	private String ident;
	private PublicKey chavePublica;
	private int denuncias;
	
	public MulticastPeerNode(String ident, PublicKey chavePublica) {
		this.ident = ident;
		this.chavePublica = chavePublica;
		this.denuncias = 0;
	}
	
	public void addDenuncia() {
		denuncias += 1;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public PublicKey getChavePublica() {
		return chavePublica;
	}
	
	public int getDenuncias() {
		return denuncias;
	}
}
