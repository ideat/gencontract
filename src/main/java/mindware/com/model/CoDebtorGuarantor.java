package mindware.com.model;

public class CoDebtorGuarantor implements Comparable<CoDebtorGuarantor>{
    private String name;
    private String addressHome;
    private String addressOffice;
    private String identifyCard;
    private String gender;
    private String civilStatus;
    private Integer numberLoan;
    private String type;
    private Integer codeMebership;
    private Integer id;
    private String insured;
    private Integer prioridad;

    public String getInsured() {
        return insured;
    }

    public void setInsured(String insured) {
        this.insured = insured;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressHome() {
        return addressHome;
    }

    public void setAddressHome(String addressHome) {
        this.addressHome = addressHome;
    }

    public String getAddressOffice() {
        return addressOffice;
    }

    public void setAddressOffice(String addressOffice) {
        this.addressOffice = addressOffice;
    }

    public String getIdentifyCard() {
        return identifyCard;
    }

    public void setIdentifyCard(String identifyCard) {
        this.identifyCard = identifyCard;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public Integer getNumberLoan() {
        return numberLoan;
    }

    public void setNumberLoan(Integer numberLoan) {
        this.numberLoan = numberLoan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCodeMebership() {
        return codeMebership;
    }

    public void setCodeMebership(Integer codeMebership) {
        this.codeMebership = codeMebership;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    @Override
    public int compareTo(CoDebtorGuarantor o) {
        if (getPrioridad() == null || o.getPrioridad() == null) {
            return 0;
        }
        return getPrioridad().compareTo(o.getPrioridad());
    }
}
