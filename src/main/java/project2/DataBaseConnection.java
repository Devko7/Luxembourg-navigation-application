package project2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataBaseConnection {
    private static volatile DataBaseConnection instance;
    private Connection connection;

    private DataBaseConnection() throws SQLException {
        // Get mySQL URL from environment variable.
        String mySQL_URL = System.getenv("ROUTING_ENGINE_MYSQL_JDBC");
        if (mySQL_URL == null || mySQL_URL.isEmpty()) {
            throw new IllegalStateException("Env. variable empty or not set");
        }

        // Change connection properties to allow load infile and set up connection to DB
        Properties properties = new Properties();
        properties.setProperty("allowLoadLocalInfile", "true");
        this.connection = DriverManager.getConnection(mySQL_URL);
    }

    // Separate method to create tables to simplify changing schema and finding bugs.
    private void createTables() throws SQLException{
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS agency ("
                + "agency_id VARCHAR(255) PRIMARY KEY,"
                + "agency_name VARCHAR(255) NOT NULL,"
                + "agency_url VARCHAR(512) NOT NULL,"
                + "agency_timezone VARCHAR(64) NOT NULL,"
                + "agency_lang VARCHAR(16)"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS calendar ("
                + "service_id VARCHAR(255) PRIMARY KEY,"
                + "monday TINYINT NOT NULL,"
                + "tuesday TINYINT NOT NULL,"
                + "wednesday TINYINT NOT NULL,"
                + "thursday TINYINT NOT NULL,"
                + "friday TINYINT NOT NULL,"
                + "saturday TINYINT NOT NULL,"
                + "sunday TINYINT NOT NULL,"
                + "start_date DATE NOT NULL,"
                + "end_date DATE NOT NULL"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS calendar_dates ("
                + "service_id VARCHAR(255) NOT NULL,"
                + "date DATE NOT NULL,"
                + "exception_type TINYINT NOT NULL,"
                + "PRIMARY KEY (service_id, date)"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS routes ("
                + "route_id VARCHAR(255) PRIMARY KEY,"
                + "agency_id VARCHAR(255),"
                + "route_short_name VARCHAR(255),"
                + "route_long_name VARCHAR(255),"
                + "route_type INT NOT NULL,"
                + "FOREIGN KEY (agency_id) REFERENCES agency(agency_id)"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS trips ("
                + "route_id VARCHAR(255) NOT NULL,"
                + "service_id VARCHAR(255) NOT NULL,"
                + "trip_id VARCHAR(255) PRIMARY KEY,"
                + "trip_headsign VARCHAR(255),"
                + "trip_short_name VARCHAR(255),"
                + "direction_id TINYINT,"
                + "block_id VARCHAR(255),"
                + "shape_id VARCHAR(255),"
                + "wheelchair_accessible TINYINT,"
                + "bikes_allowed TINYINT,"
                + "FOREIGN KEY (route_id) REFERENCES routes(route_id),"
                + "FOREIGN KEY (service_id) REFERENCES calendar(service_id)"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS stops ("
                + "stop_id VARCHAR(255) PRIMARY KEY,"
                + "stop_code VARCHAR(255),"
                + "stop_name VARCHAR(255) NOT NULL,"
                + "stop_desc VARCHAR(255),"
                + "stop_lat DOUBLE NOT NULL,"
                + "stop_lon DOUBLE NOT NULL"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS stop_times ("
                + "trip_id VARCHAR(255) NOT NULL,"
                + "arrival_time TIME NOT NULL,"
                + "departure_time TIME NOT NULL,"
                + "stop_id VARCHAR(255) NOT NULL,"
                + "stop_sequence INT NOT NULL,"
                + "pickup_type TINYINT,"
                + "drop_off_type TINYINT,"
                + "stop_headsign VARCHAR(255) NOT NULL,"
                + "PRIMARY KEY (trip_id, stop_sequence),"
                + "FOREIGN KEY (trip_id) REFERENCES trips(trip_id),"
                + "FOREIGN KEY (stop_id) REFERENCES stops(stop_id)"
                + ")"
            );

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS transfers ("
                + "from_stop_id VARCHAR(255) NOT NULL,"
                + "to_stop_id VARCHAR(255) NOT NULL,"
                + "transfer_type TINYINT NOT NULL,"
                + "min_transfer_time INT,"
                + "PRIMARY KEY (from_stop_id, to_stop_id),"
                + "FOREIGN KEY (from_stop_id) REFERENCES stops(stop_id),"
                + "FOREIGN KEY (to_stop_id) REFERENCES stops(stop_id)"
                + ")"
            );
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            // Reopen connection if closed
            String mySQL_URL = System.getenv("ROUTING_ENGINE_MYSQL_JDBC");
            Properties properties = new Properties();
            properties.setProperty("allowLoadLocalInfile", "true");
            this.connection = DriverManager.getConnection(mySQL_URL);
        }

        return this.connection;
    }

    public static DataBaseConnection getInstance() {
        synchronized (DataBaseConnection.class) {
            if (instance == null) {
                try {
                    instance = new DataBaseConnection();
                } 
                catch (SQLException e) {
                    throw new RuntimeException("Error initializing database", e);
                }
            }
        }
        return instance;
    }

    // Helper method to unzip GTFS zip file.
    private Path unzipGTFSZip(String zipFilePath) throws IOException {
        Path tempDir = Files.createTempDirectory("gtfs_unzipped_");

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) { 
                    continue;
                }

                Path outPath = tempDir.resolve(entry.getName());
                Files.createDirectories(outPath.getParent());

                try (OutputStream os = Files.newOutputStream(outPath)) {
                    byte[] buffer = new byte[4096];
                    int len;

                    while ((len = zis.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                    }
                }
            }
        }
        return tempDir;
    }

    // Loads GTFS data into database
    public void loadGTFS(String path) throws IOException, SQLException {
        // Delete previous tables if any existed
        dropAllTables();

        // Create new tables
        createTables();

        Path p = Paths.get(path);
        if (Files.isRegularFile(p) && path.toLowerCase().endsWith(".zip")) {
            Path extractedDir = unzipGTFSZip(path);
            loadGTFSFromDirectory(extractedDir);
        } else if (Files.isDirectory(p)) {
            loadGTFSFromDirectory(p);
        } else {
            throw new IllegalArgumentException("Provided path is neither a directory nor a zip file: " + path);
        }
    }

    private void loadGTFSFromDirectory(Path directory) throws IOException, SQLException {
        List<String> loadOrder = Arrays.asList(
            "agency.txt",
            "stops.txt",
            "routes.txt",
            "calendar.txt",
            "calendar_dates.txt",
            "trips.txt",
            "stop_times.txt",
            "transfers.txt"
        );

        for (String fileName : loadOrder) {
            Path filePath = directory.resolve(fileName);
            if (Files.exists(filePath)) {
                String tableName = fileName.substring(0, fileName.length() - 4); // Remove .txt
                if (tableExists(tableName)) {
                    loadFileIntoTable(filePath, tableName);
                }
            }
        }
    }

    private void loadFileIntoTable(Path filePath, String tableName) throws SQLException {
        String sql = "LOAD DATA LOCAL INFILE '" + filePath.toAbsolutePath().toString().replace("\\", "\\\\") + "' " +
                    "INTO TABLE " + tableName + " " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"' " +
                    "LINES TERMINATED BY '\r\n' " +
                    "IGNORE 1 ROWS";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw e;
        }
    }

    // Helper method to check if table was initialized in DB
    private boolean tableExists(String name) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, name, new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    public void dropAllTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

            stmt.executeUpdate("DROP TABLE IF EXISTS stop_times");
            stmt.executeUpdate("DROP TABLE IF EXISTS transfers");
            stmt.executeUpdate("DROP TABLE IF EXISTS trips");
            stmt.executeUpdate("DROP TABLE IF EXISTS routes");
            stmt.executeUpdate("DROP TABLE IF EXISTS calendar_dates");
            stmt.executeUpdate("DROP TABLE IF EXISTS calendar");
            stmt.executeUpdate("DROP TABLE IF EXISTS stops");
            stmt.executeUpdate("DROP TABLE IF EXISTS agency");

            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
}