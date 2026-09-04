package springProject.msAccountReservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import springProject.msAccountReservation.AbstractIntegrationTest;
import springProject.msAccountReservation.DbCleaner;
import springProject.msAccountReservation.JsonReaderFromFile;
import springProject.msAccountReservation.dto.ClientCreateRequest;
import springProject.msAccountReservation.dto.ClientUpdateRequest;
import springProject.msAccountReservation.entity.Client;
import springProject.msAccountReservation.entity.ClientStatus;
import springProject.msAccountReservation.repository.ClientRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ClientControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DbCleaner cleaner;
    @Autowired
    private ObjectMapper objectMapper;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        cleaner.cleaner();
    }

    @Test
    void getClients_NotClientExists_shouldReturnOK() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk());
    }

    @Test
    void getClient_ClientNotExists_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/clients/{clientId}", clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getClient_ClientExists_shouldReturnOK() throws Exception {
        // Arrange
        Client client = new Client();
        client.setMdmCode(88888L);
        client.setFullName("Иван Иванов");

        Client savedClient = clientRepository.save(client);

        // Act
        mockMvc.perform(get("/clients/{clientId}", savedClient.getId()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedClient.getId().toString()))
                .andExpect(jsonPath("$.mdmCode").value(88888))
                .andExpect(jsonPath("$.fullName").value("Иван Иванов"));
    }

    @Test
    void createClient_ValidRequest_shouldReturnCreated() throws Exception {
        // Arrange
        ClientCreateRequest request = JsonReaderFromFile.readerJson(
                "json/mdm-fullname-client.json", ClientCreateRequest.class);

        // Act
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated());
    }

    @Test
    void createClient_ExistsMdm_shouldReturnConflict() throws Exception {
        // Arrange
        Client client = new Client();
        client.setMdmCode(88888L);
        client.setFullName("Имя");

        clientRepository.save(client);

        ClientCreateRequest request = JsonReaderFromFile.readerJson(
                "json/mdm-fullname-client.json", ClientCreateRequest.class);

        // Act
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());
    }

    @Test
    void createClient_InvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateClient_ClientExists_shouldReturnOK() throws Exception {
        // Arrange
        Client client = new Client();
        client.setMdmCode(88888L);
        client.setFullName("Старое имя");

        Client savedClient = clientRepository.save(client);

        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое имя");

        // Act
        mockMvc.perform(put("/clients/{clientId}", savedClient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Новое имя"));
    }

    @Test
    void updateClient_ClientNotExists_shouldReturnNotFound() throws Exception {
        // Arrange
        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое имя");

        // Act
        mockMvc.perform(put("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void updateClient_InvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteClient_ClientExistsAndNoActiveBill_shouldReturnNoContent() throws Exception {
        // Arrange
        Client client = new Client();
        client.setMdmCode(88888L);
        client.setFullName("Иван Иванов");

        Client savedClient = clientRepository.save(client);

        // Act
        mockMvc.perform(delete("/clients/{clientId}", savedClient.getId()))
                // Assert
                .andExpect(status().isNoContent());

        // Verify
        Client deletedClient = clientRepository
                .findById(savedClient.getId())
                .orElseThrow();

        assertThat(deletedClient.getStatus())
                .isEqualTo(ClientStatus.DELETED);
    }

    @Test
    void deleteClient_ClientNotExists_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/clients/{clientId}", clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkClientExists_ClientExists_shouldReturnTrue() throws Exception {
        // Arrange
        Client client = new Client();
        client.setMdmCode(88888L);
        client.setFullName("Иван Иванов");

        Client savedClient = clientRepository.save(client);

        // Act
        mockMvc.perform(get("/clients/{clientId}/exists", savedClient.getId()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.clientId")
                        .value(savedClient.getId().toString()));
    }

    @Test
    void checkClientExists_ClientNotExists_shouldReturnFalse() throws Exception {
        mockMvc.perform(get("/clients/{clientId}/exists", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }
}
