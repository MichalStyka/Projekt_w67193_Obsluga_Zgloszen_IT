import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Klasa zarządzająca zapisem i odczytem danych plików JSON.
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
                writer.println("    \"title\": \"" + escapejSON(ticket.getTitle()) + "\",");
                writer.println("    \"description\": \"" + escapejSON(ticket.getDescription()) + "\",");
                writer.println("    \"reporterName\": \"" + escapejSON(ticket.getReporterName()) + "\",");
                writer.println("    \"reporterEmail\": \"" + ticket.getReporterEmail() + "\",");
                writer.println("    \"priority\": \"" + ticket.getPriority() + "\",");
                writer.println("    \"status\": \"" + ticket.getStatus() + "\",");
                writer.println("    \"assignedTechnicianName\": " +
                        (ticket.getAssignedTechnicianName() != null ?
                                "\"" + escapejSON(ticket.getAssignedTechnicianName()) + "\"" : "null") + ",");
                writer.println("    \"createdAt\": \"" + ticket.getCreatedAt().format(DATE_FORMATTER) + "\",");
                

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

        if (!file.exists()) {
            System.out.println("Plik " + TICKETS_FILE + " nie istnieje.");
            return tickets;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Map<String, String> currentTicket = new HashMap<>();
            boolean inTicketObject = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("{")) {
                    inTicketObject = true;
                    currentTicket.clear();
                } else if (line.equals("}") || line.equals("},")) {
                    if (inTicketObject && !currentTicket.isEmpty()) {
                        Ticket ticket = createTicketFromMap(currentTicket);
                        if (ticket != null) {
                            tickets.add(ticket);
                        }
                    }
                    inTicketObject = false;
                } else if (inTicketObject && line.contains(":")) {
                    parseJsonLine(line, currentTicket);
                }
            }

            System.out.println("Wczytano " + tickets.size() + " zgłoszeń z pliku: " + TICKETS_FILE);
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
                writer.print("  \"" + escapejSON(technicians.get(i)) + "\"");
                if (i < technicians.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");
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
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Szukaj linii zawierających nazwy techników w cudzysłowach
                if (line.startsWith("\"") && (line.endsWith("\"") || line.endsWith("\","))) {
                    String name = line.substring(1); // Usuń pierwszy cudzysłów
                    if (name.endsWith(",")) {
                        name = name.substring(0, name.length() - 1); // Usuń przecinek
                    }
                    if (name.endsWith("\"")) {
                        name = name.substring(0, name.length() - 1); // Usuń ostatni cudzysłów
                    }
                    technicians.add(unescapejSON(name));
                }
            }

            System.out.println("Wczytano " + technicians.size() + " techników z pliku: " + TECHNICIANS_FILE);
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania techników: " + e.getMessage());
            return Arrays.asList("Jan Kowalski", "Anna Nowak", "Piotr Wiśniewski");
        }

        return technicians;
    }


    /**
     * escapowanie  JSON.
     */
    private String escapejSON(String str) {
        if (str == null) return null;
        return str.replace("\"", "\\\"")
                .replace("\n", " ")  // Zamień nowe linie na spacje
                .replace("\r", " ");
    }

    /**
     * unescapowanie JSON.
     */
    private String unescapejSON(String str) {
        if (str == null) return null;
        return str.replace("\\\"", "\"");
    }

    /**
     * Parsuje pojedynczą linię JSON-a w formacie "klucz": "wartość".
     */
    private void parseJsonLine(String line, Map<String, String> data) {
        // Usuń przecinek na końcu jeśli jest
        if (line.endsWith(",")) {
            line = line.substring(0, line.length() - 1);
        }

        int colonIndex = line.indexOf(":");
        if (colonIndex == -1) return;

        String key = line.substring(0, colonIndex).trim();
        String value = line.substring(colonIndex + 1).trim();

        // Usuń cudzysłowy z klucza
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }

        // Obsłuż wartość
        if (value.equals("null")) {
            data.put(key, null);
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            // Usuń cudzysłowy z wartości
            value = value.substring(1, value.length() - 1);
            data.put(key, unescapejSON(value));
        } else {
            data.put(key, value);
        }
    }

    /**
     * Tworzy obiekt Ticket z mapy danych.
     */
    private Ticket createTicketFromMap(Map<String, String> data) {
        try {
            String ticketId = data.get("ticketId");
            String title = data.get("title");
            String description = data.get("description");
            String reporterName = data.get("reporterName");
            String reporterEmail = data.get("reporterEmail");
            String priorityStr = data.get("priority");

            // Konwertuj priorytet
            Ticket.Priority priority = convertPriority(priorityStr);

            // Utwórz zgłoszenie
            Ticket ticket = new Ticket(ticketId, title, description, reporterName, reporterEmail, priority);

            // Przypisz technika jeśli jest
            String assignedTechnician = data.get("assignedTechnicianName");
            if (assignedTechnician != null && !assignedTechnician.isEmpty()) {
                ticket.assignToTechnician(assignedTechnician);
            }

            return ticket;
        } catch (Exception e) {
            System.err.println("Błąd podczas tworzenia zgłoszenia: " + e.getMessage());
            return null;
        }
    }

    /**
     * Konwertuje string na Priority enum.
     */
    private Ticket.Priority convertPriority(String priorityStr) {
        return switch (priorityStr) {
            case "Niski" -> Ticket.Priority.NISKI;
            case "Średni" -> Ticket.Priority.SREDNI;
            case "Wysoki" -> Ticket.Priority.WYSOKI;
            case "Krytyczny" -> Ticket.Priority.KRYTYCZNY;
            default -> Ticket.Priority.SREDNI; //domyslnie sredni priority

        };
    }
}