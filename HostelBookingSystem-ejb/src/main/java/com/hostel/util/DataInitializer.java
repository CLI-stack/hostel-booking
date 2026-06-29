package com.hostel.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Singleton EJB — bootstraps MySQL database schema + seed data on first startup.
 */
@DataSourceDefinition(
    name        = "java:app/jdbc/hostelDB",
    className   = "com.mysql.cj.jdbc.MysqlDataSource",
    url = "jdbc:mysql://localhost:3307/hosteldb?zeroDateTimeBehavior=CONVERT_TO_NULL",
    user        = "root",         // Change to your MySQL username
    password="",
    minPoolSize = 5,
    maxPoolSize = 20
)
@Singleton
@Startup
public class DataInitializer {

    private static final Logger LOG = Logger.getLogger(DataInitializer.class.getName());

    @EJB
    private SeedService seedService;

    @PostConstruct
    public void init() {
        LOG.info("DataInitializer: bootstrapping MySQL schema...");
        createSchema();
        LOG.info("DataInitializer: schema ready — seeding data...");
        seedService.seed();
        LOG.info("DataInitializer: startup complete.");
    }

    private void createSchema() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:app/jdbc/hostelDB");
            try (Connection conn = ds.getConnection();
                 Statement stmt = conn.createStatement()) {

                // MySQL equivalent for EclipseLink SEQUENCE generator table
                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS SEQ_GEN (" +
                    "  SEQ_NAME VARCHAR(50) PRIMARY KEY," +
                    "  SEQ_COUNT DECIMAL(15,0)" +
                    " )");
                
                // Initialize the sequence row if it doesn't exist
                stmt.executeUpdate(
                    "INSERT IGNORE INTO SEQ_GEN (SEQ_NAME, SEQ_COUNT) VALUES ('SEQ_GEN', 0)");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  username VARCHAR(50) NOT NULL UNIQUE," +
                    "  password VARCHAR(255) NOT NULL," +
                    "  full_name VARCHAR(100) NOT NULL," +
                    "  email VARCHAR(100) NOT NULL UNIQUE," +
                    "  phone VARCHAR(20)," +
                    "  matric_number VARCHAR(20) UNIQUE," +
                    "  role VARCHAR(10) NOT NULL," +
                    "  is_active BOOLEAN NOT NULL DEFAULT TRUE," +
                    "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + // FIXED
                    "  updated_at TIMESTAMP NULL" + 
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS rooms (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  room_number VARCHAR(10) NOT NULL UNIQUE," +
                    "  type VARCHAR(10) NOT NULL," +
                    "  capacity INT NOT NULL," +
                    "  price_per_semester DECIMAL(10,2) NOT NULL," +
                    "  status VARCHAR(15) NOT NULL," +
                    "  floor INT NOT NULL," +
                    "  block VARCHAR(10)," +
                    "  description VARCHAR(500)," +
                    "  has_wifi BOOLEAN NOT NULL DEFAULT TRUE," +
                    "  has_ac BOOLEAN NOT NULL DEFAULT FALSE," +
                    "  has_attached_bathroom BOOLEAN NOT NULL DEFAULT FALSE," +
                    "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + // FIXED
                    "  updated_at TIMESTAMP NULL" +
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS registration_periods (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  name VARCHAR(100) NOT NULL," +
                    "  start_date DATE NOT NULL," +
                    "  end_date DATE NOT NULL," +
                    "  semester VARCHAR(20)," +
                    "  academic_year VARCHAR(20)," +
                    "  is_active BOOLEAN NOT NULL DEFAULT TRUE," +
                    "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" + // FIXED
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  student_id BIGINT NOT NULL," +
                    "  room_id BIGINT NOT NULL," +
                    "  registration_period_id BIGINT NOT NULL," +
                    "  booking_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + // FIXED
                    "  start_date DATE," +
                    "  end_date DATE," +
                    "  status VARCHAR(15) NOT NULL," +
                    "  rejection_reason VARCHAR(500)," +
                    "  remarks VARCHAR(500)," +
                    "  processed_by BIGINT," +
                    "  processed_at TIMESTAMP NULL" +
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS payments (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  booking_id BIGINT NOT NULL UNIQUE," +
                    "  amount DECIMAL(10,2) NOT NULL," +
                    "  status VARCHAR(10) NOT NULL," +
                    "  transaction_id VARCHAR(100) UNIQUE," +
                    "  payment_method VARCHAR(50)," +
                    "  payment_date TIMESTAMP NULL," +
                    "  verified_at TIMESTAMP NULL," +
                    "  remarks VARCHAR(500)," +
                    "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" + // FIXED
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS complaints (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  student_id BIGINT NOT NULL," +
                    "  booking_id BIGINT," +
                    "  subject VARCHAR(200) NOT NULL," +
                    "  description VARCHAR(2000) NOT NULL," +
                    "  status VARCHAR(15) NOT NULL," +
                    "  admin_response VARCHAR(2000)," +
                    "  resolved_by BIGINT," +
                    "  resolved_at TIMESTAMP NULL," +
                    "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + // FIXED
                    "  updated_at TIMESTAMP NULL" +
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS maintenance_requests (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  room_id BIGINT NOT NULL," +
                    "  reported_by BIGINT," +
                    "  issue_description VARCHAR(1000) NOT NULL," +
                    "  category VARCHAR(50)," +
                    "  status VARCHAR(15) NOT NULL," +
                    "  staff_notes VARCHAR(1000)," +
                    "  assigned_to BIGINT," +
                    "  completed_at TIMESTAMP NULL," +
                    "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + // FIXED
                    "  updated_at TIMESTAMP NULL" +
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS check_in_out (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  booking_id BIGINT NOT NULL UNIQUE," +
                    "  check_in_date TIMESTAMP NULL," +
                    "  check_out_date TIMESTAMP NULL," +
                    "  check_in_by BIGINT," +
                    "  check_out_by BIGINT," +
                    "  check_in_notes VARCHAR(500)," +
                    "  check_out_notes VARCHAR(500)," +
                    "  room_condition_on_checkin VARCHAR(200)," +
                    "  room_condition_on_checkout VARCHAR(200)" +
                    ")");

                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS notifications (" +
                    "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "  user_id BIGINT NOT NULL," +
                    "  title VARCHAR(200) NOT NULL," +
                    "  message VARCHAR(2000) NOT NULL," +
                    "  type VARCHAR(10) NOT NULL," +
                    "  is_read BOOLEAN NOT NULL DEFAULT FALSE," +
                    "  sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + // FIXED
                    "  read_at TIMESTAMP NULL" +
                    ")");

                LOG.info("MySQL schema created successfully via JDBC.");
            }
        } catch (Exception e) {
            LOG.severe("Schema creation failed: " + e.getMessage());
            throw new RuntimeException("Could not create MySQL database schema", e);
        }
    }
}