package mindware.com.model;

import java.util.Date;

public class Warranty {
    private Integer id;
    private Integer loanNumber;
    private String  typeGuarantee;
    private String currency;
    private Double assessmentEntity;
    private Date expirationDate;
    private String numberRealRight;
    private Date dateRealRight;
    private String mortageNumber;
    private Date dateMortage;
    private String description;
    private String enoughGuarante;

    private String codeGuarantee;
    private String numeroPizarra;
    private String numeroCUI;
    private String entidadEmisora;
    private String titular;


    public String getNumeroPizarra() {
        return numeroPizarra;
    }

    public void setNumeroPizarra(String numeroPizarra) {
        this.numeroPizarra = numeroPizarra;
    }

    public String getNumeroCUI() {
        return numeroCUI;
    }

    public void setNumeroCUI(String numeroCUI) {
        this.numeroCUI = numeroCUI;
    }

    public String getEntidadEmisora() {
        return entidadEmisora;
    }

    public void setEntidadEmisora(String entidadEmisora) {
        this.entidadEmisora = entidadEmisora;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCodeGuarantee() {
        return codeGuarantee;
    }

    public void setCodeGuarantee(String codeGuarantee) {
        this.codeGuarantee = codeGuarantee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(Integer loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getTypeGuarantee() {
        return typeGuarantee;
    }

    public void setTypeGuarantee(String typeGuarantee) {
        this.typeGuarantee = typeGuarantee;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAssessmentEntity() {
        return assessmentEntity;
    }

    public void setAssessmentEntity(Double assessmentEntity) {
        this.assessmentEntity = assessmentEntity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getNumberRealRight() {
        return numberRealRight;
    }

    public void setNumberRealRight(String numberRealRight) {
        this.numberRealRight = numberRealRight;
    }

    public Date getDateRealRight() {
        return dateRealRight;
    }

    public void setDateRealRight(Date dateRealRight) {
        this.dateRealRight = dateRealRight;
    }

    public String getMortageNumber() {
        return mortageNumber;
    }

    public void setMortageNumber(String mortageNumber) {
        this.mortageNumber = mortageNumber;
    }

    public Date getDateMortage() {
        return dateMortage;
    }

    public void setDateMortage(Date dateMortage) {
        this.dateMortage = dateMortage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnoughGuarante() {
        return enoughGuarante;
    }

    public void setEnoughGuarante(String enoughGuarante) {
        this.enoughGuarante = enoughGuarante;
    }
}
