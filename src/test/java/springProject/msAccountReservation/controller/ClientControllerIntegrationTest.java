package springProject.msAccountReservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import springProject.msAccountReservation.AbstractIntegrationTest;
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

@DBRider
@AutoConfigureMockMvc
@DBUnit(alwaysCleanBefore = true)
public class ClientControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
    }

    @DataSet(cleanBefore = true)
    @Test
    void getClients_NotClientExists_shouldReturnOK() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk());
    }

    @DataSet(cleanBefore = true)
    @Test
    void getClient_ClientNotExists_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/clients/{clientId}", clientId))
                .andExpect(status().isNotFound());
    }

    @DataSet(value = "datasets/client.yaml", useSequenceFiltering = false)
    @Test
    void getClient_ClientExists_shouldReturnOK() throws Exception {
        // Act
        mockMvc.perform(get("/clients/{clientId}",
                        UUID.fromString("11111111-1111-1111-1111-111111111111")))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.mdmCode").value(88888))
                .andExpect(jsonPath("$.fullName").value("Иван Иванов"));
    }

    @DataSet(cleanBefore = true)
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

    @DataSet(value = "datasets/client.yaml", useSequenceFiltering = false)
    @Test
    void createClient_ExistsMdm_shouldReturnConflict() throws Exception {
        ClientCreateRequest request = JsonReaderFromFile.readerJson(
                "json/mdm-fullname-client.json", ClientCreateRequest.class);

        // Act
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());
    }

    @DataSet(cleanBefore = true)
    @Test
    void createClient_InvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @DataSet(value = "datasets/client.yaml", useSequenceFiltering = false)
    @Test
    void updateClient_ClientExists_shouldReturnOK() throws Exception {
        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое имя");

        // Act
        mockMvc.perform(put("/clients/{clientId}",
                        UUID.fromString("11111111-1111-1111-1111-111111111111"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Новое имя"));
    }

    @DataSet(cleanBefore = true)
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

    @DataSet(cleanBefore = true)
    @Test
    void updateClient_InvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @DataSet(value = "datasets/client.yaml", useSequenceFiltering = false)
    @Test
    void deleteClient_ClientExistsAndNoActiveBill_shouldReturnNoContent() throws Exception {
        // Act
        mockMvc.perform(delete("/clients/{clientId}",
                        UUID.fromString("11111111-1111-1111-1111-111111111111")))
                // Assert
                .andExpect(status().isNoContent());

        // Verify
        Client deletedClient = clientRepository
                .findById(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .orElseThrow();

        assertThat(deletedClient.getStatus())
                .isEqualTo(ClientStatus.DELETED);
    }

    @DataSet(cleanBefore = true)
    @Test
    void deleteClient_ClientNotExists_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/clients/{clientId}", clientId))
                .andExpect(status().isNotFound());
    }

    @DataSet(value = "datasets/client.yaml", useSequenceFiltering = false)
    @Test
    void checkClientExists_ClientExists_shouldReturnTrue() throws Exception {
        // Act
        mockMvc.perform(get("/clients/{clientId}/exists",
                        UUID.fromString("11111111-1111-1111-1111-111111111111")))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.clientId")
                        .value("11111111-1111-1111-1111-111111111111"));
    }

    @DataSet(cleanBefore = true)
    @Test
    void checkClientExists_ClientNotExists_shouldReturnFalse() throws Exception {
        mockMvc.perform(get("/clients/{clientId}/exists", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }
}
