package mindware.com.view;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.Signatories;

public class SignatorieWindowForm extends Window {
    private FormLayout formLayout;
    private TextField txtNameSignatorie;
    private TextField txtIdentifyCardSignatorie;
    private TextField txtPosition;
    private ComboBox cmbStatus;
    private Button btnSave;
    private Label lblTitle;

    public Signatories signatorie;


    public SignatorieWindowForm(){
        setContent(buildMainLayout());
        postBuild();
    }

    private void postBuild(){
        btnSave.addClickListener(clickEvent -> {
           saveSignatories();


        });

//        btnClose.addClickListener(clickEvent -> {
//           close();
//        });
    }

    private void saveSignatories(){
        if (validateData()) {
            signatorie = new Signatories();
            signatorie.setNameSignatorie(txtNameSignatorie.getValue());
            signatorie.setIdentifyCardSignatorie(txtIdentifyCardSignatorie.getValue());
            signatorie.setPosition(txtPosition.getValue());
            signatorie.setStatus(cmbStatus.getValue().toString());
            close();
        }else {
            new Notification("Datos incompletos",
                    "Llenar los campos <i>obligatorios</i> ",
                    Notification.Type.ERROR_MESSAGE, true)
                    .show(Page.getCurrent());
        }
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
