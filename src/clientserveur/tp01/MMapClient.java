package clientserveur.tp01;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MMapClient {
	private int number;
	private static int MAX_COUNT = 10000;

	public MMapClient(int number) {
		this.number = Math.min(MMapServer.MAX_NUM, Math.max(number, 0));
	}

	public void mainLoop() throws IOException {

		System.out.println("Écriture dans l'emplacement " + this.number);
		final FileChannel map_file = FileChannel.open(Paths.get(MMapServer.MAP_FILE), 
				StandardOpenOption.WRITE,
				StandardOpenOption.READ);

		// Ouverture du fichier en lecture/ecriture
		for (int i = 0; i < MAX_COUNT; i++) {
			// 
			// Initialement, on fera uniquement .lock() sur tout le fichier
			// Ce qui empêche deux processus ayant des numéros différents
			// d'accéder de manière concurrente à la map.
			FileLock lock = map_file.lock(number * 4, 4, false);

			MappedByteBuffer buff = map_file.map(MapMode.READ_WRITE, number * 4, 4);
			IntBuffer ibuff = buff.asIntBuffer();
			ibuff.put(0, ibuff.get(0) + 1);
			buff.force();
			lock.release();
		}
		System.out.println("Fin d'écriture dans l'emplacement " + this.number);

	}

	public static void main(String[] args) {
		try {
			MMapClient c = new MMapClient(Integer.parseInt(args[0]));
			c.mainLoop();
		} catch (Exception e) {
			System.err.println("Erreur :\n");
			e.printStackTrace(System.err);
		}
	}
}
