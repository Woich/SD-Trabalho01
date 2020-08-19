/*
 * 
 * Autores:
 * Lucas Shoiti (lucastakahashi@alunos.utfpr.edu.br)
 * Pedro Henrique Woiciechovski (minewoichbr@gmail.com)
 * 
 * */
public enum TipoMensagem {
	
	HANDSHAKE(1, "Handshake"),
	NOTICIA(2, "Noticia"),
	DENUNCIA(3, "Denuncia"),
	GOODBYE(4, "Goodbye");
	
	private TipoMensagem(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;	
	}
	
	Integer codigo;
	String descricao;
	
	public Integer getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static TipoMensagem findByCodigo(Integer codigo) {
		for(TipoMensagem tm : values()) {
			if(tm.getCodigo() == codigo) {
				return tm;
			}
		}
		
		return null;
	}
}
