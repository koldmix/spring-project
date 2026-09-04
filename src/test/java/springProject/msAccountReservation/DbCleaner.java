package springProject.msAccountReservation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbCleaner {
    private final JdbcTemplate jdbcTemplate;

    public DbCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void cleaner(){
        jdbcTemplate.execute("""
                TRUNCATE TABLE 
                account, client 
                RESTART IDENTITY CASCADE
                """);
    }
}
