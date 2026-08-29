package springProject.msAccountReservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import springProject.msAccountReservation.dto.ClientCreateRequest;
import springProject.msAccountReservation.dto.ClientExistsResponse;
import springProject.msAccountReservation.dto.ClientPageResponse;
import springProject.msAccountReservation.dto.ClientResponse;
import springProject.msAccountReservation.dto.ClientUpdateRequest;
import springProject.msAccountReservation.entity.BillStatus;
import springProject.msAccountReservation.entity.Client;
import springProject.msAccountReservation.entity.ClientStatus;
import springProject.msAccountReservation.exception.ClientConflictException;
import springProject.msAccountReservation.exception.ClientNotFoundException;
import springProject.msAccountReservation.repository.ClientBillRepository;
import springProject.msAccountReservation.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    ClientRepository clientRepository;
    @Mock
    ClientBillRepository clientBillRepository;
    @InjectMocks
    ClientService clientService;
    UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
    }

    @Test
    void createClient_ValidClientCreateRequest_shouldReturnNewClientResponse() {
        // Arrange
        ClientCreateRequest clientCreateRequest = new ClientCreateRequest();
        clientCreateRequest.setMdmCode(88888L);
        clientCreateRequest.setFullName("Full name");
        Client client = new Client();
        client.setMdmCode(clientCreateRequest.getMdmCode());
        client.setFullName(clientCreateRequest.getFullName());
        when(clientRepository.existsByMdmCode(clientCreateRequest.getMdmCode()))
                .thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        // Act
        ClientResponse response = clientService.createClient(clientCreateRequest);
        // Assert
        assertNotNull(response);
        assertEquals(88888L, response.getMdmCode());
        assertEquals("Full name", response.getFullName());
    }

    @Test
    void createClient_ClientCreateRequestWithExistsMdm_shouldThrowClientConflictException() {
        // Arrange
        ClientCreateRequest clientCreateRequest = new ClientCreateRequest();
        clientCreateRequest.setMdmCode(88888L);
        clientCreateRequest.setFullName("Full name");
        when(clientRepository.existsByMdmCode(clientCreateRequest.getMdmCode()))
                .thenReturn(true);
        // Assert
        assertThrows(ClientConflictException.class,
                () -> clientService.createClient(clientCreateRequest));
    }

    @Test
    void deleteClient_ExistsClientIdAndClosedBill_shouldReturnNoContent() {
        // Arrange
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(clientBillRepository.existsByClientIdAndStatus(clientId, BillStatus.ACTIVE))
                .thenReturn(false);
        // Act
        clientService.deleteClient(clientId);
        // Assert
        verify(clientRepository).deleteById(clientId);
    }

    @Test
    void deleteClient_NotExistsClientId_shouldThrowClientNotFoundException() {
        // Arrange
        when(clientRepository.existsById(clientId)).thenReturn(false);
        // Assert
        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClient(clientId));
    }

    @Test
    void deleteClient_ActiveBill_shouldThrowClientConflictException() {
        // Arrange
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(clientBillRepository.existsByClientIdAndStatus(clientId, BillStatus.ACTIVE))
                .thenReturn(true);
        // Assert
        assertThrows(ClientConflictException.class, () -> clientService.deleteClient(clientId));
    }

    @Test
    void getClient_ExistsClientId_shouldReturnClientResponse() {
        // Arrange
        Client client = new Client();
        client.setId(clientId);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        // Act
        ClientResponse response = clientService.getClient(clientId);
        // Assert
        assertNotNull(response);
        assertEquals(response.getId(), clientId);
    }

    @Test
    void getClient_NotExistsClientId_shouldThrowClientNotFoundException() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        // Assert
        assertThrows(ClientNotFoundException.class, () -> clientService.getClient(clientId));
    }

    @Test
    void searchClients_ValidPageAndSize_shouldReturnClientPageResponse() {
        // Arrange
        Client client = new Client();
        client.setStatus(ClientStatus.ACTIVE);
        client.setId(clientId);
        List<Client> clientList = List.of(client);
        Page<Client> clientPage = new PageImpl<>(clientList);
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(clientPage);
        // Act
        ClientPageResponse response = clientService.searchClients(0, 20,
                null, null);
        // Assertion
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void searchClients_InvalidPageAndSize_shouldThrowClientNotFoundException() {
        // Arrange
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        // Assertion
        assertThrows(ClientNotFoundException.class, () -> clientService.searchClients(0,
                20, null, null));
    }

    @Test
    void updateClient_ExistsClientId_shouldReturnClientResponse() {
        // Arrange
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest();
        clientUpdateRequest.setFullName("new full name");
        Client client = new Client();
        client.setId(clientId);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        // Act
        ClientResponse response = clientService.updateClient(clientId, clientUpdateRequest);
        // Assert
        assertNotNull(response);
        assertEquals("new full name", response.getFullName());
    }

    @Test
    void updateClient_NotExistsClientId_shouldThrowClientNotFoundException() {
        // Arrange
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest();
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        // Assert
        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(clientId,
                clientUpdateRequest));
    }

    @Test
    void checkClientExists_ExistsClientId_shouldReturnClientExistsResponse() {
        // Arrange
        Client client = new Client();
        client.setId(clientId);
        client.setStatus(ClientStatus.ACTIVE);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        // Act
        ClientExistsResponse response = clientService.checkClientExists(clientId);
        // Assert
        assertEquals(response.getClientId(), clientId);
    }

    @Test
    void checkClientExists_NotExistsClientId_shouldReturnClientExistsResponse() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        // Act
        ClientExistsResponse response = clientService.checkClientExists(clientId);
        // Assert
        assertNotNull(response);
        assertFalse(response.getExists());
    }
}
