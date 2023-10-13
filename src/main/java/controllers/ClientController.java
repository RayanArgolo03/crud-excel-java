package controllers;

import services.ClientService;
public class ClientController {
    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    public ClientService getClientService() {return clientService;}

}
