import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Klasa zarządzająca zapisem i odczytem danych do/z plików JSON.
 */
public class Json {
    private static final String TICKETS_FILE = "tickets.json";
    private static final String TECHNICIANS_FILE = "technicians.json";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveTickets(List<Ticket> tickets) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TICKETS_FILE))) {
            writer.println("[");
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                writer.println("  {");
                writer.println("    \"ticketId\": \"" + ticket.getTicketId() + "\",");
                writer.println("    \"title\": \"" + escapeJson(ticket.getTitle()) + "\",");
                writer.println("    \"description\": \"" + escapeJson(ticket.getDescription()) + "\",");
                writer.println("    \"reporterName\": \"" + escapeJson(ticket.getReporterName()) + "\",");
                writer.println("    \"reporterEmail\": \"" + ticket.getReporterEmail() + "\",");
                writer.println("    \"priority\": \"" + ticket.getPriority() + "\",");
                writer.println("    \"status\": \"" + ticket.getStatus() + "\",");
                writer.println("    \"assignedTechnicianName\": " +
                        (ticket.getAssignedTechnicianName() != null ?
                                "\"" + escapeJson(ticket.getAssignedTechnicianName()) + "\"" : "null") + ",");
                writer.println("    \"createdAt\": \"" + ticket.getCreatedAt().format(DATE_FORMATTER) + "\",");
                writer.println("    \"lastUpdated\": \"" + ticket.getLastUpdated().format(DATE_FORMATTER) + "\",");

                writer.print("  }");
                if (i < tickets.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");
//            System.out.println("Zgłoszenia zostały zapisane do pliku: " + TICKETS_FILE);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania zgłoszeń: " + e.getMessage());
        }
    }

    /**
     * Wczytuje zgłoszenia z pliku JSON.
     *
     * @return lista wczytanych zgłoszeń
     */
    public List<Ticket> loadTickets() {
        
        List<Ticket> tickets = new ArrayList<>();
        File file = new File(TICKETS_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }

            String content = jsonContent.toString();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1); // Usuń nawiasy []

                if (!content.trim().isEmpty()) {
                    String[] ticketObjects = splitJsonObjects(content);

                    for (String ticketJson : ticketObjects) {
                        try {
                            Ticket ticket = parseTicketFromJson(ticketJson);
                            if (ticket != null) {
                                tickets.add(ticket);
                            }
                        } catch (Exception e) {
                            System.err.println("Błąd podczas parsowania zgłoszenia: " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("Wczytano zgłoszenia z pliku: " + TICKETS_FILE);
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania zgłoszeń: " + e.getMessage());
        }

        return tickets;
    }

    /**
     * Zapisuje techników do pliku JSON.
     *
     * @param technicians lista techników do zapisania
     */
    public void saveTechnicians(List<String> technicians) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TECHNICIANS_FILE))) {
            writer.println("[");
            for (int i = 0; i < technicians.size(); i++) {
                writer.print("  \"" + escapeJson(technicians.get(i)) + "\"");
                if (i < technicians.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");
//            System.out.println("Technicy zostali zapisani do pliku: " + TECHNICIANS_FILE);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania techników: " + e.getMessage());
        }
    }

    /**
     * Wczytuje techników z pliku JSON.
     *
     * @return lista wczytanych techników
     */
    public List<String> loadTechnicians() {
        List<String> technicians = new ArrayList<>();
        File file = new File(TECHNICIANS_FILE);

        if (!file.exists()) {
            System.out.println("Plik " + TECHNICIANS_FILE + " nie istnieje. Używamy domyślnej listy techników.");
            return Arrays.asList("Jan Kowalski", "Anna Nowak", "Piotr Wiśniewski");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }

            String content = jsonContent.toString();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1); // Usuń nawiasy []

                if (!content.trim().isEmpty()) {
                    String[] technicianNames = content.split(",");
                    for (String name : technicianNames) {
                        name = name.trim();
                        if (name.startsWith("\"") && name.endsWith("\"")) {
                            name = name.substring(1, name.length() - 1); // Usuń cudzysłowy
                            technicians.add(unescapeJson(name));
                        }
                    }
                }
            }

            System.out.println("Wczytano techników z pliku: " + TECHNICIANS_FILE);
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania techników: " + e.getMessage());
            return Arrays.asList("Jan Kowalski", "Anna Nowak", "Piotr Wiśniewski");
        }

        return technicians;
    }


    // Metody pomocnicze

    private String escapeJson(String str) {
        if (str == null) return null;
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String str) {
        if (str == null) return null;
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private String[] splitJsonObjects(String content) {
        List<String> objects = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int braceCount = 0;
        boolean inString = false;
        boolean escaped = false;

        for (char c : content.toCharArray()) {
            if (escaped) {
                escaped = false;
                current.append(c);
                continue;
            }

            if (c == '\\' && inString) {
                escaped = true;
                current.append(c);
                continue;
            }

            if (c == '"') {
                inString = !inString;
            }

            if (!inString) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                }
            }

            current.append(c);

            if (!inString && braceCount == 0 && c == '}') {
                objects.add(current.toString().trim());
                current = new StringBuilder();

            }
        }

        return objects.toArray(new String[0]);
    }

    private Ticket parseTicketFromJson(String jsonString) {
        try {
            Map<String, String> values = new HashMap<>();

            // Usuń nawiasy klamrowe
            String content = jsonString.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                content = content.substring(1, content.length() - 1);
            }

            // Parsuj pary klucz-wartość
            String[] pairs = splitJsonPairs(content);
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim();

                    if (value.equals("null")) {
                        values.put(key, null);
                    } else {
                        value = value.replaceAll("^\"|\"$", ""); // Usuń cudzysłowy na początku i końcu
                        values.put(key, unescapeJson(value));
                    }
                }
            }

            // Utwórz zgłoszenie
            String ticketId = values.get("ticketId");
            String title = values.get("title");
            String description = values.get("description");
            String reporterName = values.get("reporterName");
            String reporterEmail = values.get("reporterEmail");
            String priorityStr = values.get("priority");
            Ticket.Priority priority;

// Konwersja dla kompatybilności
            switch (priorityStr) {
                case "Niski" -> priority = Ticket.Priority.NISKI;
                case "Średni" -> priority = Ticket.Priority.SREDNI;
                case "Wysoki" -> priority = Ticket.Priority.WYSOKI;
                case "Krytyczny" -> priority = Ticket.Priority.KRYTYCZNY;
                default -> priority = Ticket.Priority.valueOf(priorityStr); // Próbuj normalnie
            }

            Ticket ticket = new Ticket(ticketId, title, description, reporterName, reporterEmail, priority);

            // Ustaw dodatkowe pola przez refleksję (uproszczona wersja)
            if (values.get("assignedTechnicianName") != null) {
                ticket.assignToTechnician(values.get("assignedTechnicianName"));
            }

            return ticket;
        } catch (Exception e) {
//            System.err.println("Błąd podczas parsowania zgłoszenia: " + e.getMessage());
            return null;
        }
    }

    private String[] splitJsonPairs(String content) {
        List<String> pairs = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;

        for (char c : content.toCharArray()) {
            if (escaped) {
                escaped = false;
                current.append(c);
                continue;
            }

            if (c == '\\' && inString) {
                escaped = true;
                current.append(c);
                continue;
            }

            if (c == '"') {
                inString = !inString;
            }

            if (!inString && c == ',') {
                pairs.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            pairs.add(current.toString().trim());
        }

        return pairs.toArray(new String[0]);
    }
}