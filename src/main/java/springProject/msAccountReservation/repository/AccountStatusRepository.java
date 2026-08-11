package springProject.msAccountReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springProject.msAccountReservation.entity.AccountStatus;

public interface AccountStatusRepository extends JpaRepository<AccountStatus, Integer> {
}
