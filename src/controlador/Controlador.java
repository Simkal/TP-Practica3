package controlador;

import java.util.Scanner;

import logica.Mundo;
import logica.MundoComplejo;
import logica.MundoSimple;

import java.io.*;

import celula.Celula;
import comandos.Comando;
import comandos.ParserComandos;
import excepciones.ErrorDeInicializacion;
import excepciones.FormatoNumericoIncorrecto;
import excepciones.IndicesFueraDeRango;
import excepciones.PalabraIncorrecta;


public class Controlador {
	private Mundo mundo;
	private Scanner in;
	private boolean simulacionTerminada = false;
	
	/**
	 * Metodo constructor del Controlador que define los parametros mundo e in
	 * @param mundo le pasa el nuevo mundo inicializado
	 * @param in le pasa los controles ya inicializados
	 */
    public Controlador(Mundo mundo, Scanner in){
        this.mundo = mundo;
        this.in = in;
    }
    
    /**
	 * Metodo que valida que los valores de fila y columna que pasa el usuario son validos
	 * @param f valores enteros positivos de fila
	 * @param c valores enteros positivos de columna
	 * @return TRUE si los valores de fila y columna es valido
	 * FALSE si los valores de fila y columna no son correctos, no estan dentro de los parametros definidos
	 */
	public boolean validarDatos(int f, int c){
		return this.mundo.validarDatos(f,c);
	}
    
	/**
	 * Metodo que lee lo que el usuario mete por consola e impide que no sea el comando correcto
	 * @param in le pasa los controles inicializados
	 * @return un string del comando correcto
	 */
    private String[] crearComando(Scanner in){
		System.out.print("Comando > ");
		String string = in.nextLine();
		String stringlower = string.toLowerCase();
		String[] palabras = stringlower.split(" ");
		return  palabras;
    }
    
    /**
     * Metodo encargado de los controles que el usuario introduce para el funcionamiento del juego
     * y encargado de llamar a las funciones en otras clases para mostrar por pantalla el juego y sus movimientos
     * @param  args El contenido de String[] args del main
     * @throws PalabraIncorrecta 
     * @throws IOException para evitar los errores del cargado y el guardado
     */
	public void realizaSimulacion(String[] args) throws IOException, PalabraIncorrecta{
		System.out.println("Bienvenido al juego de la vida: ");
		String mensaje = "";
		String[] palabras;
		boolean inicio = true;
		while (!this.simulacionTerminada){
			Mundo antiguo = this.mundo;
			System.out.println(mundo.toStringBuffer());
			//Si es la llamada inicial y hay algun argumento, la palabra es el argumento
			if (args.length > 0 && inicio){
				palabras = args;
				inicio = false;
			}
			else{
				palabras = crearComando(this.in);
			}
			try {
				Comando comando = ParserComandos.parseaComando(palabras);				
				mensaje = comando.ejecuta(this);
				//Movido para que solo muestre este mensaje si no hay excepciones
				System.out.println(mensaje);				
			}catch (IndicesFueraDeRango e) {
				System.out.println(e.getMessage());
				
			}catch (FormatoNumericoIncorrecto e) {
				System.out.println(e.getMessage());
				
			}catch (ErrorDeInicializacion e) {
				System.out.println(e.getMessage());
				
			}catch (NullPointerException e){
				System.out.println("Comando desconocido (Escriba AYUDA para infomarse de los comandos disponibles)");
			}catch(PalabraIncorrecta e){
				System.out.println(e.Informe()); // Asi evitamos que se cargue el fichero sin la celula erronea y vuelve al mundo anterior
				this.mundo = antiguo;
			}
		}	
	}
	
	/**
	 * Metodo que inicializa juego inicializando el mundo 
	 * @param mundo matriz de la superficie
	 */
	public void juega(Mundo mundo){
		this.mundo = mundo;
	}
	
