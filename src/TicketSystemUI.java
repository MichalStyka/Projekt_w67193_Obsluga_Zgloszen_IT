import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Klasa obsługująca interfejs użytkownika dla systemu obsługi zgłoszeń IT.
 */
public class TicketSystemUI {
    private final TicketService ticketService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;
    private final Random random;

    public TicketSystemUI(TicketService ticketService) {
        if (ticketService == null) {
            throw new IllegalArgumentException("Usługa systemu zgłoszeń nie może być null");
            }
        this.ticketService = ticketService;
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.random = new Random();
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            int choice = readIntChoice();
            switch (choice) {
                case 1 -> {
                    displayAllTickets();
                    waitForKeyPress();
                }
                case 2 -> {

                    searchTicketById();
                    waitForKeyPress();
                }
                case 3 -> {
                    createNewTicket();
                    waitForKeyPress();
                }
                case 4 -> {
                    assignTicket();
                    waitForKeyPress();
                }
                case 5 -> {
                    closeTicket();
                    waitForKeyPress();
                }
                case 6 -> {
                    showActiveTickets();
                    waitForKeyPress();
                }
                case 7 -> {
                    showTechnicians();
                    waitForKeyPress();
                }
                case 8 -> {
                    addNewTechnician();
                    waitForKeyPress();
                }
                case 9 -> {
                    removeTechnician();
                    waitForKeyPress();
                }

                case 10 ->{
                    showUnassignedTickets();
                    waitForKeyPress();
                }

                case 11 ->
                    exit = true;
                

                default ->{
                        waitForKeyPress();
                        System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
                }
            }
        }
        scanner.close();
        System.out.println("Dziękujemy za korzystanie z systemu obsługi zgłoszeń IT!");
    }


    private void waitForKeyPress() {
        System.out.println("\nNaciśnij Enter, aby kontynuować...");
        scanner.nextLine();
    }

    private void displayMainMenu() {
        System.out.println("\n===== SYSTEM OBSŁUGI ZGŁOSZEŃ IT =====");
        System.out.println("1. Wyświetl wszystkie zgłoszenia");
        System.out.println("2. Wyszukaj zgłoszenie po ID");
        System.out.println("3. Utwórz nowe zgłoszenie");
        System.out.println("4. Przypisz zgłoszenie do technika");
        System.out.println("5. Zamknij zgłoszenie");
        System.out.println("6. Pokaż aktywne zgłoszenia");
        System.out.println("7. Pokaż techników");
        System.out.println("8. Dodaj nowego technika");
        System.out.println("9. Usuń technika");
        System.out.println("10.Pokaż nieprzypisane zgłoszenia");
        System.out.println("11.Wyjście");
        System.out.print("Wybierz opcję wpisując (1-11): ");
    }

    private void displayAllTickets() {
        System.out.println("\n===== WSZYSTKIE ZGŁOSZENIA =====");
        List<Ticket> tickets = ticketService.getAllTickets();
        if (tickets.isEmpty()) {
            System.out.println("Brak zgłoszeń w systemie.");

            return;
        }
        for (Ticket ticket : tickets) {
            displayTicketDetails(ticket);
        }
        System.out.println("\nŁączna liczba zgłoszeń: " + ticketService.getTotalTicketCount());
    }


    private void showActiveTickets() {
        System.out.println("\n===== AKTYWNE ZGŁOSZENIA =====");
        List<Ticket> tickets = ticketService.getActiveTickets();
        if (tickets.isEmpty()) {
            System.out.println("Brak aktywnych zgłoszeń w systemie.");
            return;
        }
        for (Ticket ticket : tickets) {
            displayTicketDetails(ticket);
        }
        System.out.println("\nLiczba aktywnych zgłoszeń: " + ticketService.getActiveTicketCount());
    }

