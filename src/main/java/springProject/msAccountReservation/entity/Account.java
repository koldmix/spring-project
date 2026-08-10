package springProject.msAccountReservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "status_id")
    @ManyToOne
    private AccountStatus statusId;

    @JoinColumn(name = "client_id")
    @ManyToOne
    private Client clientId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "currency_code")
    private String currencyCode;

    public Account(UUID id, AccountStatus statusId, Client clientId, String accountType, String currencyCode) {
        this.id = id;
        this.statusId = statusId;
        this.clientId = clientId;
        this.accountType = accountType;
        this.currencyCode = currencyCode;
    }

    public Account() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AccountStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(AccountStatus statusId) {
        this.statusId = statusId;
    }

    public Client getClientId() {
        return clientId;
    }

    public void setClientId(Client clientId) {
        this.clientId = clientId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return id == account.id && statusId == account.statusId && clientId == account.clientId && Objects.equals(accountType, account.accountType) && Objects.equals(currencyCode, account.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusId, clientId, accountType, currencyCode);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", statusId=" + statusId +
                ", clientId=" + clientId +
                ", accountType='" + accountType + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
