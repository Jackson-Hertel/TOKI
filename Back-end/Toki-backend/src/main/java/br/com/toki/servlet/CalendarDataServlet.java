package br.com.toki.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

@WebServlet("/calendar-data")
public class CalendarDataServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:h2:~/toki-db";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ArrayList<HashMap<String, Object>> events = new ArrayList<>();
        ArrayList<HashMap<String, Object>> tasks = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // Eventos
            String sqlEvents = "SELECT id, date, time, title, description, color, priority FROM events ORDER BY date ASC, time ASC";
            try (PreparedStatement ps = conn.prepareStatement(sqlEvents);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    HashMap<String, Object> event = new HashMap<>();
                    event.put("id", rs.getInt("id"));
                    event.put("date", rs.getString("date")); // YYYY-MM-DD
                    event.put("time", rs.getString("time")); // HH:MM
                    event.put("title", rs.getString("title"));
                    event.put("description", rs.getString("description"));
                    event.put("color", rs.getString("color")); // "blue", "purple", etc
                    event.put("priority", rs.getInt("priority"));
                    events.add(event);
                }
            }

            // Tarefas
            String sqlTasks = "SELECT id, title, done FROM tasks ORDER BY id ASC";
            try (PreparedStatement ps = conn.prepareStatement(sqlTasks);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    HashMap<String, Object> task = new HashMap<>();
                    task.put("id", rs.getInt("id"));
                    task.put("title", rs.getString("title"));
                    task.put("done", rs.getBoolean("done"));
                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Monta JSON final
        HashMap<String, Object> response = new HashMap<>();
        response.put("events", events);
        response.put("tasks", tasks);

        Gson gson = new Gson();
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(response));
        out.flush();
    }
}
