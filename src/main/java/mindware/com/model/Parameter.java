package mindware.com.model;

public class Parameter {
    private Integer parameterId;
    private String typeParameter;
    private String descriptionParameter;
    private String valueParameter;

    public Integer getParameterId() {
        return parameterId;
    }

    public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }

    public String getTypeParameter() {
        return typeParameter;
    }

    public void setTypeParameter(String typeParameter) {
        this.typeParameter = typeParameter;
    }

    public String getDescriptionParameter() {
        return descriptionParameter;
    }

    public void setDescriptionParameter(String descriptionParameter) {
        this.descriptionParameter = descriptionParameter;
    }

    public String getValueParameter() {
        return valueParameter;
    }

    public void setValueParameter(String valueParameter) {
        this.valueParameter = valueParameter;
    }
}