//    private void showUnassignedTickets() {
//        System.out.println("\n===== NIEPRZYPISANE ZGŁOSZENIA =====");
//        List<Ticket> tickets = ticketService.getActiveTickets();
//        if (tickets.isEmpty()) {
//            System.out.println("Brak nieprzypisanych zgłoszeń w systemie. Wszystkie zgłoszenia są obecnie obsługiwane przez technika.");
//            return;
//        }
//        for (Ticket ticket : tickets) {
//            displayTicketDetails(ticket);
//        }
//        System.out.println("\nLiczba nieprzypisanych zgłoszeń: " + ticketService.getActiveTicketCount());
//    }

    private void displayTicketDetails(Ticket ticket) {
        System.out.println("\nID: " + ticket.getTicketId());
        System.out.println("Tytuł: " + ticket.getTitle());
        System.out.println("Opis: " + ticket.getDescription());
        System.out.println("Zgłaszający: " + ticket.getReporterName() + " (" + ticket.getReporterEmail() + ")");
        System.out.println("Priorytet: " + ticket.getPriority());
        System.out.println("Status: " + ticket.getStatus());
        System.out.println("Przypisany do: " + (ticket.getAssignedTechnicianName() != null ?
                ticket.getAssignedTechnicianName() : "brak"));
        System.out.println("Utworzono: " + ticket.getCreatedAt().format(dateFormatter));

        System.out.println("---------------------------");
    }

    private void searchTicketById() {
        System.out.println("\n===== WYSZUKIWANIE ZGŁOSZENIA =====");
        System.out.print("Podaj ID zgłoszenia (TKT-XXXXX): ");
        String ticketId = scanner.nextLine().trim();
        try {
            Ticket ticket = ticketService.findTicketById(ticketId);
            System.out.println("\nZnaleziono zgłoszenie:");
            displayTicketDetails(ticket);
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void createNewTicket() {
        System.out.println("\n===== TWORZENIE NOWEGO ZGŁOSZENIA =====");
        System.out.print("Tytuł zgłoszenia: ");
        String title = scanner.nextLine().trim();
        System.out.print("Opis problemu: ");
        String description = scanner.nextLine().trim();
        System.out.print("Imię i nazwisko zgłaszającego: ");
        String reporterName = scanner.nextLine().trim();
        System.out.print("Email zgłaszającego: ");
         String reporterEmail = scanner.nextLine().trim();

        System.out.println("Wybierz priorytet:");
        System.out.println("1. Niski");
        System.out.println("2. Średni");
        System.out.println("3. Wysoki");
        System.out.println("4. Krytyczny");
        System.out.print("Wybór: ");
        int priorityChoice = readIntChoice();

        Ticket.Priority priority = switch (priorityChoice) {
            case 1 -> Ticket.Priority.NISKI;
            case 2 -> Ticket.Priority.SREDNI;
            case 3 -> Ticket.Priority.WYSOKI;
            case 4 -> Ticket.Priority.KRYTYCZNY;
            default -> Ticket.Priority.SREDNI;
        };

        try {
            String ticketId = generateTicketId();
            Ticket ticket = new Ticket(ticketId, title, description, reporterName, reporterEmail, priority);
            ticketService.addTicket(ticket);
            System.out.println("Zgłoszenie zostało utworzone z ID: " + ticketId);

            if (priority == Ticket.Priority.WYSOKI || priority == Ticket.Priority.KRYTYCZNY) {
                ticketService.notifyAboutHighPriorityTicket(ticketId);
            }
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void assignTicket() {
         System.out.println("\n===== PRZYPISYWANIE ZGŁOSZENIA =====");
        System.out.print("Podaj ID zgłoszenia do przypisania (TKT-XXXXX): ");
        String ticketId = scanner.nextLine().trim();

        System.out.println("\nDostępni technicy:");
        List<String> technicians = ticketService.getAllTechnicians();
        for (int i = 0; i < technicians.size(); i++) {
            System.out.println((i + 1) + ". " + technicians.get(i));
        }

        System.out.print("Wybierz technika (1-" + technicians.size() + "): ");
        int techChoice = readIntChoice();

        if (techChoice >= 1 && techChoice <= technicians.size()) {
            String technicianName = technicians.get(techChoice - 1);
            try {
                String result = ticketService.assignTicketWithConfirmation(ticketId, technicianName);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
            }
        } else {
            System.out.println("Nieprawidłowy wybór technika.");
        }
    }



    private void closeTicket() {
        System.out.println("\n===== ZAMYKANIE ZGŁOSZENIA =====");
        System.out.print("Podaj ID zgłoszenia do zamknięcia (TKT-XXXXX): ");
        String ticketId = scanner.nextLine().trim();

        try {
            ticketService.closeTicket(ticketId);
            System.out.println("Zgłoszenie zostało pomyślnie zamknięte.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showTechnicians() {
        System.out.println("\n===== TECHNICY W SYSTEMIE =====");
        List<String> technicians = ticketService.getAllTechnicians();
        if (technicians.isEmpty()) {
            System.out.println("Brak techników w systemie.");
            return;
        }

        for (String technician : technicians) {
            List<Ticket> assignedTickets = ticketService.getTicketsAssignedTo(technician);
            int activeTickets = (int) assignedTickets.stream()
                    .filter(Ticket::isActive)
                    .count();
            System.out.println("• " + technician + " - przypisanych zgłoszeń: " + activeTickets);
        }
        System.out.println("\nŁączna liczba techników: " + ticketService.getTechnicianCount());
    }

    private void addNewTechnician() {
        System.out.println("\n===== DODAWANIE NOWEGO TECHNIKA =====");
        System.out.print("Podaj imię i nazwisko technika: ");
        String technicianName = scanner.nextLine().trim();

        try {
            ticketService.addTechnician(technicianName);
            System.out.println("Technik został pomyślnie dodany do systemu.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void removeTechnician() {
        System.out.println("\n===== USUWANIE TECHNIKA =====");

        List<String> technicians = ticketService.getAllTechnicians();
        if (technicians.isEmpty()) {
            System.out.println("Brak techników w systemie.");
            waitForKeyPress();
            return;
        }

        System.out.println("Dostępni technicy:");
        for (int i = 0; i < technicians.size(); i++) {
            String technicianName = technicians.get(i);
            int activeTickets = ticketService.getTicketsAssignedTo(technicianName)
                    .stream()
                    .mapToInt(ticket -> ticket.isActive() ? 1 : 0)
                    .sum();
            System.out.println((i + 1) + ". " + technicianName +
                    " (aktywnych zgłoszeń: " + activeTickets + ")");
        }

        System.out.print("Wybierz technika do usunięcia (1-" + technicians.size() + "): ");
        int choice = readIntChoice();

        if (choice >= 1 && choice <= technicians.size()) {
            String technicianName = technicians.get(choice - 1);


            try {
                ticketService.removeTechnician(technicianName);
                System.out.println("Technik został pomyślnie usunięty z systemu.");
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
            }

        }
    }

    /**
            * Wyświetla nieprzypisane zgłoszenia.
 */
    private void showUnassignedTickets() {
        System.out.println("\n===== NIEPRZYPISANE ZGŁOSZENIA =====");
        List<Ticket> tickets = ticketService.getUnassignedTickets();
        if (tickets.isEmpty()) {
            System.out.println("Wszystkie zgłoszenia są przypisane.");
            return;
        }
        for (Ticket ticket : tickets) {
            displayTicketDetails(ticket);
        }
        System.out.println("\nLiczba nieprzypisanych zgłoszeń: " + tickets.size());
        waitForKeyPress();
    }


    private String generateTicketId() {
        int number = 10000 + random.nextInt(90000);
        return "TKT-" + number; //ticket
    }

    private int readIntChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

