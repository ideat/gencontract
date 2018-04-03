package mindware.com.model;

public class Signatories {
    private String nameSignatorie;
    private String identifyCardSignatorie;
    private String position;
    private String status;


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
