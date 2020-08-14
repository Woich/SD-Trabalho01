
public enum TipoMensagem {
	
	HANDSHAKE(1, "handshake"),
	MENSAGEM(2, "mensagem");
	
	private TipoMensagem(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;	
	}
	
	int codigo;
	String descricao;
	
	public int getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static TipoMensagem findByCodigo(int codigo) {
		for(TipoMensagem tm : values()) {
			if(tm.getCodigo() == codigo) {
				return tm;
			}
		}
		
		return null;
	}
}
