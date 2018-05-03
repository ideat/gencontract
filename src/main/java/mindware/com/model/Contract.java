package mindware.com.model;

import java.util.Date;
import java.util.stream.Stream;

public class Contract {
    private Integer contractId;
    private Integer loanDataId;
    private String description;
    private Byte[] document;
    private Date dateContract;
    private String fileNameContract;
    private LoanData loanData;

    public LoanData getLoanData() {
        return loanData;
    }

    public void setLoanData(LoanData loanData) {
        this.loanData = loanData;
    }

    public String getFileNameContract() {
        return fileNameContract;
    }

    public void setFileNameContract(String fileNameContract) {
        this.fileNameContract = fileNameContract;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getLoanDataId() {
        return loanDataId;
    }

    public void setLoanDataId(Integer loanDataId) {
        this.loanDataId = loanDataId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte[] getDocument() {
        return document;
    }

    public void setDocument(Byte[] document) {
        this.document = document;
    }

    public Date getDateContract() {
        return dateContract;
    }

    public void setDateContract(Date dateContract) {
        this.dateContract = dateContract;
    }

}
