package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.User;
import mindware.com.security.Encript;
import mindware.com.service.UserService;

public class UserPasswordForm extends CustomComponent implements View {
    private FormLayout mainFormLayout;
    private PasswordField txtOldPassword;
    private PasswordField txtNewPassword;
    private PasswordField txtVerifyNewPassword;
    private Button btnSavePassword;

    public UserPasswordForm(Integer userId){
        setCompositionRoot(buildFormMainLayout());
        String password = new UserService().findUserByUserId(userId).getPassword();
        postBuild(userId,password);

    }

    private void postBuild(Integer userId, String password){
        btnSavePassword.addClickListener(clickEvent -> {
           try {
               if (validateData()) {
                   if (verifyOldPassword(userId, password)) {
                       if (verifyNewPassword()) {
                           udpatePassword(userId);
                           Notification.show("Password",
                                   "Password actualizado",
                                   Notification.Type.ERROR_MESSAGE);
                       } else {
                           Notification.show("Password",
                                   "Nuevo password no coincide",
                                   Notification.Type.ERROR_MESSAGE);
                           txtNewPassword.focus();
                       }
                   } else {
                       Notification.show("Password",
                               "Antiguo password no coincide, revisar",
                               Notification.Type.ERROR_MESSAGE);
                       txtOldPassword.focus();
                   }
               }else{
                   Notification.show("Cambio de password",
                           "Datos incompletos",
                           Notification.Type.ERROR_MESSAGE);
               }
           }catch (Exception e){
               Notification.show("Password",
                       "Error al actualizar el password: " + e.getMessage(),
                       Notification.Type.ERROR_MESSAGE);
           }

        });
    }

    private void udpatePassword(Integer userId){
        UserService userService = new UserService();
        User user = new User();
        user.setPassword(new Encript().encriptString(txtNewPassword.getValue()));
        user.setUserId(userId);
        userService.updateUserPassword(user);

    }

    private boolean validateData(){
        if (txtOldPassword.isEmpty()) return false;
        if (txtNewPassword.isEmpty()) return false;
        if (txtVerifyNewPassword.isEmpty()) return false;
        return true;
    }

    private boolean verifyNewPassword(){
        if(txtNewPassword.getValue().equals(txtVerifyNewPassword.getValue()))
            return true;
        else return false;
    }

    private boolean verifyOldPassword(Integer userId, String oldPassword){

        if (new Encript().encriptString(txtOldPassword.getValue()).equals(oldPassword))
            return true;
        else
            return false;
    }

    private FormLayout buildFormMainLayout(){
        mainFormLayout = new FormLayout();
        mainFormLayout.setSizeFull();
        mainFormLayout.setSpacing(true);


        txtOldPassword = new PasswordField("Antiguo password:");
        txtOldPassword.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainFormLayout.addComponent(txtOldPassword);
        mainFormLayout.setComponentAlignment(txtOldPassword,Alignment.MIDDLE_CENTER);

        txtNewPassword = new PasswordField("Nuevo password:");
        txtNewPassword.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainFormLayout.addComponent(txtNewPassword);
        mainFormLayout.setComponentAlignment(txtNewPassword,Alignment.MIDDLE_CENTER);

        txtVerifyNewPassword = new PasswordField("Verificar nuevo password:");
        txtVerifyNewPassword.setStyleName(ValoTheme.TEXTFIELD_TINY);
        mainFormLayout.addComponent(txtVerifyNewPassword);
        mainFormLayout.setComponentAlignment(txtVerifyNewPassword,Alignment.MIDDLE_CENTER);

        btnSavePassword = new Button("Guardar");
        btnSavePassword.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSavePassword.setIcon(VaadinIcons.DATABASE);
        mainFormLayout.addComponent(btnSavePassword);
        mainFormLayout.setComponentAlignment(btnSavePassword,Alignment.MIDDLE_CENTER);

        return mainFormLayout;

    }

}
