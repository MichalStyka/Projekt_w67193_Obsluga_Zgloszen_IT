public static void main(String[] args) {
    // Tworzenie systemu zgłoszeń
    TicketSystem ticketSystem = new TicketSystem();

    // Tworzenie usługi systemu zgłoszeń
    TicketService ticketService = new TicketService(ticketSystem);

    // Tworzenie i uruchomienie interfejsu użytkownika
    TicketSystemUI ticketSystemUI = new TicketSystemUI(ticketService);
    ticketSystemUI.start();
}
