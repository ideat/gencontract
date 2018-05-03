package mindware.com.model;

import java.util.Date;

public class ListContractLoan {
    private Integer loanNumber;
    private Integer contractId;
    private Date dateContract;
    private Double loanMount;
    private String Currency;
    private String debtorName;
    private String description;
    private String fileNameContract;

    public String getFileNameContract() {
        return fileNameContract;
    }

    public void setFileNameContract(String fileNameContract) {
        this.fileNameContract = fileNameContract;
    }

    public Integer getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(Integer loanNumber) {
        this.loanNumber = loanNumber;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Date getDateContract() {
        return dateContract;
    }

    public void setDateContract(Date dateContract) {
        this.dateContract = dateContract;
    }

    public Double getLoanMount() {
        return loanMount;
    }

    public void setLoanMount(Double loanMount) {
        this.loanMount = loanMount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
