package mindware.com.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.CoDebtorGuarantor;
import mindware.com.netbank.model.CodebtorGuarantorNetbank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodebtorGuarantorWindowForm extends Window {
    private TextField txtDireccion;
    private TextField txtAdyacentes;
    private TextField txtZona;
    private TextField txtCiudad;
    private TextField txtProvincia;
    private ComboBox cmbDepartamento;
    private ComboBox cmbTipoDireccion;
    private TextField txtCivilStatus;
    private TextField txtName;
    private TextField txtNumeroCasa;
    private Label lblTitulo;

    private Button btnSave;
    private Button btnExit;

    private GridLayout mainGridLayout;

    private List<Object> codebtorGuarantors = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();


    public CodebtorGuarantorWindowForm( Object codebtorGuarantorSelected,  List<?> codebtorGuarantorList, String origen) {
        setContent(buildGridMainLayout());

        if (origen.equals("netbank")){
            fillDataNetBank((CodebtorGuarantorNetbank) codebtorGuarantorSelected);

        }else{
            fillData((CoDebtorGuarantor) codebtorGuarantorSelected);

        }

        postBuild(codebtorGuarantorSelected, origen,codebtorGuarantorList);
    }

    private void postBuild(Object codebtorGuarantorSelected, String origen, List<?> codebtorGuarantorList ){

        btnSave.addClickListener(clickEvent ->{
            String result = validateData(cmbTipoDireccion.getValue().toString());
            if (result.equals("OK")){
                if (origen.equals("netbank")) {
                    saveDataNetBank((CodebtorGuarantorNetbank) codebtorGuarantorSelected, (List<CodebtorGuarantorNetbank>) codebtorGuarantorList);


                }else{
                    saveData((CoDebtorGuarantor) codebtorGuarantorSelected, (List<CoDebtorGuarantor>) codebtorGuarantorList);
                }
                close();
            }else {
              new  Notification("Datas incompletos",
                      result,
                      Notification.Type.ERROR_MESSAGE,true)
                      .show(Page.getCurrent());
            }
        });

        btnExit.addClickListener(clickEvent ->{
           close();
        });

        cmbTipoDireccion.addValueChangeListener(value -> {

        });
    }


    private void updateListCodeptorguarantor(Object codebtorGuarantor, List<?> codebtorGuarantorList){

        for (Object cg : codebtorGuarantorList){

        }

    }

    private void saveDataNetBank(CodebtorGuarantorNetbank codebtorGuarantorNetbank, List<CodebtorGuarantorNetbank> codebtorGuarantorNetbankList){
        codebtorGuarantorNetbank.setGbagenomb(txtName.getValue());
        codebtorGuarantorNetbank.setGbageeciv(txtCivilStatus.getValue());
        codebtorGuarantorNetbank.setTipoDireccion(cmbTipoDireccion.getValue().toString());
        codebtorGuarantorNetbank.setGbagedir(txtDireccion.getValue());
        codebtorGuarantorNetbank.setNumeroCasa(txtNumeroCasa.getValue());
        codebtorGuarantorNetbank.setAdyacentes(txtAdyacentes.getValue()==null?"":txtAdyacentes.getValue());
        codebtorGuarantorNetbank.setZona(txtZona.getValue());
        codebtorGuarantorNetbank.setCiudad(txtCiudad.getValue());
        codebtorGuarantorNetbank.setProvincia(txtProvincia.getValue());
        codebtorGuarantorNetbank.setDepartamento(cmbDepartamento.getValue().toString());

        List<CodebtorGuarantorNetbank> aux = new ArrayList<>();
        for (CodebtorGuarantorNetbank cgn : codebtorGuarantorNetbankList){
            if (!cgn.getPrdeucage().equals(codebtorGuarantorNetbank.getPrdeucage()))
                aux.add(cgn);

        }
        codebtorGuarantorNetbankList = aux;
        codebtorGuarantorNetbankList.add(codebtorGuarantorNetbank);
    }

    private void saveData(CoDebtorGuarantor coDebtorGuarantor , List<CoDebtorGuarantor> coDebtorGuarantorList){
        coDebtorGuarantor.setName(txtName.getValue());
        coDebtorGuarantor.setCivilStatus(txtCivilStatus.getValue());
        coDebtorGuarantor.setTipoDireccion(cmbTipoDireccion.getValue().toString());
        coDebtorGuarantor.setAddressHome(txtDireccion.getValue());
        coDebtorGuarantor.setNumeroCasa(txtNumeroCasa.getValue());
        coDebtorGuarantor.setAdyacentes(txtAdyacentes.getValue()==null?"":txtAdyacentes.getValue());
        coDebtorGuarantor.setZona(txtZona.getValue());
        coDebtorGuarantor.setProvincia(txtProvincia.getValue());
        coDebtorGuarantor.setCiudad(txtCiudad.getValue());
        coDebtorGuarantor.setDepartamento(cmbDepartamento.getValue().toString());

        List<CoDebtorGuarantor> aux = new ArrayList<>();
        for (CoDebtorGuarantor cg : coDebtorGuarantorList){
            if (!cg.getCodeMebership().equals(coDebtorGuarantor.getCodeMebership())){
                aux.add(cg);
            }
        }

        coDebtorGuarantorList = aux;
        coDebtorGuarantorList.add(coDebtorGuarantor);
    }

    private void fillDataNetBank(CodebtorGuarantorNetbank codebtorGuarantorNetbank){
        txtName.setValue(codebtorGuarantorNetbank.getGbagenomb());
        txtCivilStatus.setValue(codebtorGuarantorNetbank.getGbageeciv());
        cmbTipoDireccion.setValue(codebtorGuarantorNetbank.getTipoDireccion());
        txtNumeroCasa.setValue(codebtorGuarantorNetbank.getNumeroCasa());
        txtDireccion.setValue(codebtorGuarantorNetbank.getGbagedir());
        txtAdyacentes.setValue(codebtorGuarantorNetbank.getAdyacentes());
        txtZona.setValue(codebtorGuarantorNetbank.getZona());
        txtCiudad.setValue(codebtorGuarantorNetbank.getCiudad());
        txtProvincia.setValue(codebtorGuarantorNetbank.getProvincia());
        cmbDepartamento.setValue(codebtorGuarantorNetbank.getDepartamento());
    }

    private void fillData(CoDebtorGuarantor coDebtorGuarantor){
        txtName.setValue(coDebtorGuarantor.getName() == null ? "" : String.valueOf(coDebtorGuarantor.getName()));
        txtCivilStatus.setValue(coDebtorGuarantor.getCivilStatus() == null ? "":String.valueOf(coDebtorGuarantor.getCivilStatus()));
        cmbTipoDireccion.setValue(coDebtorGuarantor.getTipoDireccion() == null ? "" :String.valueOf(coDebtorGuarantor.getTipoDireccion()));
        txtDireccion.setValue(coDebtorGuarantor.getAddressHome() == null ? "": String.valueOf(coDebtorGuarantor.getAddressHome()) );
        txtNumeroCasa.setValue(coDebtorGuarantor.getNumeroCasa() == null ? "":String.valueOf(coDebtorGuarantor.getNumeroCasa()));
        txtAdyacentes.setValue(coDebtorGuarantor.getAdyacentes()==null ? "":String.valueOf(coDebtorGuarantor.getAdyacentes()));
        txtZona.setValue(coDebtorGuarantor.getZona() == null ? "":String.valueOf(coDebtorGuarantor.getZona()));
        txtCiudad.setValue(coDebtorGuarantor.getCiudad()== null ? "":String.valueOf(coDebtorGuarantor.getCiudad()));
        txtProvincia.setValue(coDebtorGuarantor.getProvincia()==null ? "":String.valueOf(coDebtorGuarantor.getProvincia()));
        cmbDepartamento.setValue(coDebtorGuarantor.getDepartamento()==null ? "":String.valueOf(coDebtorGuarantor.getDepartamento()));
    }

    private String validateData(String tipoDireccion){
        if (txtCivilStatus.isEmpty()) return "Estado civil no puede ser omitido";
        if (cmbTipoDireccion.isEmpty() || cmbTipoDireccion.getValue().equals("")) return "Tipo de direccion, no puede omitirse";
        if (txtDireccion.isEmpty()) return "Calle de la direccion no puede ser omitida";
        if (txtNumeroCasa.isEmpty()) return "Nro de docicilio no puede ser omitido";
//        if (txtAdyacentes.isEmpty()) return "Adyacentes no puede se omitida";
        if (txtZona.isEmpty()) return "Zona no puede ser omitida";
        if (txtCiudad.isEmpty()) return "Ciudad no puede ser omitida";
        if (tipoDireccion.equals("RURAL"))
            if (txtProvincia.isEmpty()) return  "Provincia no puede ser omitida";
        else
            if (txtProvincia.isEmpty()) txtProvincia.setValue("");
        if (cmbDepartamento.isEmpty()) return "Departamento no puede ser omitido";
        return "OK";
    }

    private GridLayout buildGridMainLayout(){
        mainGridLayout = new GridLayout();
        mainGridLayout.setColumns(3);
        mainGridLayout.setRows(5);
        mainGridLayout.setWidth("100%");
        mainGridLayout.setSpacing(true);
        mainGridLayout.setMargin(true);

        txtName = new TextField("Nombre completo:");
        txtName.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtName.setReadOnly(true);
        mainGridLayout.addComponent(txtName,0,0);

        txtCivilStatus = new TextField("Estado civil:");
        txtCivilStatus.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtCivilStatus,1,0);

        cmbTipoDireccion = new ComboBox("Tipo direccion:");
        cmbTipoDireccion.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbTipoDireccion.setItems("RURAL","URBANA");
        cmbTipoDireccion.setEmptySelectionAllowed(false);
        cmbTipoDireccion.setRequiredIndicatorVisible(true);
        mainGridLayout.addComponent(cmbTipoDireccion,2,0);


        txtDireccion = new TextField("Calle:");
        txtDireccion.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtDireccion.setRequiredIndicatorVisible(true);

        mainGridLayout.addComponent(txtDireccion,0,1);

        txtNumeroCasa = new TextField("Nro. domicilio");
        txtNumeroCasa.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtNumeroCasa.setRequiredIndicatorVisible(true);
        mainGridLayout.addComponent(txtNumeroCasa,1,1);

        txtAdyacentes = new TextField("Adyacentes:");
        txtAdyacentes.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtAdyacentes.setRequiredIndicatorVisible(false);
        mainGridLayout.addComponent(txtAdyacentes,2,1);

        txtZona = new TextField("Zona/Barrio:");
        txtZona.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtZona.setRequiredIndicatorVisible(true);
        mainGridLayout.addComponent(txtZona,0,2);

        txtCiudad = new TextField("Ciudad/Localidad:");
        txtCiudad.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCiudad.setRequiredIndicatorVisible(true);
        mainGridLayout.addComponent(txtCiudad,1,2);

        txtProvincia = new TextField("Provincia:");
        txtProvincia.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainGridLayout.addComponent(txtProvincia,2,2);

        cmbDepartamento = new ComboBox("Departamento:");
        cmbDepartamento.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbDepartamento.setEmptySelectionAllowed(false);
        cmbDepartamento.setRequiredIndicatorVisible(true);
        cmbDepartamento.setItems("LA PAZ","SANTA CRUZ", "COCHABAMBA",
                "ORURO","POTOSI","TARIJA","CHUQUISACA","PANDO","BENI");
        mainGridLayout.addComponent(cmbDepartamento,0,3);

        btnSave = new Button("Guardar");
        btnSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSave.setIcon(VaadinIcons.STORAGE);
        mainGridLayout.addComponent(btnSave,0,4);

        btnExit = new Button("Salir");
        btnExit.setStyleName(ValoTheme.BUTTON_DANGER);
        btnExit.setIcon(VaadinIcons.EXIT);
        mainGridLayout.addComponent(btnExit,1,4);

        return mainGridLayout;

    }


}