	/**
	 * Metodo de carga de un fichero que llama a la carga de mundo
	 * @param nombreFichero string de un fichero de texto
	 * @throws PalabraIncorrecta si no lee simple o complejo salta la excepci�n
	 * @throws FormatoNumericoIncorrecto si el nombre del fichero es inesistente devuelve la excepcion
	 */
	 
	public void cargar(String nombreFichero)throws PalabraIncorrecta, FormatoNumericoIncorrecto{
		String nombre = nombreFichero;
		try {
			FileReader archivo = new FileReader(nombre);
			Scanner entrada = new Scanner(archivo);
			String tipo = entrada.nextLine();
			if (tipo.equals("simple")){
				this.mundo = new MundoSimple();
			}
			else if (tipo.equals("complejo")){
				this.mundo = new MundoComplejo();
			}
			else {
				entrada.close();
				throw new PalabraIncorrecta(1, "La palabra que encabeza el fichero no es ni simple ni complejo");
			}
			mundo.cargar(entrada);
			entrada.close();
			System.out.println("Carga realizada con exito");
		} catch (FileNotFoundException e) {
			System.out.println("El nombre del fichero especificado no existe");
		}
	}
	
	/**
	 * Metodo de guardado de una partida en un fichero de texto
	 * @param nombreFichero string del nombre del fichero de texto a guardar 
	 * @throws IOException si no puede guardar la partida salta la excepcion
	 */	 
	public void guardar(String nombreFichero)throws IOException{
		File archivo = new File(nombreFichero);
		FileWriter escribir;
		try {
			escribir = new FileWriter(archivo);			
			escribir.append(mundo.guardar());
			escribir.close();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}
		System.out.println("Partida guardada correctamente");
	}

	/**
	 * Metodo que llama a evoluciona de mundo para realizar el paso
	 * @return string del mundo evolucionado
	 */
	public String daUnPaso() {
		return this.mundo.evoluciona();
	}
	
	/**
	 * Metodo que iguala a true la simulacionTerminada
	 */
	public void terminaSimulacion() {
		this.simulacionTerminada = true;
	}
	
	/**
	 * metodo que llama al vaciar del mundo
	 */
	public void vaciar(){
		this.mundo.vaciar();
	}
	
	/**
	 * Metodo que inicializa el mundo
	 */
	public void generarCelulas(){
		this.mundo.inicializaMundo();
	}
	
	/**
	 * Metodo que elimina una celula de la superficie
	 * @param f numero entero positivo de la fila
	 * @param c numero entero positivo de la columna
	 * @return true si se ha eliminado una celula de la superficie y false si no lo ha hecho
	 */
	public boolean eliminarCelulaSuperficie(int f, int c){
		return this.mundo.eliminarCelulaSuperficie(f,c);
	}
	
	/**
	 * Metodo que crea una celula simple o compleja segun haya sido elegida
	 * @param f numero entero positivo de la fila
	 * @param c numero entero positivo de la columna
	 * @param celula la celula simple o compleja segun sea
	 * @return true si se ha creado una celula de la superficie y false si no lo ha hecho
	 */
	public boolean crearCelulaSuperficie(int f, int c, Celula celula){
		return this.mundo.crearCelulaSuperficie(f,c,celula);
	}	
	
	/**
	 * Permite recoger un comando numerico del Scanner ya abierto sin tener que abrirlo de nuevo 
	 * @return Un entero recogido del Scanner
	 */
	public int getComando(){
		System.out.print("De que tipo: Compleja (1) o Simple (2): ");
		int comando = in.nextInt();
		//Limpio el scanner despues de leer el entero
		in.nextLine();
		return comando;
	}
	
	/**
	 * Metodo que llama al crearcelula de mundo
	 * @param fila numero entero positivo de fila
	 * @param columna numero entero positivo de columna
	 */
	public void crearCelula(int fila,int columna){
		try{
			System.out.println(mundo.crearCelula(fila, columna,this.in));
		}
		catch(IndicesFueraDeRango e){
			System.out.println(e.getMessage());
		}
			
		
	}
}
		
		
		
		
		
		
		
		
