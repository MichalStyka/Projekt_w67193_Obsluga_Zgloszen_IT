public static void main(String[] args) {
    // Tworzenie systemu zgłoszeń
    TicketSystem ticketSystem = new TicketSystem();

    // Tworzenie usługi systemu zgłoszeń
    TicketService ticketService = new TicketService(ticketSystem);

    // Dodanie przykładowych zgłoszeń
    try {
        ticketService.addTicket(new Ticket("TKT-12345", "Nie działa drukarka",
                "Drukarka w biurze nie reaguje na polecenia drukowania",
                "Anna Kowalska", "anna.kowalska@firma.pl", Ticket.Priority.SREDNI));

        ticketService.addTicket(new Ticket("TKT-12346", "Błąd serwera email",
                "Serwer poczty elektronicznej zwraca błędy 500",
                "Piotr Nowak", "piotr.nowak@firma.pl", Ticket.Priority.WYSOKI));

    } catch (IllegalArgumentException e) {
        System.out.println("Błąd podczas dodawania przykładowych zgłoszeń: " + e.getMessage());
    }

    // Tworzenie i uruchomienie interfejsu użytkownika
    TicketSystemUI ticketSystemUI = new TicketSystemUI(ticketService);
    ticketSystemUI.start();
}