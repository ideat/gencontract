package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.LoanData;
import mindware.com.model.Parameter;
import mindware.com.service.ParameterService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariablesContractsForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private Panel panelGridVariable;
    private TextField txtVariableId;
    private ComboBox cmbObject;
    private TextField txtVariable;
    private TextField txtDescription;
    private Button btnAddVariable;
    private Button btnNewVariable;

    private Grid<Parameter> gridVariable;


    public VariablesContractsForm(){

        setCompositionRoot(buildMainLayout());
        LoanData loanData = new LoanData();
        List<Field> fieldList = getFields(loanData);
        fillComboObject(fieldList);
        fillGridVariable();
        postBuild();

    }

    private void postBuild(){
        cmbObject.addValueChangeListener(valueChangeEvent -> {
           txtVariable.setValue(valueChangeEvent.getValue().toString());
        });

        btnAddVariable.addClickListener(clickEvent -> {
            if (txtVariableId.isEmpty())
                insertParameter();
            else{
                Parameter parameter = new Parameter();
                parameter.setValueParameter(txtVariable.getValue());
                parameter.setDescriptionParameter(txtDescription.getValue());
                parameter.setTypeParameter("variable_contract");
                parameter.setParameterId(Integer.parseInt(txtVariableId.getValue()));
                updateParameter(parameter);
            }
        });

        gridVariable.addItemClickListener(itemClick -> {
            Parameter parameterVariable = itemClick.getItem();
            fillParameterSelected(parameterVariable);
        });

        btnNewVariable.addClickListener(clickEvent -> {
            clearFields();
        });
    }


    private void clearFields(){
        txtVariableId.clear();
        txtVariable.clear();
        txtDescription.clear();
    }

    private void fillParameterSelected(Parameter parameterVariable){
        txtVariableId.setValue(parameterVariable.getParameterId().toString());
        txtVariable.setValue(parameterVariable.getValueParameter());
        txtDescription.setValue(parameterVariable.getDescriptionParameter());

    }

    private void updateParameter(Parameter parameter){
        if (validateData()) {
//            if (validateVariable(txtVariable.getValue())) {
                ParameterService parameterService = new ParameterService();
                parameterService.updateParameter(parameter);
                fillGridVariable();
//            }else {
//                Notification.show("ERROR",
//                        "Formato del valor de la variable incorrecto, formato valido #valor#",
//                        Notification.Type.ERROR_MESSAGE);
//            }
        }else {
            Notification.show("ERROR",
                    "Datos incompeltos, complete la informacion",
                    Notification.Type.ERROR_MESSAGE);
        }
    }

    private void insertParameter() {
        if (validateData()) {
//            if (validateVariable(txtVariable.getValue())) {
                ParameterService parameterService = new ParameterService();
                parameterService.insertParameter(prepareInsertParameter());
                fillGridVariable();
                Notification.show("Variables contrato",
                        "Insertada la nueva variable",
                        Notification.Type.HUMANIZED_MESSAGE);
//            }else{
//                Notification.show("ERROR",
//                        "Formato del valor de la variable incorrecto, formato valido #valor#",
//                        Notification.Type.ERROR_MESSAGE);
//            }
        }else{
            Notification.show("ERROR",
                    "Datos incompeltos, complete la informacion",
                    Notification.Type.ERROR_MESSAGE);
        }
    }


    private void fillGridVariable(){
        ParameterService parameterService = new ParameterService();
        List<Parameter> parameterList = parameterService.findParameterByType("variable_contract");

        gridVariable.removeAllColumns();
        gridVariable.setItems(parameterList);
        gridVariable.addColumn(Parameter::getParameterId).setCaption("ID");
        gridVariable.addColumn(Parameter::getValueParameter).setCaption("Variable");
        gridVariable.addColumn(Parameter::getDescriptionParameter).setCaption("Descripcion");

    }

//    private boolean validateVariable(String variable){
//        if (variable.startsWith("#") && (variable.endsWith("#"))) return true;
//        return false;
//    }

    private boolean validateData(){
        if (txtVariable.isEmpty()) return false;
        if (txtDescription.isEmpty()) return false;
        return true;
    }

    private Parameter prepareInsertParameter(){
        Parameter parameter = new Parameter();
        parameter.setTypeParameter("variable_contract");
        parameter.setDescriptionParameter(txtDescription.getValue());
        parameter.setValueParameter(txtVariable.getValue());
        return parameter;
    }

    private void fillComboObject(List<Field> fieldList){
        List<String> listName = new ArrayList<>();
        for (Field field : fieldList){
            listName.add(field.getName());
        }
        cmbObject.setItems(listName);
    }

    private List<Field> getFields(LoanData loanData){
        return Arrays.asList(loanData.getClass().getDeclaredFields());
    }

    private GridLayout buildMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setRows(5);
        gridMainLayout.setColumns(6);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setWidth("100%");

        txtVariableId = new TextField("ID:");
        txtVariableId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtVariableId.setEnabled(false);
        gridMainLayout.addComponent(txtVariableId,0,0);

        cmbObject = new ComboBox("Lista objetos:");
        cmbObject.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbObject.setEmptySelectionAllowed(false);
        gridMainLayout.addComponent(cmbObject,0,1);
        gridMainLayout.setComponentAlignment(cmbObject,Alignment.BOTTOM_LEFT);

        txtVariable = new TextField("Variable:");
        txtVariable.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridMainLayout.addComponent(txtVariable,1,1);
        gridMainLayout.setComponentAlignment(txtVariable,Alignment.BOTTOM_LEFT);

        txtDescription = new TextField("Descripcion de la variable");
        txtDescription.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtDescription.setWidth("100%");
        gridMainLayout.addComponent(txtDescription,2,1,3,1);
        gridMainLayout.setComponentAlignment(txtDescription,Alignment.BOTTOM_LEFT);

        btnAddVariable = new Button("Guardar");
        btnAddVariable.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnAddVariable.setIcon(VaadinIcons.DATABASE);
        gridMainLayout.addComponent(btnAddVariable,4,1);
        gridMainLayout.setComponentAlignment(btnAddVariable,Alignment.BOTTOM_LEFT);

        btnNewVariable = new Button("Nueva");
        btnNewVariable.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnNewVariable.setIcon(VaadinIcons.FILE_START);
        gridMainLayout.addComponent(btnNewVariable,5,1);
        gridMainLayout.setComponentAlignment(btnNewVariable,Alignment.BOTTOM_LEFT);

        panelGridVariable = new Panel();
        panelGridVariable.setStyleName(ValoTheme.PANEL_WELL);
        panelGridVariable.setWidth("100%");

        gridVariable = new Grid();
        gridVariable.setStyleName(ValoTheme.TABLE_COMPACT);
        gridVariable.setSizeFull();
        panelGridVariable.setContent(gridVariable);
        
        gridMainLayout.addComponent(panelGridVariable,0,2,5,4);




        return gridMainLayout;
    }

}
