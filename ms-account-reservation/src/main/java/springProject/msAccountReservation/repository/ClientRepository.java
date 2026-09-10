package springProject.msAccountReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springProject.msAccountReservation.entity.Client;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByMdmCode(long mdmCode);
    @Modifying
    @Query("""
        UPDATE Client c
        SET c.status = 'DELETED'
        WHERE c.id = :clientId
    """)
    int softDeleteById(@Param("clientId") UUID clientId);
}
