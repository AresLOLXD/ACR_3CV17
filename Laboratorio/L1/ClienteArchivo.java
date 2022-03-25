import javax.swing.JFileChooser;//Se importa la biblioteca para el explorador de archivos
import java.net.*;//Se importa las bibliotecas para el uso de Sockets
import java.io.*; //Se importa las bibliotecas para el manejo de streams(flujos)

public class ClienteArchivo {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Crea un lector de buffer para
																						// la entrada por
																						// defecto(Consola)
			System.out.printf("Escriba la dirección del servidor:");
			String host = br.readLine(); // Se lee la direccion recibida por el usuario
			System.out.printf("\n\nEscriba el puerto:");
			int pto = Integer.parseInt(br.readLine()); // Se lee y convierte el puerto recibido por el usuario
			Socket cl = new Socket(host, pto); // Se crea un socket de tipo cliente
			JFileChooser jf = new JFileChooser(); // Utiliza el constructor para el selector de archivos
			jf.setMultiSelectionEnabled(true); // Lo cambiamos para que ahora acepte multiples archivos
			int r = jf.showOpenDialog(null); // Le decimos que no tenemos un componente padre, entonces el creara uno
												// para el explorador
			if (r == JFileChooser.APPROVE_OPTION) { // Significa que si selecciono archivos
				File[] files = jf.getSelectedFiles(); // Obtenemos los archivos seleccionados
				DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Se abre el stream de salida del
																					// socket para el servidor
				dos.writeInt(files.length); // Le enviamos la cantidad de archivos a enviar
				for (File f : files) { // ITeramos sobre cada uno de los archivos en el
					String archivo = f.getAbsolutePath(); // Ruta absoluta del archivo
					String nombre = f.getName(); // Nombre del archivo
					long tam = f.length(); // Tamaño del archivo
					dos.flush(); // Enviamos la informacion
					DataInputStream dis = new DataInputStream(new FileInputStream(archivo)); // Este stream espara poder
																								// leer el archivo y
																								// enviarlo
					dos.writeUTF(nombre); // Le enviamos el nombre
					dos.flush(); // Enviamos la informacion
					dos.writeLong(tam); // Enviamos el tamaño del archivo
					dos.flush(); // Enviamos la informacion
					byte[] b = new byte[1024]; // Creamos un buffer temporal de 1MB para poder enviar chunks del archivo
					long enviados = 0; // Cantidad de chunks enviados
					int porcentaje, n; // Porcentaje enviado y bytes leidos
					while (enviados < tam) { // Iteramos mientras aun no hayamos terminado de leer el archivo
						n = dis.read(b); // Leemos maximo 1MB del archivo
						dos.write(b, 0, n); // Enviamos el chunk leido
						dos.flush(); // Enviamos la informacion
						enviados = enviados + n; // Sumamos la cantidad de bytes leidos
						porcentaje = (int) (enviados * 100 / tam); // Actualizamos el porcentaje
						System.out.print("Enviado: " + porcentaje + "%\r                                     "); // Mostramos
																													// el
																													// porcentaje
					} // While
					System.out.print("\n\nArchivo enviado");
					dis.close(); // Cerramos el buffer del archivo
				}
				dos.close();
				cl.close();
			} // if
		} catch (Exception e) {
			e.printStackTrace(); // Por cualquier cosa
		}
	}
}