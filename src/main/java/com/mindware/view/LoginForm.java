package com.mindware.view;

import com.mindware.MyUI;

import com.vaadin.data.HasValue;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by freddy on 05-04-17.
 */
public class LoginForm extends CustomComponent {
    private AbsoluteLayout mainLayout;
    private GridLayout gridLayout_1;
    private Button btnActualizarPassword;
    private Button btnIngresar;
    private PasswordField txtPassword;
    private TextField txtLogin;
    private Label lblLogin;
    private Label lblPassword;
    private Label lblMensaje;

    private VerticalLayout verticalLayout;

    private FormLayout formLayout;

    public LoginForm() {
        setHeight("100%");
        setCompositionRoot(buildMainLayout());

        postBuild();
    }

    private void postBuild() {
        btnIngresar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                logeo();
            }
        });

        txtPassword.addShortcutListener(new ShortcutListener("",KeyCode.ENTER,null) {
            @Override
            public void handleAction(Object o, Object o1) {
                logeo();
            }
        });

        txtLogin.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {

            }
        });
    }

    private void logeo() {
        if (validarCredenciales(txtLogin.getValue().toString(), txtPassword.getValue().toString())) {
            ((MyUI) UI.getCurrent()).callMenu();
        } else {
            lblMensaje.setVisible(true);
            lblMensaje.setValue("Credenciales invalidas");
        }
    }

    private boolean validarCredenciales(String login, String password) {
//        UsuarioService usuarioService = new UsuarioService();
//        usuario = usuarioService.findUsuarioByLogin(login);
//
//        if (usuario==null) {
//            return false;
//        } else {
//            String encriptado = new Encript().encriptString(password);
//            if (usuario.getEstado().equals("baja"))
//                return false;
//            else {
//                if (usuario.getPassword().equals(encriptado)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        }
        return true;

    }

    private AbsoluteLayout buildMainLayout() {
        mainLayout = new AbsoluteLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        setWidth("100.0%");
        setHeight("100.0%");

//        gridLayout_1 = buildGridLayout();
        mainLayout.addComponent(buildVerticalLayout(),"top:90px;right:0px;left:40px");



        return mainLayout;
    }

    private VerticalLayout buildVerticalLayout(){
        verticalLayout = new VerticalLayout();


        Panel panel = new Panel("<font size=4 color=#357EC7> Bienvenido - Ingrese sus credenciales<font>");

        panel.setStyleName("desktop");
        panel.setSizeUndefined();
        panel.setCaptionAsHtml(true);
        panel.setWidth("440px");
        panel.setHeight("210px");

        panel.setContent(buildFormLayout());

        verticalLayout.addComponent(panel);
        verticalLayout.setComponentAlignment(panel,Alignment.BOTTOM_CENTER);
        return verticalLayout;
    }

    private FormLayout buildFormLayout(){
        formLayout = new FormLayout();

        lblMensaje = new Label();
        lblMensaje.setStyleName(ValoTheme.LABEL_BOLD,true);
        lblMensaje.setStyleName(ValoTheme.LABEL_FAILURE,true);
        lblMensaje.setVisible(false);

        txtLogin = new TextField("Login:");
        txtLogin.setStyleName(ValoTheme.TEXTFIELD_SMALL);

        txtPassword = new PasswordField("Password:");

        btnIngresar = new Button();
        btnIngresar.setCaption("Ingresar");
        btnIngresar.setIcon(VaadinIcons.UNLOCK);
        btnIngresar.setStyleName(ValoTheme.BUTTON_PRIMARY);

        formLayout.addStyleName(ValoTheme.PANEL_WELL);
        formLayout.addComponent(lblMensaje);
        formLayout.addComponent(txtLogin);
        formLayout.addComponent(txtPassword);
        formLayout.addComponent(btnIngresar);
        formLayout.setSizeUndefined(); // Shrink to fit



        formLayout.setMargin(true);


        return formLayout;
    }



}
