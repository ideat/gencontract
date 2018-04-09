package mindware.com.model;

public class Signatories {
    private Integer signatorieId;
    private String nameSignatorie;
    private String identifyCardSignatorie;
    private String position;
    private String status;


    public Signatories(int signatorieId, String nameSignatorie, String identifyCardSignatorie, String position, String status ){
        this.nameSignatorie = nameSignatorie;
        this.identifyCardSignatorie = identifyCardSignatorie;
        this.position = position;
        this.status = status;
        this.signatorieId = signatorieId;
    }

    public Signatories(){}

    public Integer getSignatorieId() {
        return signatorieId;
    }

    public void setSignatorieId(Integer signatorieId) {
        this.signatorieId = signatorieId;
    }

    public String getNameSignatorie() {
        return nameSignatorie;
    }

    public void setNameSignatorie(String nameSignatorie) {
        this.nameSignatorie = nameSignatorie;
    }

    public String getIdentifyCardSignatorie() {
        return identifyCardSignatorie;
    }

    public void setIdentifyCardSignatorie(String identifyCardSignatorie) {
        this.identifyCardSignatorie = identifyCardSignatorie;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
