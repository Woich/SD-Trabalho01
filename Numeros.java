import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Numeros {
	
	private final List<Integer> numeros = new ArrayList<>();
	
	public Numeros(int de, int ate) {
        //Carrega a lista de n�meros
		for (int numero = de; numero <= ate; numero++) {
            numeros.add(numero);
        }
    }
	
	public int sortear() {
        if (numeros.isEmpty()) {
        	//Caso a lista de portas esteja vazia
        	throw new IllegalStateException("A lista de portas est� vazia!");
        }
        int sorteado = 6789;
        
        while(sorteado == 6789) {
        	//Sorteia enquanto n�o ser a porta multicast
        	sorteado = new Random().nextInt(numeros.size());
        }
        
        return numeros.remove(sorteado);
    }
	
}
