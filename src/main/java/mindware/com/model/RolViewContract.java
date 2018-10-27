package mindware.com.model;

import java.util.List;

public class RolViewContract {
    private Integer rolViewContractId;
    private String rolViewContractName;
    private String description;
    private List<BranchUser> branchUsers;

    public String getRolViewContractName() {
        return rolViewContractName;
    }

    public void setRolViewContractName(String rolViewContractName) {
        this.rolViewContractName = rolViewContractName;
    }

    public Integer getRolViewContractId() {
        return rolViewContractId;
    }

    public void setRolViewContractId(Integer rolViewContractId) {
        this.rolViewContractId = rolViewContractId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BranchUser> getBranchUsers() {
        return branchUsers;
    }

    public void setBranchUsers(List<BranchUser> branchUsers) {
        this.branchUsers = branchUsers;
    }
}
