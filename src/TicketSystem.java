import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasa zarządzająca kolekcją zgłoszeń serwisowych.
 */
public class TicketSystem {
    private final Map<String, Ticket> tickets;
    private final Set<String> technicians;
    private final Json dataManager;

    public TicketSystem() {
        this.tickets = new HashMap<>();
        this.technicians = new HashSet<>();
        this.dataManager = new Json();

        // Wczytaj dane z plików JSON
        loadDataFromFiles();

    }

    private void loadDataFromFiles() {
        // Wczytaj techników
        List<String> loadedTechnicians = dataManager.loadTechnicians();
        technicians.addAll(loadedTechnicians);

        // Wczytaj zgłoszenia
        List<Ticket> loadedTickets = dataManager.loadTickets();
        for (Ticket ticket : loadedTickets) {
            tickets.put(ticket.getTicketId(), ticket);
        }
        if (tickets.isEmpty()) {
            System.out.println("Brak zgłoszeń w systemie. Dodawanie przykładowych danych...");

            try {
                // Dodaj przykładowe zgłoszenia
                Ticket ticket1 = new Ticket("TKT-12345", "Nie działa drukarka",
                        "Drukarka w biurze nie reaguje na polecenia drukowania",
                        "Anna Kowalska", "anna.kowalska@firma.pl", Ticket.Priority.SREDNI);

                Ticket ticket2 = new Ticket("TKT-12346", "Błąd serwera email",
                        "Serwer poczty elektronicznej zwraca błędy 500",
                        "Piotr Nowak", "piotr.nowak@firma.pl", Ticket.Priority.WYSOKI);

                tickets.put(ticket1.getTicketId(), ticket1);
                tickets.put(ticket2.getTicketId(), ticket2);

                // Zapisz przykładowe dane do plików
                saveDataToFiles();

                System.out.println("Dodano przykładowe zgłoszenia do systemu.");

            } catch (IllegalArgumentException e) {
                System.err.println("Błąd podczas dodawania przykładowych zgłoszeń: " + e.getMessage());
            }
        }
    }

    /**
     * Zapisuje wszystkie dane do plików JSON.
     */
    public void saveDataToFiles() {
        dataManager.saveTickets(new ArrayList<>(tickets.values()));
        dataManager.saveTechnicians(new ArrayList<>(technicians));
    }


    public void addTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być null");
        }
        if (tickets.containsKey(ticket.getTicketId())) {
            throw new IllegalArgumentException("Zgłoszenie o ID " +
                    ticket.getTicketId() + " już istnieje w systemie");
        }
        tickets.put(ticket.getTicketId(), ticket);
        saveDataToFiles(); // Auto-save po dodaniu
    }

    public void addTechnician(String technicianName) {
        if (technicianName == null || technicianName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa technika nie może być pusta");
        }
        if (technicians.contains(technicianName)) {
            throw new IllegalArgumentException("Technik " + technicianName + " już istnieje w systemie");
        }
        technicians.add(technicianName);
        saveDataToFiles(); // Auto-save po dodaniu
    }

    public Optional<Ticket> findTicketById(String ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public Ticket assignTicket(String ticketId, String technicianName) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Zgłoszenie o ID " + ticketId + " nie istnieje w systemie");
        }
        if (!technicians.contains(technicianName)) {
            throw new IllegalArgumentException("Technik " + technicianName + " nie istnieje w systemie");
        }
        ticket.assignToTechnician(technicianName);
        saveDataToFiles(); // Auto-save po zmianie
        return ticket;
    }


    public void closeTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Zgłoszenie o ID " + ticketId + " nie istnieje w systemie");
        }
        ticket.close();
        saveDataToFiles(); // Auto-save po zmianie
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    public List<Ticket> getActiveTickets() {
        return tickets.values().stream()
                .filter(Ticket::isActive)
                .collect(Collectors.toList());
    }

    public List<String> getAllTechnicians() {
        return new ArrayList<>(technicians);
    }

    public List<Ticket> getTicketsAssignedTo(String technicianName) {
        return tickets.values().stream()
                .filter(ticket -> technicianName.equals(ticket.getAssignedTechnicianName()))
                .collect(Collectors.toList());
    }

    public List<Ticket> getUnassignedTickets() {
        return tickets.values().stream()
                .filter(ticket -> !ticket.isAssigned())
                .collect(Collectors.toList());
    }

    /**
     * Usuwa technika z systemu.
     *
     * @param technicianName nazwa technika do usunięcia
     * @throws IllegalArgumentException jeśli technik nie istnieje lub ma przypisane zgłoszenia
     */
    public void removeTechnician(String technicianName) {
        if (!technicians.contains(technicianName)) {
            throw new IllegalArgumentException("Technik " + technicianName + " nie istnieje w systemie");
        }

        // Sprawdź czy technik ma przypisane aktywne zgłoszenia
        List<Ticket> assignedTickets = getTicketsAssignedTo(technicianName).stream()
                .filter(Ticket::isActive)
                .toList();

        if (!assignedTickets.isEmpty()) {
            throw new IllegalArgumentException("Nie można usunąć technika " + technicianName +
                    " - ma przypisane " + assignedTickets.size() + " aktywnych zgłoszeń");
        }

        technicians.remove(technicianName);
        saveDataToFiles(); // Auto-save po usunięciu
    }

    public int getTotalTicketCount() {
        return tickets.size();
    }

    public int getActiveTicketCount() {
        return getActiveTickets().size();
    }

    public int getTechnicianCount() {
        return technicians.size();
    }
}