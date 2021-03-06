package comandos;

import controlador.Controlador;

public class Paso implements Comando {

	@Override
	public Comando parsea(String[] palabras) {
		Comando comando;
		if(palabras[0].equalsIgnoreCase("paso") && (palabras.length == 1))
			comando = new Paso();
		else comando = null;
		return comando;
	}

	@Override
	public String textoAyuda() {
		return ("PASO: mueve las celulas de lugar en el mundo" + System.getProperty("line.separator"));
	}
	
	@Override
	public String ejecuta(Controlador controlador){
		return controlador.daUnPaso();
	}

}
