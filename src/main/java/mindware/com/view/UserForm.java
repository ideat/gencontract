package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.navigator.View;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.Rol;
import mindware.com.model.RolViewContract;
import mindware.com.model.User;
import mindware.com.security.Encript;
import mindware.com.service.RolService;
import mindware.com.service.RolViewContractService;
import mindware.com.service.UserService;
import org.apache.poi.hssf.record.UserSViewEnd;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class UserForm extends CustomComponent implements View{
    private GridLayout gridMainLayout;
    private Panel panelGridUser;
    private Panel panelUser;
    private Button btnSaveUser;
    private Button btnNewUser;
    private Grid<User> gridUser;
    private TextField txtUserId;
    private TextField txtNameUser;
    private TextField txtLogin;
    private PasswordField txtPassword;
    private RadioButtonGroup btnGroupStateUser;
    private ComboBox<Rol> cmbRol;
    private CheckBox chkResetPassword;
    private ComboBox<RolViewContract> cmbRolViewContract;
    private GridCellFilter<User> filterUser;


    public UserForm(){
        setCompositionRoot(buildGridMainLayout());
        fillRol();
        fillGridUser();
        fillRolViewContract();
        postBuild();

    }

    private void postBuild(){
        btnSaveUser.addClickListener(clickEvent -> {
            if (txtUserId.isEmpty()) {
                insertUser();
                fillGridUser();

            }else{
                updateUser();
                fillGridUser();
            }
        });

        cmbRol.addValueChangeListener(valueChangeEvent -> {

            txtLogin.focus();
        });

        gridUser.addItemClickListener(itemClick -> {
           User user = itemClick.getItem();
           txtUserId.setValue(user.getUserId().toString());
           txtNameUser.setValue(user.getNameUser());
           txtLogin.setValue(user.getLogin());
           txtPassword.setValue(user.getPassword());
           btnGroupStateUser.setValue(user.getState());
           cmbRol.setValue(user.getRol());
           cmbRolViewContract.setValue(user.getRolViewContract());
           txtPassword.setEnabled(false);
           txtLogin.setEnabled(false);
        });

        btnNewUser.addClickListener(clickEvent -> clearFields());

    }

    private void updateUser(){
        try {
            if (validateData()) {
                User user = prepareUser();
                UserService userService = new UserService();
                userService.updateUser(user);
                Notification.show("Usuario",
                        "Datos actualizados",
                        Notification.Type.HUMANIZED_MESSAGE);
            }
        }catch (Exception e){
            Notification.show("Usuario",
                    "Error al actualizar datos: " + e.getMessage(),
                    Notification.Type.ERROR_MESSAGE);
        }
    }

    private void insertUser(){
        try {
            if (validateData()) {
                if (validateLogin()) {
                    User user = prepareUser();
                    UserService userService = new UserService();
                    userService.insertUser(user);
                    clearFields();
                    Notification.show("Usuario",
                            "Nuevo usuario registrado",
                            Notification.Type.HUMANIZED_MESSAGE);
                } else {
                    Notification.show("LOGIN",
                            "Login ya esta registrado, ingrese otro",
                            Notification.Type.ERROR_MESSAGE);
                    txtLogin.focus();
                }

            } else {
                Notification.show("DATOS USUARIOS",
                        "Completar los datos del usuario",
                        Notification.Type.ERROR_MESSAGE);
            }
        }catch (Exception e){
            Notification.show("Usuario",
                    "Error al crear usuario: " + e.getMessage(),
                    Notification.Type.ERROR_MESSAGE);
        }

    }

    private User prepareUser() {
        Encript encript = new Encript();
        User user = new User();
        user.setNameUser(txtNameUser.getValue());
        user.setLogin(txtLogin.getValue());
        user.setRolId(cmbRol.getValue().getRolId());
        user.setState(btnGroupStateUser.getValue().toString());
        user.setRolViewContractId(cmbRolViewContract.getValue().getRolViewContractId());
        if (txtUserId.isEmpty())
            user.setPassword(encript.encriptString(txtPassword.getValue()));
        else {
            if (chkResetPassword.getValue().equals(true))
                user.setPassword(encript.encriptString("password"));
            else
                user.setPassword(txtPassword.getValue());
            user.setUserId(Integer.parseInt(txtUserId.getValue()));
        }

        return user;
    }

    private void clearFields(){
        txtUserId.clear();
        txtLogin.clear();
        txtPassword.clear();
        txtNameUser.clear();
        btnGroupStateUser.clear();
        chkResetPassword.clear();
        txtPassword.setEnabled(true);
        txtLogin.setEnabled(true);
        cmbRol.clear();
        cmbRolViewContract.clear();
    }

    private void fillGridUser(){
        if (gridUser.getHeaderRowCount()>1)
            gridUser.removeHeaderRow(1);
        gridUser.removeAllColumns();
        UserService userService = new UserService();
        List<User> userList = userService.findAllUser();
        gridUser.setItems(userList);
        gridUser.addColumn(User::getUserId).setCaption("ID");
        gridUser.addColumn(User::getNameUser).setCaption("Nombre usuario").setId("nameUser");
        gridUser.addColumn(User::getLogin).setCaption("Login").setId("login");
        gridUser.addColumn(User::getPassword).setCaption("Password").setHidden(true);
        gridUser.addColumn(User::getState).setCaption("Estado").setId("state");
        gridUser.addColumn(User::getRol).setCaption("Rol").setHidden(true);
        gridUser.addColumn(User::getRolViewContract).setCaption("Ambito contrato").setHidden(true);

        fillGridFilterUser(gridUser);
    }

    private void fillGridFilterUser(final Grid grid){
        this.filterUser = new GridCellFilter<>(grid);

        this.filterUser.setTextFilter("nameUser", true,false);
        this.filterUser.setTextFilter("login", true, false);
        this.filterUser.setTextFilter("state", true, false);
//        this.filterUser.setTextFilter("rol", true, false);
//        this.filterUser.setTextFilter("rolViewContract", true, false);

    }

    private void fillRol(){
        RolService rolService = new RolService();
        List<Rol> rolList = rolService.findAllRol();
        cmbRol.setItems(rolList);
        cmbRol.setItemCaptionGenerator(Rol::getRolName);
    }

    private void fillRolViewContract(){
        RolViewContractService rolViewContractService = new RolViewContractService();
        List<RolViewContract> rolViewContractList = rolViewContractService.findAllRolViewContract();
        cmbRolViewContract.setItems(rolViewContractList);
        cmbRolViewContract.setItemCaptionGenerator(RolViewContract::getRolViewContractName);
    }

    private boolean validateData(){
        if (txtNameUser.isEmpty()) return false;
        if (txtLogin.isEmpty()) return false;
        if (txtPassword.isEmpty()) return false;
        if (cmbRol.isEmpty()) return false;
        if (btnGroupStateUser.isEmpty()) return false;
        if (cmbRolViewContract.isEmpty()) return false;
        return true;
    }

    private boolean validateLogin(){
        UserService userService = new UserService();
        User user = userService.findUserByLogin(txtLogin.getValue());
        if (user==null)
          return true;
        return false;
    }

    private GridLayout buildGridMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(7);
        gridMainLayout.setRows(5);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        txtUserId = new TextField("ID:");
        txtUserId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtUserId.setEnabled(false);
        gridMainLayout.addComponent(txtUserId,0,0);

        txtNameUser = new TextField("Nombre usuario:");
        txtNameUser.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtNameUser.setWidth("100%");
        txtNameUser.setRequiredIndicatorVisible(true);
        gridMainLayout.addComponent(txtNameUser,1,0,2,0);

        txtLogin = new TextField("Login:");
        txtLogin.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLogin.setRequiredIndicatorVisible(true);
        gridMainLayout.addComponent(txtLogin,0,1);

        txtPassword = new PasswordField("Password:");
        txtPassword.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtPassword.setRequiredIndicatorVisible(true);
        gridMainLayout.addComponent(txtPassword,1,1);

        cmbRol = new ComboBox<>("Rol usuario:");
        cmbRol.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbRol.setEmptySelectionAllowed(false);
        cmbRol.setRequiredIndicatorVisible(true);
        cmbRol.setEmptySelectionCaption("Seleccione Rol");
        gridMainLayout.addComponent(cmbRol,2,1);

        btnGroupStateUser = new RadioButtonGroup("Estado:");
        btnGroupStateUser.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        btnGroupStateUser.setItems("ACTIVO","BAJA");
        btnGroupStateUser.setRequiredIndicatorVisible(true);
        gridMainLayout.addComponent(btnGroupStateUser,0,2);

        chkResetPassword = new CheckBox("Reset password:");
        chkResetPassword.setStyleName(ValoTheme.CHECKBOX_SMALL);
        gridMainLayout.addComponent(chkResetPassword,1,2);
        gridMainLayout.setComponentAlignment(chkResetPassword,Alignment.MIDDLE_LEFT);

        cmbRolViewContract = new ComboBox<>("Ambito contratos:");
        cmbRolViewContract.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbRolViewContract.setEmptySelectionAllowed(false);
        cmbRolViewContract.setRequiredIndicatorVisible(true);
        cmbRolViewContract.setWidth("100%");
        gridMainLayout.addComponent(cmbRolViewContract,2,2,3,2);

        btnSaveUser = new Button("Guardar");
        btnSaveUser.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSaveUser.setIcon(VaadinIcons.DATABASE);
        btnSaveUser.setWidth("150px");
        gridMainLayout.addComponent(btnSaveUser,4,0);

        btnNewUser = new Button("Nuevo");
        btnNewUser.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnNewUser.setIcon(VaadinIcons.PLUS_CIRCLE);
        btnNewUser.setWidth("150px");
        gridMainLayout.addComponent(btnNewUser,4,1);

        gridMainLayout.addComponent(buildPanelGridUser(),0,3,4,3);

        return gridMainLayout;
    }



    private Panel buildPanelGridUser(){
        panelGridUser = new Panel();
        panelGridUser.setStyleName(ValoTheme.PANEL_WELL);
        panelGridUser.setWidth("100%");
        panelGridUser.setHeight("350px");
        gridUser = new Grid<>(User.class);
        gridUser.setStyleName(ValoTheme.TABLE_SMALL);
        gridUser.setWidth("100%");
        gridUser.setHeight("330px");
        panelGridUser.setContent(gridUser);
        return panelGridUser;
    }

}
