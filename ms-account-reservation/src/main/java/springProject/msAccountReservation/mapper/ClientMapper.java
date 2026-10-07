package springProject.msAccountReservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import springProject.msAccountReservation.dto.ClientCreateRequest;
import springProject.msAccountReservation.dto.ClientResponse;
import springProject.msAccountReservation.dto.ClientUpdateRequest;
import springProject.msAccountReservation.entity.Client;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientResponse toClientResponseFromClient(Client client);
    Client toEntityFromCreateRequest (ClientCreateRequest request);
    void updateClientFromRequest(ClientUpdateRequest request, @MappingTarget Client client);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return value.atOffset(ZoneOffset.UTC);
    }
}
