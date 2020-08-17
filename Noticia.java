
public class Noticia {
	
	private String identOrigem;
	private String noticia;
	private byte[] assinatura;
	
	public Noticia(String identOrigem, String noticia, byte[] assinatura) {
		this.identOrigem = identOrigem;
		this.noticia = noticia;
		this.assinatura = assinatura;
	}
	
	public String getIdentOrigem() {
		return identOrigem;
	}
	
	public String getNoticia() {
		return noticia;
	}
	
	public byte[] getAssinatura() {
		return assinatura;
	}
	
}
