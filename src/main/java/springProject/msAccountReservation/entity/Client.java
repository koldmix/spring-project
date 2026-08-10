package springProject.msAccountReservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "client_type")
    private String clientType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "document_series")
    private String documentSeries;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "mdm_code")
    private long mdmCode;

    public Client(UUID id, String fullName, String citizenship, String clientType, String documentNumber, String documentSeries, String documentType, long mdmCode) {
        this.id = id;
        this.fullName = fullName;
        this.citizenship = citizenship;
        this.clientType = clientType;
        this.documentNumber = documentNumber;
        this.documentSeries = documentSeries;
        this.documentType = documentType;
        this.mdmCode = mdmCode;
    }

    public Client() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentSeries() {
        return documentSeries;
    }

    public void setDocumentSeries(String documentSeries) {
        this.documentSeries = documentSeries;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public long getMdmCode() {
        return mdmCode;
    }

    public void setMdmCode(long mdmCode) {
        this.mdmCode = mdmCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Client client)) return false;
        return id == client.id && mdmCode == client.mdmCode && Objects.equals(fullName, client.fullName) && Objects.equals(citizenship, client.citizenship) && Objects.equals(clientType, client.clientType) && Objects.equals(documentNumber, client.documentNumber) && Objects.equals(documentSeries, client.documentSeries) && Objects.equals(documentType, client.documentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, citizenship, clientType, documentNumber, documentSeries, documentType, mdmCode);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", citizenship='" + citizenship + '\'' +
                ", clientType='" + clientType + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", documentSeries='" + documentSeries + '\'' +
                ", documentType='" + documentType + '\'' +
                ", mdmCode=" + mdmCode +
                '}';
    }
}
