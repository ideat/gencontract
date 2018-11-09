package mindware.com.model;

public class Signatories {
    private Integer signatorieId;
    private String nameSignatorie;
    private String identifyCardSignatorie;
    private String position;
    private String status;
    private String nroPoder;
    private String fechaPoder;
    private String nroNotaria;
    private String nombreNotario;
    private String distritoJudicial;
    private String nroTestimonio;
    private String fechaTestimonio;
    private String priority;

    public Signatories(int signatorieId, String nameSignatorie, String identifyCardSignatorie, String position, String status
    , String nroPoder, String fechaPoder, String nombreNotario, String distritoJudicial, String nroTestimonio, String fechaTestimonio
    , String priority){
        this.nameSignatorie = nameSignatorie;
        this.identifyCardSignatorie = identifyCardSignatorie;
        this.position = position;
        this.status = status;
        this.signatorieId = signatorieId;
        this.nroPoder = nroPoder;
        this.fechaPoder = fechaPoder;
        this.nombreNotario = nombreNotario;
        this.distritoJudicial = distritoJudicial;
        this.nroTestimonio = nroTestimonio;
        this.fechaTestimonio = fechaTestimonio;
        this.priority = priority;
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

    public String getNroPoder() {
        return nroPoder;
    }

    public void setNroPoder(String nroPoder) {
        this.nroPoder = nroPoder;
    }

    public String getFechaPoder() {
        return fechaPoder;
    }

    public void setFechaPoder(String fechaPoder) {
        this.fechaPoder = fechaPoder;
    }

    public String getNroNotaria() {
        return nroNotaria;
    }

    public void setNroNotaria(String nroNotaria) {
        this.nroNotaria = nroNotaria;
    }

    public String getNombreNotario() {
        return nombreNotario;
    }

    public void setNombreNotario(String nombreNotario) {
        this.nombreNotario = nombreNotario;
    }

    public String getDistritoJudicial() {
        return distritoJudicial;
    }

    public void setDistritoJudicial(String distritoJudicial) {
        this.distritoJudicial = distritoJudicial;
    }

    public String getNroTestimonio() {
        return nroTestimonio;
    }

    public void setNroTestimonio(String nroTestimonio) {
        this.nroTestimonio = nroTestimonio;
    }

    public String getFechaTestimonio() {
        return fechaTestimonio;
    }

    public void setFechaTestimonio(String fechaTestimonio) {
        this.fechaTestimonio = fechaTestimonio;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
