package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.Parameter;
import mindware.com.service.ParameterService;

import java.util.List;

public class CustomVariableForm extends CustomComponent implements View {
    private GridLayout mainGridLayout;
    private Panel panelGridVariable;
    private Grid<Parameter> gridVariable;
    private Button btnSaveVariable;
    private Button btnNewVariable;
    private TextField txtIdVariable;
    private TextField txtNameVariable;
    private TextField txtValueVariable;
    private ComboBox cmbTypeVariable;

    public CustomVariableForm(){
        setCompositionRoot(buildMainGridLayout());
        fillGridVariable();
        postBuild();
    }

    private void postBuild(){
        btnNewVariable.addClickListener(clickEvent -> {
           clearFields();
        });

        btnSaveVariable.addClickListener(clickEvent -> {
            try {
                if (validateData()) {
                    if (txtIdVariable.isEmpty()) {
                        if (validateNameVariable()) {
                            new ParameterService().insertParameter(prepareParameter());
                            fillGridVariable();
                            clearFields();
                            Notification.show("Parametro",
                                    "Parametro registrado",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        } else {
                            Notification.show("ERROR",
                                    "Nombre variable ya existe",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    } else {
                        if (validateNameVariable()) {
                            new ParameterService().updateParameter(prepareParameter());
                            fillGridVariable();
                            clearFields();
                            Notification.show("Parametro",
                                    "Parametro actulizado",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        }
                    }

                } else {
                    Notification.show("ERROR",
                            "Datos imcompletos, llene todos",
                            Notification.Type.ERROR_MESSAGE);
                }
            }catch (Exception e){
                Notification.show("ERROR",
                        "Al registrar el parametro " + e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }

        });

        gridVariable.addItemClickListener(itemClick -> {
            fillSelectParameter(itemClick.getItem());
            txtNameVariable.setEnabled(false);
        });
    }

    private void fillSelectParameter(Parameter parameter){
        txtIdVariable.setValue(parameter.getParameterId().toString());
        txtValueVariable.setValue(parameter.getDescriptionParameter());
        txtNameVariable.setValue(parameter.getValueParameter());
    }

    private void fillGridVariable(){
        ParameterService parameterService = new ParameterService();
        List<Parameter> parameterList = parameterService.findParameterByType("custom_variable_contract");
        gridVariable.removeAllColumns();
        gridVariable.setItems(parameterList);
        gridVariable.addColumn(Parameter::getParameterId).setCaption("ID");
        gridVariable.addColumn(Parameter::getValueParameter).setCaption("Nombre variable");
        gridVariable.addColumn(Parameter::getDescriptionParameter).setCaption("Valor variable");
    }

    private Parameter prepareParameter(){
        Parameter parameter = new Parameter();
        parameter.setTypeParameter("custom_variable_contract");
        parameter.setValueParameter(txtNameVariable.getValue());
        parameter.setDescriptionParameter(txtValueVariable.getValue());

        return parameter;
    }

    private boolean validateNameVariable(){
        if (new ParameterService().findParameterByNameAndType("custom_variable_contract",txtNameVariable.getValue())==0)
            return true;
        else
            return false;

    }

    private boolean validateData(){
        if (txtValueVariable.isEmpty()) return false;
        if (txtNameVariable.isEmpty()) return false;
        if (txtValueVariable.isEmpty()) return false;
        return true;
    }

    private void clearFields(){
        txtIdVariable.clear();
        txtNameVariable.clear();
        txtValueVariable.clear();
        txtNameVariable.setEnabled(true);
    }

    private GridLayout buildMainGridLayout(){
        mainGridLayout = new GridLayout();
        mainGridLayout.setColumns(5);
        mainGridLayout.setRows(5);
        mainGridLayout.setSpacing(true);

        txtIdVariable = new TextField("ID:");
        txtIdVariable.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtIdVariable.setEnabled(false);
        mainGridLayout.addComponent(txtIdVariable,0,0);

        txtNameVariable = new TextField("Nombre variable:");
        txtNameVariable.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtNameVariable,1,0);

        txtValueVariable = new TextField("Valor variable:");
        txtValueVariable.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtValueVariable,2,0);

        btnSaveVariable = new Button("Guardar");
        btnSaveVariable.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSaveVariable.setIcon(VaadinIcons.DATABASE);
        mainGridLayout.addComponent(btnSaveVariable,3,0);
        mainGridLayout.setComponentAlignment(btnSaveVariable,Alignment.BOTTOM_LEFT);

        btnNewVariable = new Button("Nueva");
        btnNewVariable.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnNewVariable.setIcon(VaadinIcons.PLUS_CIRCLE);
        mainGridLayout.addComponent(btnNewVariable,4,0);
        mainGridLayout.setComponentAlignment(btnNewVariable,Alignment.BOTTOM_LEFT);

        mainGridLayout.addComponent(builPanelGridVariable(),0,1,4,3);

        return mainGridLayout;
    }

    private Panel builPanelGridVariable(){
        panelGridVariable = new Panel();
        panelGridVariable.setStyleName(ValoTheme.PANEL_WELL);
        panelGridVariable.setSizeFull();

        gridVariable = new Grid<>();
        gridVariable.setStyleName(ValoTheme.TABLE_SMALL);
        gridVariable.setWidth("100%");

        panelGridVariable.setContent(gridVariable);

        return panelGridVariable;
    }



}
