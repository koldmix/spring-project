package springProject.msAccountReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springProject.msAccountReservation.entity.Client;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}
