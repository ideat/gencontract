package mindware.com;

import javax.servlet.annotation.WebServlet;

import mindware.com.model.MenuOption;
import mindware.com.model.Option;
import mindware.com.model.Rol;
import mindware.com.service.RolService;
import mindware.com.service.UserService;
import mindware.com.view.*;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.client.ServerConnector;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import elemental.json.JsonArray;
import kaesdingeling.hybridmenu.HybridMenu;
import kaesdingeling.hybridmenu.builder.HybridMenuBuilder;
import kaesdingeling.hybridmenu.builder.left.LeftMenuButtonBuilder;
import kaesdingeling.hybridmenu.builder.left.LeftMenuSubMenuBuilder;
import kaesdingeling.hybridmenu.builder.top.TopMenuButtonBuilder;
import kaesdingeling.hybridmenu.builder.top.TopMenuLabelBuilder;
import kaesdingeling.hybridmenu.builder.top.TopMenuSubContentBuilder;
import kaesdingeling.hybridmenu.components.NotificationCenter;
import kaesdingeling.hybridmenu.data.DesignItem;
import kaesdingeling.hybridmenu.data.MenuConfig;
import kaesdingeling.hybridmenu.data.enums.EMenuComponents;
import kaesdingeling.hybridmenu.data.enums.EMenuStyle;
import kaesdingeling.hybridmenu.data.leftmenu.MenuButton;
import kaesdingeling.hybridmenu.data.leftmenu.MenuSubMenu;
import kaesdingeling.hybridmenu.data.top.TopMenuButton;
import kaesdingeling.hybridmenu.data.top.TopMenuLabel;
import kaesdingeling.hybridmenu.data.top.TopMenuSubContent;

