import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//import java.util.List;
import java.util.Objects;

public class Ticket {
    public enum Status {
        NOWE("Nowe"),
        W_TRAKCIE("W trakcie"),
        ZAMKNIETE("Zamknięte");

        private final String displayName;
        Status(String displayName) { this.displayName = displayName; }
        @Override
        public String toString() { return displayName; }
    }

    public enum Priority {
        NISKI("Niski"), SREDNI("Średni"), WYSOKI("Wysoki"), KRYTYCZNY("Krytyczny");

        private final String displayName;
        Priority(String displayName) { this.displayName = displayName; }
        @Override
        public String toString() { return displayName; }
    }

    private final String ticketId;
    private final String title;
    private final String description;
    private final String reporterName;
    private final String reporterEmail;
    private final Priority priority;
    private final LocalDateTime createdAt;
    private Status status;
    private String assignedTechnicianName;


    public Ticket(String ticketId, String title, String description,
                  String reporterName, String reporterEmail, Priority priority) {
        if (ticketId == null || !ticketId.matches("TKT-\\d{5}")) {
            throw new IllegalArgumentException("ID zgłoszenia musi mieć format TKT-XXXXX (5 cyfr)");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tytuł zgłoszenia nie może być pusty !");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Opis zgłoszenia nie może być pusty !");
        }
        if (reporterName == null || reporterName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa zgłaszającego nie może być pusta !");
        }
        if (reporterEmail == null || reporterEmail.trim().isEmpty() || !reporterEmail.contains("@")) {
            throw new IllegalArgumentException("Email zgłaszającego musi być prawidłowy !");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priorytet nie może być null !");
        }

        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.reporterName = reporterName;
        this.reporterEmail = reporterEmail;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.status = Status.NOWE;
    }

    public void assignToTechnician(String technicianName) {
        if (status == Status.ZAMKNIETE) {
            throw new IllegalStateException("Nie można przypisać zamkniętego zgłoszenia");
        }
        if (technicianName == null || technicianName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa technika nie może być pusta");
        }
        this.assignedTechnicianName = technicianName;
        this.status = Status.W_TRAKCIE;

    }



    public void close() {

        this.status = Status.ZAMKNIETE;

    }

    public boolean isAssigned() {
        return assignedTechnicianName != null && !assignedTechnicianName.trim().isEmpty();
    }

    public boolean isActive() {
        return status != Status.ZAMKNIETE;
    }

    // Gettery
    public String getTicketId() { return ticketId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getReporterName() { return reporterName; }
    public String getReporterEmail() { return reporterEmail; }
    public Priority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }
    public String getAssignedTechnicianName() { return assignedTechnicianName; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", assignedTo='" + (assignedTechnicianName != null ? assignedTechnicianName : "brak") + '\'' +
                ", created=" + createdAt.format(formatter) +
                '}';
    }
}
