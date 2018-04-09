package mindware.com.model;

import com.fasterxml.jackson.core.TreeNode;


public class BranchOffice {
    private int branchOfficeId;
    private String branchName;
    private String cityName;
    private String provinceName;
    private String signatories;

    public int getBranchOfficeId() {
        return branchOfficeId;
    }

    public void setBranchOfficeId(int branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSignatories() {
        return signatories;
    }

    public void setSignatories(String signatories) {
        this.signatories = signatories;
    }
}
