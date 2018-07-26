package mindware.com.model;

import com.fasterxml.jackson.core.TreeNode;
import com.google.gson.JsonArray;

import java.util.Date;

public class LoanData {
    private Integer loanDataId;
    private Integer branchOfficeId;
    private Integer loanNumber;
    private Date loanDate;//*
    private String currency;
    private Double loanMount;
    private Integer loanTerm;
    private Double interestRate;
    private Double treRate; //*
    private Double teacRate; //*
    private Double feePayment; //*
    private String paymentFrecuency; //*
    private Double creditLifeInsurance;
    private Double totalPayment; //*
    private String loanDestination; //*
    private String agency;
    private String official;
    private String debtorName;
    private String identityCardDebtor;
    private String addressDebtor;
    private String civilStatusDebtor;
    private String genderDebtor;
    private Integer clientLoanId;
    private Integer fixedPaymentDay;
    private String coDebtors;
    private String guarantors;
    private String warranty;
    private String extraData;
    private BranchOffice branchOffice;


    public BranchOffice getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(BranchOffice branchOffice) {
        this.branchOffice = branchOffice;
    }

    public Integer getLoanDataId() {
        return loanDataId;
    }

    public void setLoanDataId(Integer loanDataId) {
        this.loanDataId = loanDataId;
    }

    public Integer getBranchOfficeId() {
        return branchOfficeId;
    }

    public void setBranchOfficeId(Integer branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }

    public Integer getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(Integer loanNumber) {
        this.loanNumber = loanNumber;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getLoanMount() {
        return loanMount;
    }

    public void setLoanMount(Double loanMount) {
        this.loanMount = loanMount;
    }

    public Integer getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(Integer loanTerm) {
        this.loanTerm = loanTerm;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getTreRate() {
        return treRate;
    }

    public void setTreRate(Double treRate) {
        this.treRate = treRate;
    }

    public Double getTeacRate() {
        return teacRate;
    }

    public void setTeacRate(Double teacRate) {
        this.teacRate = teacRate;
    }

    public Double getFeePayment() {
        return feePayment;
    }

    public void setFeePayment(Double feePayment) {
        this.feePayment = feePayment;
    }

    public String getPaymentFrecuency() {
        return paymentFrecuency;
    }

    public void setPaymentFrecuency(String paymentFrecuency) {
        this.paymentFrecuency = paymentFrecuency;
    }

    public Double getCreditLifeInsurance() {
        return creditLifeInsurance;
    }

    public void setCreditLifeInsurance(Double creditLifeInsurance) {
        this.creditLifeInsurance = creditLifeInsurance;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getLoanDestination() {
        return loanDestination;
    }

    public void setLoanDestination(String loanDestination) {
        this.loanDestination = loanDestination;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getIdentityCardDebtor() {
        return identityCardDebtor;
    }

    public void setIdentityCardDebtor(String identityCardDebtor) {
        this.identityCardDebtor = identityCardDebtor;
    }

    public String getAddressDebtor() {
        return addressDebtor;
    }

    public void setAddressDebtor(String addressDebtor) {
        this.addressDebtor = addressDebtor;
    }

    public String getCivilStatusDebtor() {
        return civilStatusDebtor;
    }

    public void setCivilStatusDebtor(String civilStatusDebtor) {
        this.civilStatusDebtor = civilStatusDebtor;
    }

    public String getGenderDebtor() {
        return genderDebtor;
    }

    public void setGenderDebtor(String genderDebtor) {
        this.genderDebtor = genderDebtor;
    }

    public Integer getClientLoanId() {
        return clientLoanId;
    }

    public void setClientLoanId(Integer clientLoanId) {
        this.clientLoanId = clientLoanId;
    }

    public Integer getFixedPaymentDay() {
        return fixedPaymentDay;
    }

    public void setFixedPaymentDay(Integer fixedPaymentDay) {
        this.fixedPaymentDay = fixedPaymentDay;
    }

    public String getCoDebtors() {
        return coDebtors;
    }

    public void setCoDebtors(String coDebtors) {
        this.coDebtors = coDebtors;
    }

    public String getGuarantors() {
        return guarantors;
    }

    public void setGuarantors(String guarantors) {
        this.guarantors = guarantors;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}


