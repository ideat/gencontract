package mindware.com.model;

public class BranchUser {
    private Integer branchUserId;
    private Integer branchOfficeId;
    private Integer rolViewContractId;
    private String city;
    private BranchOffice branchOffice;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getBranchUserId() {
        return branchUserId;
    }

    public void setBranchUserId(Integer branchUserId) {
        this.branchUserId = branchUserId;
    }

    public Integer getBranchOfficeId() {
        return branchOfficeId;
    }

    public void setBranchOfficeId(Integer branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }

    public Integer getRolViewContractId() {
        return rolViewContractId;
    }

    public void setRolViewContractId(Integer rolViewContractId) {
        this.rolViewContractId = rolViewContractId;
    }

    public BranchOffice getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(BranchOffice branchOffice) {
        this.branchOffice = branchOffice;
    }
}
