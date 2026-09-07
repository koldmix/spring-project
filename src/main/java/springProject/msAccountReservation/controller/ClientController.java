package springProject.msAccountReservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import springProject.msAccountReservation.api.ClientsApi;
import springProject.msAccountReservation.dto.*;
import springProject.msAccountReservation.service.ClientService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientController implements ClientsApi {

    private final ClientService clientService;

    @Override
    public ResponseEntity<ClientResponse> createClient(ClientCreateRequest request) {
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteClient(UUID clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ClientResponse> getClient(UUID clientId) {
        ClientResponse response = clientService.getClient(clientId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ClientPageResponse> searchClients(Integer page, Integer size, String fullName, Long mdmCode) {
        ClientPageResponse response = clientService.searchClients(page, size, fullName, mdmCode);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ClientResponse> updateClient(UUID clientId, ClientUpdateRequest request) {
        ClientResponse response = clientService.updateClient(clientId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ClientExistsResponse> checkClientExists(UUID clientId) {
        ClientExistsResponse response = clientService.checkClientExists(clientId);
        return ResponseEntity.ok(response);
    }
}