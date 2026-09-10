package springProject.msAccountReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springProject.msAccountReservation.entity.BillStatus;
import springProject.msAccountReservation.entity.ClientBill;

import java.util.UUID;

public interface ClientBillRepository extends JpaRepository<ClientBill, UUID> {
    boolean existsByClientIdAndStatus(UUID clientId, BillStatus status);
}