import mindware.com.page.SettingsPage;
import mindware.com.page.ThemeBuilderPage;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Theme("mytheme")
@Title("Generador contratos")
@PushStateNavigation
@SuppressWarnings({ "serial", "deprecation" })
public class MyUI extends UI implements DetachListener {
    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = true, ui = MyUI.class)
    public static class Servlet extends VaadinServlet {
    }
    //private SimplePanel content = new SimplePanel();
    
    private NotificationCenter notiCenter = null;
    private HybridMenu hybridMenu = null;
	private LoginForm loginForm = null;
    @Override
    protected void init(VaadinRequest request) {
    	UI.getCurrent().setPollInterval(5000);
        loginForm = new LoginForm();
    	setContent(loginForm);
        try {
            createFolders();
        } catch (IOException e) {
            e.printStackTrace();
        }

//		callMenu();
    }

    private void createFolders() throws IOException {

        Path path = Paths.get(System.getProperties().get("user.home").toString());

        File template = new File(path.toString()+"/template");
        File generated = new File(path.toString()+"/generated");

        if (!template.exists()) {
            template.mkdirs();
        }
        if (!generated.exists()) {
            generated.mkdirs();
        }
    }

	public void callMenu(String login, Integer userId, Integer rolId) {
		MenuConfig menuConfig = new MenuConfig();
		menuConfig.setDesignItem(DesignItem.getDarkDesign());

		notiCenter = new NotificationCenter(5000);

		hybridMenu = HybridMenuBuilder.get()
                .setContent(new VerticalLayout())
                .setMenuComponent(EMenuComponents.LEFT_WITH_TOP)
                .setConfig(menuConfig)
//    			.withNotificationCenter(notiCenter)
                .build();

		UI.getCurrent().getNavigator().addView(UserForm.class.getSimpleName(), UserForm.class);
        UI.getCurrent().getNavigator().addView(UserPasswordForm.class.getSimpleName(), new UserPasswordForm(userId));
		UI.getCurrent().getNavigator().addView(ImportDataForm.class.getSimpleName(), ImportDataForm.class);
		UI.getCurrent().getNavigator().addView(TemplateContractForm.class.getSimpleName(), TemplateContractForm.class);
		UI.getCurrent().getNavigator().addView(RegisterNewDataForm.class.getSimpleName(), RegisterNewDataForm.class);
		UI.getCurrent().getNavigator().addView(ManageBranchOfficeForm.class.getSimpleName(), ManageBranchOfficeForm.class);
		UI.getCurrent().getNavigator().addView(ListContractsForm.class.getSimpleName(), ListContractsForm.class);
		UI.getCurrent().getNavigator().addView(VariablesContractsForm.class.getSimpleName(), VariablesContractsForm.class);
        UI.getCurrent().getNavigator().addView(CustomVariableForm.class.getSimpleName(), CustomVariableForm.class);
		UI.getCurrent().getNavigator().addView(RolForm.class.getSimpleName(), RolForm.class);
		UI.getCurrent().getNavigator().addView(GenerateContractsForm.class.getSimpleName(), GenerateContractsForm.class);
		UI.getCurrent().getNavigator().addView(ParametersForm.class.getSimpleName(), ParametersForm.class);
//    	UI.getCurrent().getNavigator().addView(ThemeBuilderPage.class.getSimpleName(), ThemeBuilderPage.class);
		UI.getCurrent().getNavigator().addView(SettingsPage.class.getSimpleName(), SettingsPage.class);
		UI.getCurrent().getNavigator().setErrorView(UserForm.class);


		if(hybridMenu.getMenuComponents().equals(EMenuComponents.ONLY_LEFT))
    	buildLeftMenu(hybridMenu,rolId);
		else
			if(hybridMenu.getMenuComponents().equals(EMenuComponents.LEFT_WITH_TOP)) {
				buildLeftMenu(hybridMenu, rolId);
				buildTopOnlyMenu(hybridMenu,userId);
			}else
				buildTopOnlyMenu(hybridMenu,userId);

		getNavigator().addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                if (event.getOldView() != null && event.getOldView().getClass().getSimpleName().equals(ThemeBuilderPage.class.getSimpleName())) {
                    hybridMenu.switchTheme(DesignItem.getDarkDesign());
                }
                return true;
            }
        });


		setContent(hybridMenu);
		VaadinSession.getCurrent().setAttribute(HybridMenu.class, hybridMenu);

		JavaScript.getCurrent().addFunction("aboutToClose", new JavaScriptFunction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void call(JsonArray arguments) {
                detach();
            }
        });

		Page.getCurrent().getJavaScript().execute("window.onbeforeunload = function (e) { var e = e || window.event; aboutToClose(); return; };");
	}

	private void printState(ServerConnector connector, boolean serverDebug) {
    	System.out.println(connector.getClass().getSimpleName());
}

	private void buildTopOnlyMenu(HybridMenu hybridMenu, Integer userId) {

		TopMenuSubContent userAccountMenu = TopMenuSubContentBuilder.get()
				.setButtonCaption("Usuario")
				.setButtonIcon(new ThemeResource("images/profilDummy.jpg"))
				.addButtonStyleName(EMenuStyle.ICON_RIGHT)
				.addButtonStyleName(EMenuStyle.PROFILVIEW)
				.setAlignment(Alignment.MIDDLE_RIGHT)
				.build(hybridMenu);

		userAccountMenu.addButton("Cambiar password").addClickListener(clickEvent -> {

            UI.getCurrent().getNavigator().navigateTo(UserPasswordForm.class.getSimpleName());
        });


		TopMenuButtonBuilder.get()
				.setCaption("Vista menu")
				.setIcon(VaadinIcons.EXPAND_SQUARE)
				.setAlignment(Alignment.MIDDLE_RIGHT)
				.addClickListener(e -> hybridMenu.setLeftMenuMinimal(!hybridMenu.isLeftMenuMinimal()))
				.build(hybridMenu);
		
		TopMenuButton notiButton = TopMenuButtonBuilder.get()
			.setIcon(VaadinIcons.BELL_O)
			.setAlignment(Alignment.MIDDLE_RIGHT)
			.build(hybridMenu);
		
		notiCenter.setNotificationButton(notiButton);

		TopMenuLabel label = TopMenuLabelBuilder.get()
				.setCaption("<b>Generador contratos</b>")
				.setIcon(new ThemeResource("images/Logo.png"))
				.build(hybridMenu);

		label.getComponent().addClickListener(e -> {
			UI.getCurrent().getNavigator().navigateTo(UserForm.class.getSimpleName());
		});

//		TopMenuButton notiButtonLow = TopMenuButtonBuilder.get()
//				.setCaption("Add Low noti")
//				.setIcon(VaadinIcons.BELL_O)
//				.setUseOwnListener(true)
//				.build(hybridMenu);
//
//		TopMenuButton notiButtonMedium = TopMenuButtonBuilder.get()
//				.setCaption("Add Medium noti")
//				.setIcon(VaadinIcons.BELL_O)
//				.setUseOwnListener(true)
//				.build(hybridMenu);
//
//		TopMenuButton notiButtonHigh = TopMenuButtonBuilder.get()
//				.setCaption("Add High noti")
//				.setIcon(VaadinIcons.BELL_O)
//				.setUseOwnListener(true)
//				.build(hybridMenu);
		
//		notiButtonLow.addClickListener(e -> {
//			NotificationBuilder.get(notiCenter)
//			.withCaption("Test")
//			.withDescription("descriptifghhgjghjkfjhgjfhjfoikjrsadopherduiothjreouithruetijpertheriuhton")
//			.withPriority(ENotificationPriority.LOW)
//			.withCloseButton()
//			.build();
//		});
//
//		notiButtonMedium.addClickListener(e -> {
//			NotificationBuilder.get(notiCenter)
//			.withCaption("Test")
//			.withDescription("sdfgdfhg")
//			.build();
//		});
		
//		notiButtonHigh.addClickListener(e -> {
//			NotificationBuilder.get(notiCenter)
//				.withCaption("Test")
//				.withDescription("descriptifghhgjghjkfjhgjfhjfoikjrsadopherduiothjreouithruetijpertheriuhton")
//				.withPriority(ENotificationPriority.HIGH)
//				.withIcon(VaadinIcons.INFO)
//				.withCloseButton()
//				.build();
//		});


//		TopMenuButtonBuilder.get()
//				.setCaption("Home")
//				.setIcon(VaadinIcons.HOME)
//				.setNavigateTo(UserForm.class)
//				.build(hybridMenu);

	}



	private void buildLeftMenu(HybridMenu hybridMenu,  Integer rolId) {
        RolService rolService = new RolService();
        Rol rol = rolService.findAllRolMenuOptionByRolId(rolId);
        List<MenuOption> menuOptionList = rol.getMenuOption();
        String[] listOptions = new String[menuOptionList.size()];
        int i=0;
        for(MenuOption menuOption:menuOptionList){
            listOptions[i] = menuOption.getOptionId().toString();
            i+=1;
        }



        if (Arrays.asList(listOptions).contains("1")) {
            MenuButton importDataButton = LeftMenuButtonBuilder.get()
                    .withCaption("Importar datos")
                    .withIcon(VaadinIcons.STORAGE)
                    .withNavigateTo(ImportDataForm.class)
                    .build();

            hybridMenu.addLeftMenuButton(importDataButton);
        }
//        if (Arrays.asList(listOptions).contains("2")) {
//            MenuButton registerDataButton = LeftMenuButtonBuilder.get()
//                    .withCaption("Registrar nuevos datos")
//                    .withIcon(VaadinIcons.ADD_DOCK)
//                    .withNavigateTo(RegisterNewDataForm.class)
//                    .build();
//
//            hybridMenu.addLeftMenuButton(registerDataButton);
//        }

        if (Arrays.asList(listOptions).contains("3") || Arrays.asList(listOptions).contains("4") || Arrays.asList(listOptions).contains("5")) {
            MenuSubMenu contractManageList = LeftMenuSubMenuBuilder.get()
                    .setCaption("Contratos")
                    .setIcon(VaadinIcons.BRIEFCASE)
//				.setConfig(hybridMenu.getConfig())
                    .build(hybridMenu);
            if (Arrays.asList(listOptions).contains("3")) {
                contractManageList.addLeftMenuButton(LeftMenuButtonBuilder.get()
                        .withCaption("Plantilla contratos")
                        .withIcon(VaadinIcons.FILE_TEXT)
                        .withNavigateTo(TemplateContractForm.class)
                        .build());
            }
            if (Arrays.asList(listOptions).contains("4")) {
                contractManageList.addLeftMenuButton(LeftMenuButtonBuilder.get()
                        .withCaption("Generar contratos")
                        .withIcon(VaadinIcons.FILE_PROCESS)
                        .withNavigateTo(GenerateContractsForm.class)
                        .build());
            }
            if (Arrays.asList(listOptions).contains("5")) {
                contractManageList.addLeftMenuButton(LeftMenuButtonBuilder.get()
                        .withCaption("Lista contratos")
                        .withIcon(VaadinIcons.FILE_TABLE)
                        .withNavigateTo(ListContractsForm.class)
                        .build());
            }
        }
        if (Arrays.asList(listOptions).contains("6")) {
            MenuButton ManageBranchButton = LeftMenuButtonBuilder.get()
                    .withCaption("Datos Sucursales")
                    .withIcon(VaadinIcons.OFFICE)
                    .withNavigateTo(ManageBranchOfficeForm.class)
                    .build();
            hybridMenu.addLeftMenuButton(ManageBranchButton);
        }
        if (Arrays.asList(listOptions).contains("7")) {
            MenuButton VariablesContractsButton = LeftMenuButtonBuilder.get()
                    .withCaption("Variables de contratos")
                    .withIcon(VaadinIcons.PALETE)
                    .withNavigateTo(VariablesContractsForm.class)
                    .build();
            hybridMenu.addLeftMenuButton(VariablesContractsButton);
        }

        if (Arrays.asList(listOptions).contains("12")) {
            MenuButton CustomVariableButton = LeftMenuButtonBuilder.get()
                    .withCaption("Variables personalizadas")
                    .withIcon(VaadinIcons.ABACUS)
                    .withNavigateTo(CustomVariableForm.class)
                    .build();
            hybridMenu.addLeftMenuButton(CustomVariableButton);
        }

        if (Arrays.asList(listOptions).contains("8")) {
            MenuButton UserButton = LeftMenuButtonBuilder.get()
                    .withCaption("Usuarios")
                    .withIcon(VaadinIcons.USERS)
                    .withNavigateTo(UserForm.class)
                    .build();
            hybridMenu.addLeftMenuButton(UserButton);
        }
        if (Arrays.asList(listOptions).contains("9")) {
            MenuButton RolButton = LeftMenuButtonBuilder.get()
                    .withCaption("Roles")
                    .withIcon(VaadinIcons.FILE_CODE)
                    .withNavigateTo(RolForm.class)
                    .build();
            hybridMenu.addLeftMenuButton(RolButton);
        }
//        if (Arrays.asList(listOptions).contains("10")) {
//            MenuButton ParameterButton = LeftMenuButtonBuilder.get()
//                    .withCaption("Parametros")
//                    .withIcon(VaadinIcons.PANEL)
//                    .withNavigateTo(ParametersForm.class)
//                    .build();
//            hybridMenu.addLeftMenuButton(ParameterButton);
//        }
        if (Arrays.asList(listOptions).contains("11")) {
            MenuSubMenu demoSettings = LeftMenuSubMenuBuilder.get()
                    .setCaption("Preferencia")
                    .setIcon(VaadinIcons.COGS)
                    .setConfig(hybridMenu.getConfig())
                    .build(hybridMenu);

            LeftMenuButtonBuilder.get()
                    .withCaption("Tema de color Blanco")
                    .withIcon(VaadinIcons.PALETE)
                    .withClickListener(e -> hybridMenu.switchTheme(DesignItem.getWhiteDesign()))
                    .build(demoSettings);

            LeftMenuButtonBuilder.get()
                    .withCaption("Tema de color Azul")
                    .withIcon(VaadinIcons.PALETE)
                    .withClickListener(e -> hybridMenu.switchTheme(DesignItem.getWhiteBlueDesign()))
                    .build(demoSettings);

            LeftMenuButtonBuilder.get()
                    .withCaption("Tema color Obscuro")
                    .withIcon(VaadinIcons.PALETE)
                    .withClickListener(e -> hybridMenu.switchTheme(DesignItem.getDarkDesign()))
                    .build(demoSettings);

            LeftMenuButtonBuilder.get()
                    .withCaption("Alternar vista minima")
                    .withIcon(VaadinIcons.EXPAND_SQUARE)
                    .withClickListener(e -> hybridMenu.setLeftMenuMinimal(!hybridMenu.isLeftMenuMinimal()))
                    .build(demoSettings);
        }



        MenuButton ExitButton = LeftMenuButtonBuilder.get()
                .withCaption("Salir")
                .withIcon(VaadinIcons.EXIT)
                .withClickListener(clickEvent -> {
                    Page.getCurrent().setLocation( "/gencontract" );
                    VaadinSession.getCurrent().close();
                })
                .build();
        hybridMenu.addLeftMenuButton(ExitButton);
	}
	
	public HybridMenu getHybridMenu() {
		return hybridMenu; 
	}
	
	@Override
	public void detach(DetachEvent event) {
		getUI().close();
	}
}
