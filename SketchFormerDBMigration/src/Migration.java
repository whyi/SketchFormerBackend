import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Migration {

	public static String MESH_PATH = "/meshes";

	public static ArrayList<Path> getFiles() throws IOException {
		ArrayList<Path> files = new ArrayList<Path>();
		final String dir = System.getProperty("user.dir");
		Files.walk(Paths.get(dir + MESH_PATH)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				files.add(filePath);
			}
		});

		return files;
	}

	private static String readFileContent(final Path filePath)
			throws IOException {
		return Files.lines(filePath).collect(Collectors.joining(""));
	}

	public static void main(String[] args) throws Exception {
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient("localhost", 27017);
			final DB db = mongoClient.getDB("sails");
			final DBCollection coll = db.getCollection("mesh");
			coll.drop();

			final ArrayList<Path> filePaths = getFiles();
			filePaths.forEach(filePath -> {
				String content = null;
				try {
					content = readFileContent(filePath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BasicDBObject mesh = new BasicDBObject("title", filePath
						.getFileName().toString()).append("content", content)
						.append("size", content.length());
				coll.insert(mesh);

			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
