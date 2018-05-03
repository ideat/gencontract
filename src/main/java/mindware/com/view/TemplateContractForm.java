package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.UploadException;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.Parameter;
import mindware.com.service.ParameterService;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TemplateContractForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private Grid<Parameter> gridContracts;
    private TextField txtContractId;
    private TextField txtNameFileContract;
    private TextField txtDescriptionContract;
    private Button btnUpdateContract;
    private Button btnNewContract;
    private Panel panelGridContracts;
    private Upload uploadContract;
    private File fileContract;

    public TemplateContractForm(){

        setCompositionRoot(buildMainLayout());
        postBuild();
    }

    private void postBuild(){
        fillGridContract(getListParameter());
        gridContracts.addItemClickListener(itemClick -> {
           Parameter parameter = itemClick.getItem();
           txtContractId.setValue(parameter.getParameterId().toString());
           txtNameFileContract.setValue(parameter.getValueParameter());
           txtDescriptionContract.setValue(parameter.getDescriptionParameter());
           txtNameFileContract.setEnabled(false);
        });
        btnUpdateContract.addClickListener(clickEvent -> {
           Parameter parameter = new Parameter();
           parameter.setParameterId(Integer.parseInt(txtContractId.getValue()));
           parameter.setDescriptionParameter(txtDescriptionContract.getValue());
           parameter.setValueParameter(txtNameFileContract.getValue());
           parameter.setTypeParameter("contract");
           ParameterService parameterService = new ParameterService();
           parameterService.updateParameter(parameter);
           Notification.show("ACTUALIZAR",
                    "Datos actualizados",
                    Notification.Type.HUMANIZED_MESSAGE);
           fillGridContract(getListParameter());
           clearData();
           txtNameFileContract.setEnabled(true);
        });
        btnNewContract.addClickListener(clickEvent -> {
            clearData();
            txtNameFileContract.setEnabled(true);
            txtNameFileContract.focus();
        });
    }


    private void insertContract(){
        Parameter parameter = new Parameter();
        parameter.setTypeParameter("contract");
        parameter.setValueParameter(txtNameFileContract.getValue());
        parameter.setDescriptionParameter(txtDescriptionContract.getValue());

        ParameterService parameterService = new ParameterService();
        parameterService.insertParameter(parameter);
    }

    private List<Parameter> getListParameter(){
        ParameterService parameterService = new ParameterService();
        return parameterService.findParameterByType("contract");
    }

    private void fillGridContract(List<Parameter> parameterList){
        gridContracts.removeAllColumns();
        gridContracts.setItems(parameterList);
        gridContracts.addColumn(Parameter::getParameterId).setCaption("ID");
        gridContracts.addColumn(Parameter::getValueParameter).setCaption("Nombre contrato");
        gridContracts.addColumn(Parameter::getDescriptionParameter).setCaption("Descripcion");

    }

    private void clearData(){
        txtNameFileContract.clear();
        txtDescriptionContract.clear();
        txtContractId.clear();
    }

    private GridLayout buildMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(6);
        gridMainLayout.setRows(5);
        gridMainLayout.setSizeFull();
        gridMainLayout.setSpacing(true);

        txtContractId = new TextField("ID");
        txtContractId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtContractId.setEnabled(false);
        gridMainLayout.addComponent(txtContractId,0,0);
        gridMainLayout.setComponentAlignment(txtContractId,Alignment.BOTTOM_LEFT);

        txtNameFileContract = new TextField("Nombre contrato:");
        txtNameFileContract.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtNameFileContract.setDescription("Nombre del archivo del contrato a ser guardado");
        txtNameFileContract.setWidth("100%");
        gridMainLayout.addComponent(txtNameFileContract,1,0);
        gridMainLayout.setComponentAlignment(txtNameFileContract,Alignment.BOTTOM_LEFT);

        txtDescriptionContract = new TextField("Descripcion contrato:");
        txtDescriptionContract.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtDescriptionContract.setWidth("100%");
        gridMainLayout.addComponent(txtDescriptionContract,2,0,3,0);
        gridMainLayout.setComponentAlignment(txtDescriptionContract,Alignment.BOTTOM_LEFT);

        btnUpdateContract = new Button("Actualizar");
        btnUpdateContract.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnUpdateContract.setIcon(VaadinIcons.REFRESH);
        gridMainLayout.addComponent(btnUpdateContract,4,0);
        gridMainLayout.setComponentAlignment(btnUpdateContract,Alignment.BOTTOM_LEFT);

        btnNewContract = new Button("Nuevo");
        btnNewContract.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnNewContract.setIcon(VaadinIcons.FILE_TEXT_O);
        gridMainLayout.addComponent(btnNewContract,5,0);
        gridMainLayout.setComponentAlignment(btnNewContract,Alignment.BOTTOM_LEFT);

        uploadContract = new Upload(null, new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String fileName, String mimetype) {
                if (!txtNameFileContract.isEmpty() || !txtDescriptionContract.isEmpty()) {
                    if (!fileName.isEmpty()) {
                        try {

                            String extension = FilenameUtils.getExtension(fileName);
                            if (extension.equals("doc") || extension.equals("docx") || extension.equals("odt")) {
                                fileContract = File.createTempFile(fileName, extension);
                                String path = this.getClass().getClassLoader().getResource("/contract/template").getPath();
                                txtNameFileContract.setValue(txtNameFileContract.getValue()+'.'+extension);
                                fileContract = new File(path, txtNameFileContract.getValue());

                                if (fileContract.exists()) {
                                    Notification.show("ERROR",
                                            "Existe contrato con ese nombre",
                                            Notification.Type.ERROR_MESSAGE);
                                } else {
                                    insertContract();
                                    fillGridContract(getListParameter());
                                    clearData();
                                    return new FileOutputStream(fileContract);
                                }
                            } else {
                                Notification.show("ERROR",
                                        "Formato archivo no valido",
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Notification.show("ERROR",
                            "Ingrese el nombre y descripcion del contrato",
                            Notification.Type.ERROR_MESSAGE);
                    txtNameFileContract.focus();
                }
                return new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {

                    }
                };
            }
        });

        uploadContract.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(Upload.StartedEvent startedEvent) {
                if (startedEvent.getFilename().isEmpty())
                    Notification.show("ARCHIVO",
                            "Seleccione un archivo",
                            Notification.Type.WARNING_MESSAGE);
            }
        });


        uploadContract.addFailedListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(Upload.FailedEvent failedEvent) {

                Notification.show("ERROR",
                        "Error al cargar el archivo",
                        Notification.Type.ERROR_MESSAGE);


            }
        });

        uploadContract.setButtonCaption("Cargar contrato");
        uploadContract.setWidth("100%");
        uploadContract.setImmediateMode(false);

        gridMainLayout.addComponent(uploadContract,0,1,1,1);
        gridMainLayout.setComponentAlignment(uploadContract,Alignment.BOTTOM_LEFT);

        panelGridContracts = new Panel();
        panelGridContracts.setStyleName(ValoTheme.PANEL_WELL);
        panelGridContracts.setSizeFull();
        gridMainLayout.addComponent(panelGridContracts,0,2,5,4);

        gridContracts = new Grid<>();
        gridContracts.setStyleName(ValoTheme.TABLE_COMPACT);
        gridContracts.setSizeFull();
        panelGridContracts.setContent(gridContracts);

        return gridMainLayout;
    }
}
