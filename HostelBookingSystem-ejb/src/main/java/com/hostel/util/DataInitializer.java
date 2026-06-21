package com.hostel.util;

import com.hostel.dao.RegistrationPeriodDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.dao.UserDAO;
import com.hostel.entity.RegistrationPeriod;
import com.hostel.entity.Room;
import com.hostel.entity.User;
import com.hostel.entity.enums.RoomStatus;
import com.hostel.entity.enums.RoomType;
import com.hostel.entity.enums.UserRole;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

@DataSourceDefinition(
    name            = "java:app/jdbc/hostelDB",
    className       = "org.h2.jdbcx.JdbcDataSource",
    url             = "jdbc:h2:~/hosteldb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    user            = "sa",
    password        = "",
    minPoolSize     = 5,
    maxPoolSize     = 20
)
@Singleton
@Startup
public class DataInitializer {

    private static final Logger LOG = Logger.getLogger(DataInitializer.class.getName());

    @Inject private UserDAO userDAO;
    @Inject private RoomDAO roomDAO;
    @Inject private RegistrationPeriodDAO periodDAO;

    @PostConstruct
    public void init() {
        seedAdminUser();
        seedStaffUser();
        seedStudentUsers();
        seedRooms();
        seedRegistrationPeriod();
        LOG.info("=== Hostel Booking System initialized with seed data ===");
    }

    private void seedAdminUser() {
        if (userDAO.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", PasswordUtil.hash("Admin@123"),
                "System Administrator", "admin@hostel.edu.my", UserRole.ADMIN);
            admin.setPhone("0123456789");
            userDAO.save(admin);
            LOG.info("Admin user created: admin / Admin@123");
        }
    }

    private void seedStaffUser() {
        if (userDAO.findByUsername("staff1").isEmpty()) {
            User staff = new User("staff1", PasswordUtil.hash("Staff@123"),
                "Ahmad bin Hassan", "staff1@hostel.edu.my", UserRole.STAFF);
            staff.setPhone("0123456780");
            userDAO.save(staff);
            LOG.info("Staff user created: staff1 / Staff@123");
        }
    }

    private void seedStudentUsers() {
        if (userDAO.findByUsername("student1").isEmpty()) {
            User s1 = new User("student1", PasswordUtil.hash("Student@123"),
                "Nur Amalina Binti Babah", "amalina@student.edu.my", UserRole.STUDENT);
            s1.setMatricNumber("227670");
            s1.setPhone("0123456781");
            userDAO.save(s1);

            User s2 = new User("student2", PasswordUtil.hash("Student@123"),
                "Nureen Husna Binti Sharil", "nureen@student.edu.my", UserRole.STUDENT);
            s2.setMatricNumber("227750");
            s2.setPhone("0123456782");
            userDAO.save(s2);

            LOG.info("Student users created: student1, student2 / Student@123");
        }
    }

    private void seedRooms() {
        if (roomDAO.count() == 0) {
            String[][] rooms = {
                {"A101", "SINGLE",  "1", "A", "550.00",  "Single room with WiFi"},
                {"A102", "SINGLE",  "1", "A", "550.00",  "Single room with WiFi"},
                {"A201", "DOUBLE",  "2", "A", "450.00",  "Double sharing room"},
                {"A202", "DOUBLE",  "2", "A", "450.00",  "Double sharing room"},
                {"A301", "TRIPLE",  "3", "A", "380.00",  "Triple sharing room"},
                {"A302", "TRIPLE",  "3", "A", "380.00",  "Triple sharing room"},
                {"B101", "SINGLE",  "1", "B", "600.00",  "Single room with AC and WiFi"},
                {"B102", "SINGLE",  "1", "B", "600.00",  "Single room with AC and WiFi"},
                {"B201", "DOUBLE",  "2", "B", "500.00",  "Double sharing with AC"},
                {"B202", "QUAD",    "4", "B", "320.00",  "Quad sharing room"},
            };

            for (String[] r : rooms) {
                Room room = new Room();
                room.setRoomNumber(r[0]);
                room.setType(RoomType.valueOf(r[1]));
                room.setCapacity(Integer.parseInt(r[2]));
                room.setBlock(r[3]);
                room.setPricePerSemester(new BigDecimal(r[4]));
                room.setDescription(r[5]);
                room.setFloor(1);
                room.setStatus(RoomStatus.AVAILABLE);
                room.setHasWifi(true);
                room.setHasAc("B".equals(r[3]));
                roomDAO.save(room);
            }
            LOG.info("10 rooms seeded");
        }
    }

    private void seedRegistrationPeriod() {
        if (periodDAO.count() == 0) {
            RegistrationPeriod period = new RegistrationPeriod();
            period.setName("Semester 2 2025/2026 Registration");
            period.setSemester("Semester 2");
            period.setAcademicYear("2025/2026");
            period.setStartDate(LocalDate.of(2026, 1, 1));
            period.setEndDate(LocalDate.of(2026, 12, 31));
            period.setActive(true);
            periodDAO.save(period);
            LOG.info("Registration period seeded");
        }
    }
}
