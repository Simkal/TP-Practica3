package celula;

import excepciones.FormatoNumericoIncorrecto;
import logica.Casilla;
import logica.CasillaMensaje;
import logica.Superficie;

public class CelulaSimple implements Celula {
	private static final int MAX_PASOS_SIN_MOVER = 1;
	private static final int PASOS_REPRODUCCION = 2;
	private int pasosSinMover;
	private int pasosReproduccion;
	
	/**
	 * Metodo constructor generico de la clase CelulaSimple definido para las celulas basicas del juego
	 */
	public CelulaSimple(){
		this.pasosSinMover = MAX_PASOS_SIN_MOVER;
		this.pasosReproduccion = PASOS_REPRODUCCION;
	}
	
	/**
	 * Constructor de una CelulaSimple con argumentos
	 * @param SinMover Numero de pasos que puede pasar la celula sin moverse
	 * @param Reproduccion Numero de pasos que le quedan a la celula para reproducirse
	 */
	public CelulaSimple(int SinMover, int Reproduccion){
		this.pasosSinMover = SinMover;
		this.pasosReproduccion = Reproduccion;
	}
		
	@Override
	public CasillaMensaje ejecutaMovimiento(int f, int c, Superficie superficie){
		Casilla casillaMovimiento;		
		CasillaMensaje casillaMensaje;
		if (this.pasosSinMover != 0){
			int cont = 0;
			int [] fila = {-1, 0, 1};
			Casilla[] casilla = new Casilla[8];
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					int nf = f + fila[i];
					int nc = c + fila[j];
					if (nf < 0){
						nf = superficie.getFilas() - 1;
					}
					else if (nf == superficie.getFilas()){
						nf = 0;
					}
					if (nc < 0){
						nc = superficie.getColumnas() - 1;
					}
					else if (nc == superficie.getColumnas()){
						nc = 0;
					}
					if (superficie.casillaVacia(nf,nc)){
						casilla[cont] = new Casilla(nf, nc);
						cont++;
					}
			 	}
			}
			//Indica que no hay casillas vacias, por lo que no se puede mover
			if (cont == 0){
				//Si no se puede mover y esta por reproducirse, la celula muere
				if (this.pasosReproduccion < 0){
					superficie.vaciarCasilla(f, c);
					casillaMensaje = new CasillaMensaje (null, "Muere la celula simple de la casilla (" + f + "," + c + ") por no poder reproducirse");
				}
				else {
					this.pasosSinMover--;
					casillaMensaje = new CasillaMensaje (null, "La celula simple de la casilla (" + f + "," + c + ") no se puede mover");
				}
			}
			//Si se mueve la celula, entonces decrementamos sus contadores
			else {
				casillaMovimiento = casilla[(int) (Math.random() * (cont - 1))];
				int k = casillaMovimiento.getFila(), l = casillaMovimiento.getColumna();
				this.pasosReproduccion--;
				if (this.pasosReproduccion < 0){
					this.pasosReproduccion = PASOS_REPRODUCCION;
					superficie.moverCelula(k, l, f, c);
					superficie.llenarCasilla(f, c, new CelulaSimple());
					casillaMensaje = new CasillaMensaje (casillaMovimiento,"Movimiento de celula simple de (" + f + "," + c + ") a (" + k + "," + l + ")"
							+ System.getProperty("line.separator") + "Nace nueva celula simple en (" + f + "," + c + ")" + " cuyo padre ha sido (" + k + "," + l + ")");
				}
				else {
					casillaMensaje = new CasillaMensaje (casillaMovimiento, "Movimiento de celula simple de (" + f + "," + c + ") a (" + k + "," + l + ")");
					superficie.moverCelula(k, l, f, c);
				}
			}
		}
		else {
			superficie.vaciarCasilla(f, c);
			casillaMensaje = new CasillaMensaje (null, "Muere la celula simple de la casilla (" + f + "," + c + ") por inactividad");
		}
		return casillaMensaje;
	}
	
	/**
     * Coge los valores numericos de pasosSinMover y pasosReproduccion y los convierte en una cadena para mostrar
     * @return una cadena con los dos parametros numericos de la celula, separados por un guion
     */
	public String toStringBuffer() {
		return (this.pasosSinMover + "-" + this.pasosReproduccion);
	}
	
	@Override
	public boolean esComestible() {
		return true;
	}
	
	@Override
	public void cargar(String[] datos) throws FormatoNumericoIncorrecto{
		try{
			this.pasosReproduccion = Integer.parseInt(datos[4]);
			this.pasosSinMover = Integer.parseInt(datos[3]);
		}catch(NumberFormatException e){
			throw new FormatoNumericoIncorrecto("La linea tiene parametros con formato erroneo");
		}
	}
	
	@Override
	public String guardar() {
		return "simple " + this.pasosReproduccion + " " + this.pasosSinMover;
	}	
}
