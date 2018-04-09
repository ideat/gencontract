package mindware.com.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.BranchOffice;
import mindware.com.model.Signatories;
import mindware.com.service.BranchOfficeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignatorieWindowForm extends Window {
    private FormLayout formLayout;
    private TextField txtSignatorieId;
    private TextField txtNameSignatorie;
    private TextField txtIdentifyCardSignatorie;
    private TextField txtPosition;
    private ComboBox cmbStatus;
    private Button btnSave;
    private Label lblTitle;

    public Signatories signatorie;


    public SignatorieWindowForm(int branOfficeId, String signatories, Signatories signatorieSelected, String task){
        setContent(buildMainLayout());
        postBuild(branOfficeId,signatories, signatorieSelected,task);
        if (task.equals("EDIT")) fillSignatorie(signatorieSelected);
    }

    private void postBuild(int branOfficeId, String signatories, Signatories signatorieSelected, String task){
        btnSave.addClickListener(clickEvent -> {
            try {
                if (task.equals("INSERT")) {
                    saveSignatories(branOfficeId, signatories, task);
                }
                else {
                    saveSignatories(branOfficeId,signatoriesWithOutSelected(signatories,signatorieSelected),task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void fillSignatorie(Signatories signatories){
        txtSignatorieId.setValue(signatories.getSignatorieId().toString());
        txtNameSignatorie.setValue(signatories.getNameSignatorie());
        txtIdentifyCardSignatorie.setValue(signatories.getIdentifyCardSignatorie());
        cmbStatus.setValue(signatories.getStatus());
        txtPosition.setValue(signatories.getPosition());
    }



    private void saveSignatories(int branOfficeId, String currentSignatories, String task) throws IOException {
        if (validateData()) {
            signatorie = new Signatories();
            signatorie.setNameSignatorie(txtNameSignatorie.getValue());
            signatorie.setIdentifyCardSignatorie(txtIdentifyCardSignatorie.getValue());
            signatorie.setPosition(txtPosition.getValue());
            signatorie.setStatus(cmbStatus.getValue().toString());
            List<Signatories> signatoriesList = new ArrayList<>();

            ObjectMapper mapper = new ObjectMapper();
            String jsonSignatories=null;


            if (!currentSignatories.equals("[]")) {
                List<Signatories> auxList = Arrays.asList(mapper.readValue(currentSignatories, Signatories[].class));
                if (task.equals("INSERT"))
                    signatorie.setSignatorieId(auxList.size()+1);
                else
                    signatorie.setSignatorieId(Integer.parseInt(txtSignatorieId.getValue()));

                for(Signatories signatories:auxList){
                     signatoriesList.add(signatories);
                }
            }else {
                signatorie.setSignatorieId(1);
            }


            signatoriesList.add(signatorie);
            jsonSignatories = mapper.writeValueAsString(signatoriesList);

            BranchOffice branchOffice = new BranchOffice();
            branchOffice.setBranchOfficeId(branOfficeId);
            branchOffice.setSignatories(jsonSignatories);
            BranchOfficeService branchOfficeService = new BranchOfficeService();
            branchOfficeService.updateSignatoriesBranchOffice(branchOffice);

            close();
        }else {
            new Notification("Datos incompletos",
                    "Llenar los campos <i>obligatorios</i> ",
                    Notification.Type.ERROR_MESSAGE, true)
                    .show(Page.getCurrent());
        }
    }

    private String signatoriesWithOutSelected(String currentSignatories, Signatories signaturieSelected){
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Signatories> auxList = Arrays.asList(mapper.readValue(currentSignatories, Signatories[].class));
            List<Signatories> signatoriesList = new ArrayList<>();
            for(Signatories signatorie:auxList){
                if (!signatorie.getSignatorieId().equals(signaturieSelected.getSignatorieId()))
                    signatoriesList.add(signatorie);
            }
//            signatoriesList.removeIf(b -> b.equals(signaturieSelected));
//            for(Signatories signatorie:signatoriesList){
//                if(signatorie.getSignatorieId().equals(signaturieSelected.getSignatorieId()))
//                    signatoriesList.remove(signatorie);
//            }

            return mapper.writeValueAsString(signatoriesList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private boolean validateData(){
        if (txtNameSignatorie.isEmpty()) return false;
        if (txtIdentifyCardSignatorie.isEmpty()) return false;
        if (txtPosition.isEmpty()) return false;
        if (cmbStatus.isEmpty()) return false;
        return true;
    }

    private FormLayout buildMainLayout(){
        formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        lblTitle = new Label("Datos firmante");
        lblTitle.setStyleName(ValoTheme.LABEL_COLORED);
        lblTitle.setStyleName(ValoTheme.LABEL_H2,true);
        formLayout.addComponent(lblTitle);

        txtSignatorieId = new TextField("ID:");
        txtSignatorieId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtSignatorieId.setEnabled(false);
        formLayout.addComponent(txtSignatorieId);

        txtNameSignatorie = new TextField("Nombre completo:");
        txtNameSignatorie.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtNameSignatorie.setRequiredIndicatorVisible(true);
        formLayout.addComponent(txtNameSignatorie);

        txtIdentifyCardSignatorie = new TextField("Carnet:");
        txtIdentifyCardSignatorie.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtIdentifyCardSignatorie.setRequiredIndicatorVisible(true);
        formLayout.addComponent(txtIdentifyCardSignatorie);

        txtPosition = new TextField("Cargo:");
        txtPosition.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtPosition.setRequiredIndicatorVisible(true);
        formLayout.addComponent(txtPosition);

        cmbStatus = new ComboBox("Estado");
        cmbStatus.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbStatus.setItems("ACTIVO","BAJA");
        cmbStatus.setRequiredIndicatorVisible(true);
        cmbStatus.setEmptySelectionAllowed(false);
        formLayout.addComponent(cmbStatus);

        btnSave = new Button("Guardar");
        btnSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
        formLayout.addComponent(btnSave);

        return formLayout;
    }

}
