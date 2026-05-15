package com.defender.config;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printConnectionInfo();

        String email = "";
        String role = null;

        while (role == null) {
            System.out.println("--- WELCOME TO DEFENDER SYSTEM ---");
            System.out.print("Enter your SLU Email: ");
            email = scanner.nextLine().trim().toLowerCase();

            if (email.isEmpty()) {
                System.out.println("[!] Please enter your email.");
                continue;
            }

            String loginResult = lookupRole(email);
            if (loginResult == null) {
                System.out.println("[!] Access Denied: Email not registered. Please try again.");
            } else if (loginResult.startsWith("DB_ERROR:")) {
                System.out.println("[!] " + loginResult.substring("DB_ERROR:".length()));
            } else {
                role = loginResult;
            }
        }

        System.out.println("\nLogin Successful!  Welcome, " + role + "!");

        // MAIN MENU
        while (true) {
            System.out.println("\n--- DEFENDER THESIS SYSTEM ---");
            System.out.println("User: " + email);
            System.out.println("Role: " + role);
            System.out.println("-".repeat(30));

            int optionCount = 1;

            // Shared Options
            System.out.println(optionCount++ + ". View All Advisers");
            System.out.println(optionCount++ + ". View Team Composition");
            System.out.println(optionCount++ + ". View Official Schedule Report");

            // Role-Specific Options
            if (role.equals("HEAD")) {
                System.out.println(optionCount++ + ". Register New Team & 8 Members");
                System.out.println(optionCount++ + ". Assign Team to Defense Period");
                System.out.println(optionCount++ + ". Assign Adviser & Panelists");
                System.out.println(optionCount++ + ". Add New Defense Period");
                System.out.println(optionCount++ + ". Unschedule a Team");
            }

            if (role.equals("ADVISER")) {
                System.out.println(optionCount++ + ". Update Defense Result");
            }

            if (role.equals("STUDENT")) {
                System.out.println(optionCount++ + ". Choose Defense Room");
            }

            System.out.println("0. Exit");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) return;

            handleMenuChoice(choice, role, email, scanner);
        }
    }

    private static void handleMenuChoice(int choice, String role, String email, Scanner scanner) {
        if (choice == 1) { showAdvisers(); return; }
        if (choice == 2) {
            showAllTeams();
            System.out.print("\nEnter Team ID to view details: ");
            int tId = scanner.nextInt();
            showTeamMembers(tId);
            return;
        }
        if (choice == 3) { showFullSchedule(); return; }

        if (role.equals("HEAD")) {
            switch (choice) {
                case 4: registerTeamFlow(scanner); break;
                case 5:
                    assignTeamToPeriodFlow(scanner);
                    break;
                case 6: assignStaffFlow(scanner); break;
                case 7:
                    System.out.print("Start Date (YYYY-MM-DD): ");
                    String s = scanner.nextLine();
                    System.out.print("End Date (YYYY-MM-DD): ");
                    addNewDefensePeriod(s, scanner.nextLine());
                    break;
                case 8:
                    showAllTeams();
                    System.out.print("Enter Team ID to clear: ");
                    unscheduleTeam(scanner.nextInt());
                    break;
            }
        }
        else if (role.equals("ADVISER") && choice == 4) {
            updateResultFlow(scanner);
        }
        else if (role.equals("STUDENT") && choice == 4) {
            studentRoomFlow(email, scanner);
        }
    }

    private static boolean isMentorAvailable(int teamId, int periodId) {
        String sql =
                "SELECT u.firstName, u.lastName " +
                        "FROM user u " +
                        "JOIN availability a ON u.user_ID = a.user_ID " +
                        "JOIN defense_period dp ON dp.period_ID = ? " +
                        "WHERE u.user_ID IN (" +
                        "    SELECT adviser_user_ID FROM thesis_team WHERE team_ID = ? " +
                        "    UNION " +
                        "    SELECT panel_ID FROM panel_assignment WHERE team_ID = ?" +
                        ") " +
                        "AND (a.days LIKE CONCAT('%', DAYNAME(dp.start_date), '%'))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, periodId);
            ps.setInt(2, teamId);
            ps.setInt(3, teamId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.printf("[CONFLICT] %s %s is busy during this period.%n",
                        rs.getString(1), rs.getString(2));
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return true; // No conflicts found
    }

    //FLOW HELPERS

    private static void studentRoomFlow(String email, Scanner scanner) {
        int myTeamId = getTeamIdByEmail(email);
        int periodId = getAssignedPeriod(myTeamId);

        if (periodId == -1) {
            System.out.println("[!] No defense period assigned by the Head yet.");
            return;
        }

        if (!isMentorAvailable(myTeamId, periodId)) {
            return;
        }

        showAvailableRoomsForPeriod(myTeamId, periodId);

        System.out.print("\nEnter Room ID choice: ");
        int chosenRoomId = scanner.nextInt();
        updateRoomForDefense(myTeamId, chosenRoomId);
    }

    private static void updateResultFlow(Scanner scanner) {
        showAllTeams();
        System.out.print("Enter Team ID to update: ");
        int teamId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Result (PASSED/REDEFENSE/FAILED): ");
        updateDefenseStatusByTeam(teamId, scanner.nextLine().toUpperCase());
    }

    private static void assignTeamToPeriodFlow(Scanner scanner) {
        showAllTeams();
        System.out.print("Enter Team ID: ");
        int teamId = scanner.nextInt();
        showDefensePeriods();
        System.out.print("Enter Period ID: ");
        int periodId = scanner.nextInt();
        saveInitialSchedule(teamId, periodId);
    }

    private static void assignStaffFlow(Scanner scanner) {
        showAllTeams();
        System.out.print("Enter Team ID: ");
        int teamId = scanner.nextInt();

        showAdvisers();
        System.out.print("Enter Adviser ID: ");
        assignAdviserToTeam(teamId, scanner.nextInt());

        showAvailablePanelists();
        System.out.println("Enter Panelist IDs (0 to stop): ");
        while (true) {
            int pId = scanner.nextInt();
            if (pId == 0) break;
            assignPanelToTeam(teamId, pId);
        }
    }

    private static void registerTeamFlow(Scanner scanner) {
        System.out.println("\n--- TEAM REGISTRATION ---");
        System.out.print("Team Name: ");
        String name = scanner.nextLine();
        System.out.print("Class Code: ");
        String code = scanner.nextLine();
        System.out.print("Remarks: ");
        String remarks = scanner.nextLine();

        String[] emails = new String[8];
        for (int i = 0; i < 8; i++) {
            System.out.print("Member " + (i + 1) + " Email: ");
            emails[i] = scanner.nextLine();
        }

        String sql = "{call sp_RegisterFullTeam(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, name);
            cstmt.setString(2, code);
            cstmt.setString(3, remarks);
            for (int i = 0; i < 8; i++) {
                cstmt.setString(i + 4, emails[i]);
            }

            cstmt.execute();
            System.out.println("[SUCCESS] Procedure executed. Team and 8 members linked!");

        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }

    //DATABASE METHODS

    private static void printConnectionInfo() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String dbName = "";
            try (ResultSet db = stmt.executeQuery("SELECT DATABASE()")) {
                if (db.next()) {
                    dbName = db.getString(1);
                }
            }
            int userCount = 0;
            try (ResultSet count = stmt.executeQuery("SELECT COUNT(*) FROM user")) {
                if (count.next()) {
                    userCount = count.getInt(1);
                }
            }
            System.out.println("[i] Connected to database: " + dbName + " (" + userCount + " users in user table)");
            if (!"newnewdb".equalsIgnoreCase(dbName)) {
                System.out.println("[!] Wrong database. Expected 'newnewdb'. Use Build -> Rebuild Project in IntelliJ.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Cannot connect to MySQL: " + e.getMessage());
            System.out.println("    Start WAMP, then run the app again (MySQL must be on port 3309).");
        }
        System.out.println();
    }

    /**
     * @return role (HEAD, ADVISER, etc.), null if email not in database,
     *         or "DB_ERROR:..." if the database could not be reached
     */
    private static String lookupRole(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT 'HEAD' as role FROM user u JOIN head h ON u.user_ID = h.user_ID WHERE TRIM(u.email) = ? " +
                    "UNION SELECT 'ADVISER' FROM user u JOIN adviser a ON u.user_ID = a.user_ID WHERE TRIM(u.email) = ? " +
                    "UNION SELECT 'STUDENT' FROM user u JOIN student s ON u.user_ID = s.user_ID WHERE TRIM(u.email) = ? " +
                    "UNION SELECT 'PANEL' FROM user u JOIN panel_member p ON u.user_ID = p.user_ID WHERE TRIM(u.email) = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, email);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        } catch (SQLException e) {
            return "DB_ERROR:Database connection failed: " + e.getMessage()
                    + "\n    Make sure WAMP MySQL is running on port 3309 and newnewdb exists.";
        }
    }

    private static void showAdvisers() {
        String sql = "SELECT u.user_ID, u.firstName, u.lastName, u.email FROM user u JOIN adviser a ON u.user_ID = a.user_ID";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- LIST OF ADVISERS ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Name: %s %s | Email: %s%n", rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void showAllTeams() {
        String sql = "SELECT team_ID, team_name, class_code, status FROM thesis_team";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- TEAMS ---");
            System.out.printf("%-5s | %-20s | %-10s | %-10s%n", "ID", "NAME", "CLASS", "STATUS");
            while (rs.next()) {
                System.out.printf("%-5d | %-20s | %-10s | %-10s%n", rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void showTeamMembers(int teamId) {
        String sql = "SELECT u.firstName, u.lastName, u.email FROM user u JOIN student s ON u.user_ID = s.user_ID WHERE s.team_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            System.out.println("--- MEMBERS ---");
            while (rs.next()) {
                System.out.printf("%-15s %-15s | %s%n", rs.getString(1), rs.getString(2), rs.getString(3));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void showDefensePeriods() {
        String sql =
                "SELECT p.period_ID, p.start_date, p.end_date, t.team_name " +
                        "FROM defense_period p " +
                        "LEFT JOIN defense_schedule ds ON ds.period_ID = p.period_ID " +
                        "LEFT JOIN thesis_team t ON t.team_ID = ds.team_ID " +
                        "ORDER BY p.period_ID";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- DEFENSE PERIODS ---");
            while (rs.next()) {
                String team = rs.getString(4);
                String status = team != null ? "TAKEN by " + team : "AVAILABLE";
                System.out.printf("ID: %d | %s to %s | %s%n",
                        rs.getInt(1), rs.getString(2), rs.getString(3), status);
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }

    private static void saveInitialSchedule(int teamId, int periodId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!teamExists(conn, teamId)) {
                System.out.println("[!] Team ID " + teamId + " does not exist.");
                return;
            }
            if (!periodExists(conn, periodId)) {
                System.out.println("[!] Period ID " + periodId + " does not exist.");
                return;
            }

            int existingPeriod = getAssignedPeriod(conn, teamId);
            if (existingPeriod != -1) {
                if (existingPeriod == periodId) {
                    System.out.println("[!] This team is already scheduled for period " + periodId + ".");
                } else {
                    System.out.println("[!] Schedule taken: team is already assigned to period "
                            + existingPeriod + ". Unschedule the team first (option 8).");
                }
                return;
            }

            String conflict = findScheduleConflict(conn, teamId, periodId);
            if (conflict != null) {
                System.out.println("[!] Schedule taken: " + conflict);
                return;
            }

            String sql = "INSERT INTO defense_schedule (team_ID, period_ID, room_ID) VALUES (?, ?, NULL)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, teamId);
                ps.setInt(2, periodId);
                ps.executeUpdate();
                System.out.println("[SUCCESS] Team assigned to period. Students can now book a room.");
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }

    private static boolean teamExists(Connection conn, int teamId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM thesis_team WHERE team_ID = ?")) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean periodExists(Connection conn, int periodId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM defense_period WHERE period_ID = ?")) {
            ps.setInt(1, periodId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static int getAssignedPeriod(Connection conn, int teamId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT period_ID FROM defense_schedule WHERE team_ID = ? LIMIT 1")) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /** @return conflict description, or null if the period slot is free for this team */
    private static String findScheduleConflict(Connection conn, int teamId, int periodId) throws SQLException {
        String periodTakenSql =
                "SELECT t.team_name, p.start_date, p.end_date " +
                        "FROM defense_schedule ds " +
                        "JOIN thesis_team t ON ds.team_ID = t.team_ID " +
                        "JOIN defense_period p ON p.period_ID = ds.period_ID " +
                        "WHERE ds.period_ID = ? AND ds.team_ID != ? " +
                        "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(periodTakenSql)) {
            ps.setInt(1, periodId);
            ps.setInt(2, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format("period %s to %s is already assigned to team \"%s\".",
                            rs.getString(2), rs.getString(3), rs.getString(1));
                }
            }
        }

        String adviserSql =
                "SELECT t.team_name, u.firstName, u.lastName " +
                        "FROM defense_schedule ds " +
                        "JOIN thesis_team t ON ds.team_ID = t.team_ID " +
                        "JOIN thesis_team my ON my.team_ID = ? " +
                        "JOIN user u ON u.user_ID = my.adviser_user_ID " +
                        "WHERE ds.period_ID = ? AND ds.team_ID != ? " +
                        "AND my.adviser_user_ID IS NOT NULL AND t.adviser_user_ID = my.adviser_user_ID " +
                        "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(adviserSql)) {
            ps.setInt(1, teamId);
            ps.setInt(2, periodId);
            ps.setInt(3, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format("adviser %s %s is already defending team \"%s\" in this period.",
                            rs.getString(2), rs.getString(3), rs.getString(1));
                }
            }
        }

        String panelSql =
                "SELECT t.team_name, u.firstName, u.lastName " +
                        "FROM defense_schedule ds " +
                        "JOIN thesis_team t ON ds.team_ID = t.team_ID " +
                        "JOIN panel_assignment my_pa ON my_pa.team_ID = ? " +
                        "JOIN panel_assignment their_pa ON their_pa.team_ID = t.team_ID " +
                        "AND their_pa.panel_ID = my_pa.panel_ID " +
                        "JOIN user u ON u.user_ID = my_pa.panel_ID " +
                        "WHERE ds.period_ID = ? AND ds.team_ID != ? " +
                        "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(panelSql)) {
            ps.setInt(1, teamId);
            ps.setInt(2, periodId);
            ps.setInt(3, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format("panelist %s %s is already assigned to team \"%s\" in this period.",
                            rs.getString(2), rs.getString(3), rs.getString(1));
                }
            }
        }

        return null;
    }

    private static void showFullSchedule() {
        String sql =
                "SELECT t.team_name, r.room_code, p.start_date, p.end_date, " +
                        "adv_user.lastName as adviser, " +
                        "GROUP_CONCAT(CONCAT(pan_user.firstName, ' ', pan_user.lastName) SEPARATOR ', ') as panelists, " +
                        "t.status " +
                        "FROM defense_schedule ds " +
                        "JOIN thesis_team t ON ds.team_ID = t.team_ID " +
                        "LEFT JOIN room r ON ds.room_ID = r.room_ID " +
                        "JOIN defense_period p ON ds.period_ID = p.period_ID " +
                        "LEFT JOIN user adv_user ON t.adviser_user_ID = adv_user.user_ID " +
                        "LEFT JOIN panel_assignment pa ON t.team_ID = pa.team_ID " +
                        "LEFT JOIN user pan_user ON pa.panel_ID = pan_user.user_ID " +
                        "GROUP BY ds.schedule_ID, t.team_name, r.room_code, p.start_date, p.end_date, adv_user.lastName, t.status";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n" + "=".repeat(150));
            System.out.printf("%-15s | %-10s | %-8s | %-25s | %-12s | %-30s%n",
                    "TEAM", "STATUS", "ROOM", "PERIOD", "ADVISER", "PANELISTS");
            System.out.println("-".repeat(150));

            while (rs.next()) {
                String panelists = rs.getString("panelists");
                if (panelists == null) panelists = "TBA";

                System.out.printf("%-15s | %-10s | %-8s | %s to %-12s | %-12s | %-30s%n",
                        rs.getString("team_name"),
                        rs.getString("status"), // New column added here
                        rs.getString("room_code") != null ? rs.getString("room_code") : "TBA",
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("adviser") != null ? rs.getString("adviser") : "TBA",
                        panelists);
            }
            System.out.println("=".repeat(150));
        } catch (SQLException e) {
            System.out.println("[!] Error generating report: " + e.getMessage());
        }
    }

    private static void updateDefenseStatusByTeam(int teamId, String result) {
        String sql = "UPDATE thesis_team SET status = ? WHERE team_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, result);
            ps.setInt(2, teamId);
            if (ps.executeUpdate() > 0) System.out.println("[SUCCESS] Status updated.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void assignAdviserToTeam(int teamId, int adviserId) {
        String sql = "UPDATE thesis_team SET adviser_user_ID = ? WHERE team_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adviserId);
            ps.setInt(2, teamId);
            ps.executeUpdate();
            System.out.println("[SUCCESS] Adviser linked.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void showAvailablePanelists() {
        String sql = "SELECT u.user_ID, u.firstName, u.lastName FROM user u JOIN panel_member p ON u.user_ID = p.user_ID";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) System.out.printf("ID: %d | Name: %s %s%n", rs.getInt(1), rs.getString(2), rs.getString(3));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void assignPanelToTeam(int teamId, int panelId) {
        String sql = "INSERT IGNORE INTO panel_assignment (panel_ID, team_ID) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, panelId);
            ps.setInt(2, teamId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static int createTeam(String name, String code, String rem) {
        String sql = "INSERT INTO thesis_team (team_name, class_code, remarks, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, code);
            ps.setString(3, rem);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    private static boolean linkStudentToTeamByEmail(int teamId, String email) {
        String sql = "UPDATE student SET team_id = ? WHERE user_ID = (SELECT user_ID FROM user WHERE email = ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static int getTeamIdByEmail(String email) {
        String sql = "SELECT team_id FROM student s JOIN user u ON s.user_ID = u.user_ID WHERE u.email = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { return -1; }
    }

    private static int getAssignedPeriod(int teamId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return getAssignedPeriod(conn, teamId);
        } catch (SQLException e) {
            return -1;
        }
    }

    private static boolean showAvailableRoomsForPeriod(int myTeamId, int periodId) {
        String sql =
                "SELECT * FROM room " +
                        "WHERE room_ID NOT IN (" +
                        "    SELECT room_ID FROM defense_schedule WHERE period_ID = ? AND room_ID IS NOT NULL" +
                        ") " +
                        "AND NOT EXISTS (" +
                        "    SELECT 1 FROM defense_schedule ds " +
                        "    JOIN thesis_team tt ON ds.team_ID = tt.team_ID " +
                        "    LEFT JOIN panel_assignment pa ON tt.team_ID = pa.team_ID " +
                        "    WHERE ds.period_ID = ? " +
                        "    AND ds.team_ID != ? " + // <--- ADD THIS LINE HERE
                        "    AND (tt.adviser_user_ID IN (SELECT adviser_user_ID FROM thesis_team WHERE team_ID = ?) " +
                        "    OR pa.panel_ID IN (SELECT panel_ID FROM panel_assignment WHERE team_ID = ?))" +
                        ")";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, periodId);
            ps.setInt(2, periodId);
            ps.setInt(3, myTeamId);
            ps.setInt(4, myTeamId);
            ps.setInt(5, myTeamId);

            ResultSet rs = ps.executeQuery();
            boolean found = false;

            System.out.println("\n--- AVAILABLE ROOMS FOR YOUR TEAM ---");
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | %s (%s)%n",
                        rs.getInt("room_ID"), rs.getString("room_code"), rs.getString("building"));
            }

            if (!found) {
                System.out.println("[!] NO ROOMS AVAILABLE...");
            }
            return found;

        } catch (SQLException e) {
            System.out.println("[!] Error: " + e.getMessage());
            return false;
        }
    }

    private static void updateRoomForDefense(int teamId, int roomId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int periodId = getAssignedPeriod(conn, teamId);
            if (periodId == -1) {
                System.out.println("[!] This team has no defense period assigned yet.");
                return;
            }

            if (isRoomTakenInPeriod(conn, periodId, roomId, teamId)) {
                System.out.println("[!] Schedule taken: that room is already booked for this period.");
                return;
            }

            String sql = "UPDATE defense_schedule SET room_ID = ? WHERE team_ID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, roomId);
                ps.setInt(2, teamId);
                if (ps.executeUpdate() > 0) {
                    System.out.println("[SUCCESS] Room booked.");
                } else {
                    System.out.println("[!] Could not update schedule for this team.");
                }
            }
        } catch (SQLException e) {
            System.out.println("[!] DB Error: " + e.getMessage());
        }
    }

    private static boolean isRoomTakenInPeriod(Connection conn, int periodId, int roomId, int teamId)
            throws SQLException {
        String sql = "SELECT 1 FROM defense_schedule WHERE period_ID = ? AND room_ID = ? AND team_ID != ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, periodId);
            ps.setInt(2, roomId);
            ps.setInt(3, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void addNewDefensePeriod(String start, String end) {
        String sql = "INSERT INTO defense_period (start_date, end_date) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start);
            ps.setString(2, end);
            ps.executeUpdate();
            System.out.println("[SUCCESS] Period added.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void unscheduleTeam(int teamId) {
        String sql = "DELETE FROM defense_schedule WHERE team_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.executeUpdate();
            System.out.println("[SUCCESS] Team unscheduled.");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}