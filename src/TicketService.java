import java.util.List;
import java.util.Optional;

/**
 * Klasa usługowa zapewniająca interfejs do operacji na systemie zgłoszeń IT.
 */
public class TicketService {
    private final TicketSystem ticketSystem;

    public TicketService(TicketSystem ticketSystem) {
        if (ticketSystem == null) {
            throw new IllegalArgumentException("System zgłoszeń nie może być null");
        }
        this.ticketSystem = ticketSystem;
    }

    public void addTicket(Ticket ticket) {
        ticketSystem.addTicket(ticket);
    }

    public void addTechnician(String technicianName) {
        ticketSystem.addTechnician(technicianName);
    }

    public List<Ticket> getAllTickets() {
        return ticketSystem.getAllTickets();
    }

    public List<Ticket> getActiveTickets() {
        return ticketSystem.getActiveTickets();
    }


    public List<String> getAllTechnicians() {
        return ticketSystem.getAllTechnicians();
    }

    public Ticket findTicketById(String ticketId) {
        Optional<Ticket> ticketOpt = ticketSystem.findTicketById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new IllegalArgumentException("Zgłoszenie o ID " + ticketId + " nie zostało znalezione");
        }
        return ticketOpt.get();
    }

    public String assignTicketWithConfirmation(String ticketId, String technicianName) {
        try {
            Ticket ticket = ticketSystem.assignTicket(ticketId, technicianName);
            return "Przypisano zgłoszenie '" + ticket.getTitle() + "' do technika: " + technicianName;
        } catch (Exception e) {
            return "Błąd podczas przypisywania: " + e.getMessage();
        }
    }

    public List<Ticket> getUnassignedTickets() {
        return ticketSystem.getUnassignedTickets();
    }


    public void closeTicket(String ticketId) {
        ticketSystem.closeTicket(ticketId);
    }

    public List<Ticket> getTicketsAssignedTo(String technicianName) {
        return ticketSystem.getTicketsAssignedTo(technicianName);
    }


    public void notifyAboutHighPriorityTicket(String ticketId) {
        ticketSystem.findTicketById(ticketId).ifPresent(ticket -> {
            if (!ticket.isAssigned() &&
                    (ticket.getPriority() == Ticket.Priority.WYSOKI || ticket.getPriority() == Ticket.Priority.KRYTYCZNY)) {
                System.out.println("ALERT: Nieprzypisane zgłoszenie o wysokim priorytecie - '"
                        + ticket.getTitle() + "' (ID: " + ticketId + ")");
            }
        });
    }

    public void removeTechnician(String technicianName) {
        ticketSystem.removeTechnician(technicianName);
    }

    public int getTotalTicketCount() {
        return ticketSystem.getTotalTicketCount();
    }

    public int getActiveTicketCount() {
        return ticketSystem.getActiveTicketCount();
    }

    public int getTechnicianCount() {
        return ticketSystem.getTechnicianCount();
    }
}
