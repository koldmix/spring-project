package springProject.msAccountReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springProject.msAccountReservation.entity.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
