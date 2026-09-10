package springProject.msAccountReservation.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springProject.msAccountReservation.dto.ClientCreateRequest;
import springProject.msAccountReservation.dto.ClientExistsResponse;
import springProject.msAccountReservation.dto.ClientPageResponse;
import springProject.msAccountReservation.dto.ClientResponse;
import springProject.msAccountReservation.dto.ClientUpdateRequest;
import springProject.msAccountReservation.dto.PageableObject;
import springProject.msAccountReservation.entity.BillStatus;
import springProject.msAccountReservation.entity.Client;
import springProject.msAccountReservation.entity.ClientBill;
import springProject.msAccountReservation.entity.ClientStatus;
import springProject.msAccountReservation.exception.ClientConflictException;
import springProject.msAccountReservation.exception.ClientNotFoundException;
import springProject.msAccountReservation.mapper.ClientMapper;
import springProject.msAccountReservation.repository.ClientBillRepository;
import springProject.msAccountReservation.repository.ClientRepository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientBillRepository clientBillRepository;
    private final ClientMapper clientMapper;
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);


    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        if (clientRepository.existsByMdmCode(request.getMdmCode())) {
            logger.warn("Попытка создания уже существующего клиента");
            throw new ClientConflictException("Conflict: Client with mdmCode "
                    + request.getMdmCode() + " already exists");
        }

        Client client = clientMapper.toEntityFromCreateRequest(request);
        client.setStatus(ClientStatus.ACTIVE);

        Client savedClient = clientRepository.save(client);

        ClientBill newBill = new ClientBill();
        newBill.setClientId(client.getId());
        newBill.setStatus(BillStatus.ACTIVE);

        clientBillRepository.save(newBill);

        return clientMapper.toClientResponseFromClient(savedClient);
    }

    @Transactional
    public void deleteClient(UUID clientId) {
        if (!clientRepository.existsById(clientId)) {
            logger.warn("Попытка удалить несуществующего клиента");
            throw new ClientNotFoundException("Not Found: Client with ID "
                    + clientId + " does not exist");
        }
        if (clientBillRepository.existsByClientIdAndStatus(clientId, BillStatus.ACTIVE)) {
            logger.warn("Попытка удалить клиента с активными счетами");
            throw new ClientConflictException("У клиента есть активные счета. Удаление запрещено.");
        }

        clientRepository.softDeleteById(clientId);
    }

    public ClientResponse getClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(" Client with "
                        + clientId + " not found"));

        return clientMapper.toClientResponseFromClient(client);
    }

    public ClientPageResponse searchClients(Integer page, Integer size, String fullName, Long mdmCode) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Client> clientPage = clientRepository.findAll(pageable);

        List<ClientResponse> dtoList = clientPage.getContent().stream()
                .map(clientMapper::toClientResponseFromClient)
                .toList();

        PageableObject pageableObject = new PageableObject();
        pageableObject.setPageNumber(clientPage.getNumber());
        pageableObject.setPageSize(clientPage.getSize());
        pageableObject.setTotalPages(clientPage.getTotalPages());
        pageableObject.setTotalElements(clientPage.getTotalElements());

        ClientPageResponse response = new ClientPageResponse();
        response.setContent(dtoList);
        response.setPageable(pageableObject);

        return response;
    }

    public ClientResponse updateClient(UUID clientId, ClientUpdateRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client with ID "
                        + clientId + " not found"));

        clientMapper.updateClientFromRequest(request, client);

        Client updatedClient = clientRepository.save(client);

        return clientMapper.toClientResponseFromClient(updatedClient);
    }

    public ClientExistsResponse checkClientExists(UUID clientId) {
        Client client = clientRepository.findById(clientId).orElse(null);
        ClientExistsResponse response = new ClientExistsResponse();
        if (client != null) {
            response.setExists(true);
            response.setClientId(clientId);
            response.setStatus(springProject.msAccountReservation.dto.
                    ClientStatus.valueOf(client.getStatus().name()));
        } else
            response.setExists(false);
        return response;
    }

}