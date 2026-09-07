package springProject.msAccountReservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import springProject.msAccountReservation.JsonReaderFromFile;
import springProject.msAccountReservation.dto.ClientCreateRequest;
import springProject.msAccountReservation.dto.ClientExistsResponse;
import springProject.msAccountReservation.dto.ClientPageResponse;
import springProject.msAccountReservation.dto.ClientResponse;
import springProject.msAccountReservation.dto.ClientUpdateRequest;
import springProject.msAccountReservation.exception.ClientConflictException;
import springProject.msAccountReservation.exception.ClientNotFoundException;
import springProject.msAccountReservation.service.ClientService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ClientController.class)
public class ClientControllerTest {
    @MockitoBean
    private ClientService clientService;
    @Autowired
    private MockMvc mockMvc;
    private UUID clientId;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        clientId = UUID.randomUUID();
    }

    @Test
    public void createClient_ValidRequest_shouldReturnCreated() throws Exception {
        // Arrange
        ClientCreateRequest createRequest = JsonReaderFromFile.readerJson(
                "json/mdm-fullname-client.json", ClientCreateRequest.class);
        ClientResponse response = new ClientResponse();
        when(clientService.createClient(createRequest)).thenReturn(response);
        // Act
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                // Assert
                .andExpect(status().isCreated());
    }

    @Test
    public void createClient_NotValidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                // Assert
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createClient_ExistsMdm_shouldReturnConflict() throws Exception {
        // Arrange
        ClientCreateRequest createRequest = JsonReaderFromFile.readerJson(
                "json/mdm-fullname-client.json", ClientCreateRequest.class);
        when(clientService.createClient(createRequest))
                .thenThrow(new ClientConflictException("Клиент с существующим mdmCode"));
        // Act
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                // Assert
                .andExpect(status().isConflict());
    }

    @Test
    public void deleteClient_ValidId_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/clients/{clientId}", clientId))
                .andExpect(status().isNoContent());
        verify(clientService).deleteClient(clientId);
    }

    @Test
    public void deleteClient_NotValidId_shouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new ClientNotFoundException("Клиент не найден"))
                .when(clientService).deleteClient(clientId);
        // Act
        mockMvc.perform(delete("/clients/{clientId}", clientId))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteClient_ExistsBill_shouldReturnConflict() throws Exception {
        // Arrange
        doThrow(new ClientConflictException("У клиента есть активные счета"))
                .when(clientService).deleteClient(clientId);
        // Act
        mockMvc.perform(delete("/clients/{clientId}", clientId))
                // Assert
                .andExpect(status().isConflict());
    }

    @Test
    public void getClient_ValidRequest_shouldReturnOK() throws Exception {
        // Arrange
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setId(clientId);
        clientResponse.setFullName("Иван Иваныч");
        when(clientService.getClient(clientId)).thenReturn(clientResponse);
        // Act
        mockMvc.perform(get("/clients/{clientId}", clientId))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.fullName").value(clientResponse.getFullName()));
    }

    @Test
    public void getClient_NotExistsId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(clientService.getClient(clientId)).thenThrow(ClientNotFoundException.class);
        // Act
        mockMvc.perform(get("/clients/{clientId}", clientId))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchClients_ValidRequest_shouldReturnOK() throws Exception {
        // Arrange
        ClientPageResponse clientPageResponse = new ClientPageResponse();
        when(clientService.searchClients(2, 10, null,  null))
                .thenReturn(clientPageResponse);
        // Act
        mockMvc.perform(get("/clients"))
        // Assert
                .andExpect(status().isOk());
    }

    @Test
    public void updateClient_ValidRequest_shouldReturnOK() throws Exception {
        // Arrange
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest();
        clientUpdateRequest.setFullName("Новое имя");
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setFullName(clientUpdateRequest.getFullName());
        when(clientService.updateClient(eq(clientId), any(ClientUpdateRequest.class)))
                .thenReturn(clientResponse);
        // Act
        mockMvc.perform(put("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\": \"Новое имя\"}"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Новое имя"));
    }

    @Test
    public void updateClient_NotValidRequest_shouldReturnBadRequest() throws Exception {
        // Arrange
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest();
        clientUpdateRequest.setFullName("Новое имя");
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setFullName(clientUpdateRequest.getFullName());
        when(clientService.updateClient(eq(clientId), any(ClientUpdateRequest.class)))
                .thenReturn(clientResponse);
        // Act
        mockMvc.perform(put("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateClient_NotExistsId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(clientService.updateClient(eq(clientId), any(ClientUpdateRequest.class)))
                .thenThrow(ClientNotFoundException.class);
        // Act
        mockMvc.perform(put("/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\": \"Новое имя\"}"))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    public void checkClientExists_ValidRequest_shouldReturnOK() throws Exception {
        // Arrange
        ClientExistsResponse clientExistsResponse = new ClientExistsResponse();
        clientExistsResponse.setExists(true);
        when(clientService.checkClientExists(clientId)).thenReturn(clientExistsResponse);
        // Act
        mockMvc.perform(get("/clients/{clientId}/exists", clientId))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }
}