package clientserveur.tp01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Timer;
import java.util.TimerTask;

public class MMapServer {
	public final static String MAP_FILE = "/tmp/tp01.map";
	public final static int MAX_NUM = 4;

	public MMapServer() {
	}

	private void initMapFile() throws IOException {
		final FileChannel map_file = FileChannel.open(Paths.get(MAP_FILE), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.READ);

		FileLock lock = map_file.tryLock();
		if (lock == null)
			throw new IOException("Cannot acquire lock on " + MAP_FILE);

		// Section critique, on a un accès exclusif au fichier

		// On "mmappe" le fichier, c'est à dire qu'on obtient un tableau d'octet
		// en mémoire.
		// Les modifications faites dans ce tableau sont reflétée dans le
		// fichier au moment
		// de la fermeture. 4 est la taille d'un int java en octet.

		MappedByteBuffer buff = map_file.map(MapMode.READ_WRITE, 0L, 4 * MAX_NUM);

		// On ontient une "vue" tableau d'entiers à partir du tableau d'octets.
		IntBuffer ibuff = buff.asIntBuffer();

		// On mets toutes les cases à 0.
		for (int i = 0; i < MAX_NUM; i++)
			ibuff.put(i, 0);

		// Fin de section critique.
		lock.release();

		map_file.close();

	}

	private MappedByteBuffer openMapFile() throws IOException {
		// ouverture du fichier en lecture seulement
		final FileChannel map_file = FileChannel.open(Paths.get(MAP_FILE), StandardOpenOption.READ);
		MappedByteBuffer map = map_file.map(MapMode.READ_ONLY, 0, 4 * MAX_NUM);
		map_file.close();
		return map;
	}

	private Timer monitor(final IntBuffer b) throws IOException {

		TimerTask task = new TimerTask() {
			private IntBuffer ibuff = b;
			private long origin = java.lang.System.currentTimeMillis();
			public void run() {
				
				for (int i = 0; i < MAX_NUM; i++)
					System.out.print(i + ": " + ibuff.get(i) + "  ");
				long now = java.lang.System.currentTimeMillis() - origin;
				System.out.println("("+ (now/1000) + "s)");
			
			}
		};

		Timer t = new Timer();
		t.schedule(task, 0, 5000); // affiche toutes les 5s
		return t;
	}

	public void mainLoop() throws IOException {
		Timer timer = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Choisir une action :\n" + "- [i]nitialiser le fichier de map\n"
					+ "- [c]ommencer le monitoring\n" + "- [t]erminer le monitoring\n" + "- [q]uiter\n");
			switch (input.readLine()) {

			case "i":
				initMapFile();
				break;
			case "c":
				MappedByteBuffer buff = openMapFile();
				timer = monitor(buff.asIntBuffer());
				break;

			case "t":
				if (timer != null)
					timer.cancel();
				break;
			case "q":
				if (timer != null)
					timer.cancel();
				return;
			default:
				System.out.println("Commande invalide\n");
			}

		}

	}

	public static void main(String[] args) {
		try {
			MMapServer serv = new MMapServer();
			serv.mainLoop();

		} catch (Exception e) {
			System.err.println("Erreur :\n");
			e.printStackTrace(System.err);
		}
	}

}
