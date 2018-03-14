package com.mindware;

import javax.servlet.annotation.WebServlet;

import com.mindware.View.*;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.client.ServerConnector;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.FontAwesome;
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
import kaesdingeling.hybridmenu.builder.NotificationBuilder;
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
import kaesdingeling.hybridmenu.data.enums.ENotificationPriority;
import kaesdingeling.hybridmenu.data.leftmenu.MenuButton;
import kaesdingeling.hybridmenu.data.leftmenu.MenuSubMenu;
import kaesdingeling.hybridmenu.data.top.TopMenuButton;
import kaesdingeling.hybridmenu.data.top.TopMenuLabel;
import kaesdingeling.hybridmenu.data.top.TopMenuSubContent;

import com.mindware.page.SettingsPage;
import com.mindware.page.ThemeBuilderPage;

@Theme("demo")
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

    @Override
    protected void init(VaadinRequest request) {
    	UI.getCurrent().setPollInterval(5000);
    	
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
    	UI.getCurrent().getNavigator().addView(ImportDataForm.class.getSimpleName(), ImportDataForm.class);
    	UI.getCurrent().getNavigator().addView(ListContractForm.class.getSimpleName(), ListContractForm.class);
    	UI.getCurrent().getNavigator().addView(RegisterNewDataForm.class.getSimpleName(), RegisterNewDataForm.class);
		UI.getCurrent().getNavigator().addView(ManageBranchOfficeForm.class.getSimpleName(), ManageBranchOfficeForm.class);
		UI.getCurrent().getNavigator().addView(TemplateContractsForm.class.getSimpleName(), TemplateContractsForm.class);
		UI.getCurrent().getNavigator().addView(VariablesContractsForm.class.getSimpleName(), VariablesContractsForm.class);
		UI.getCurrent().getNavigator().addView(RolForm.class.getSimpleName(), RolForm.class);
		UI.getCurrent().getNavigator().addView(GenerateContractsForm.class.getSimpleName(), GenerateContractsForm.class);
		UI.getCurrent().getNavigator().addView(ParametersForm.class.getSimpleName(), ParametersForm.class);
//    	UI.getCurrent().getNavigator().addView(ThemeBuilderPage.class.getSimpleName(), ThemeBuilderPage.class);
		UI.getCurrent().getNavigator().addView(SettingsPage.class.getSimpleName(), SettingsPage.class);
		UI.getCurrent().getNavigator().setErrorView(UserForm.class);


		if(hybridMenu.getMenuComponents().equals(EMenuComponents.ONLY_LEFT))
    	buildLeftMenu(hybridMenu);
		else
			if(hybridMenu.getMenuComponents().equals(EMenuComponents.LEFT_WITH_TOP)) {
				buildLeftMenu(hybridMenu);
				buildTopOnlyMenu(hybridMenu);
			}else
				buildTopOnlyMenu(hybridMenu);

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

	private void buildTopOnlyMenu(HybridMenu hybridMenu) {
//		TopMenuButtonBuilder.get()
//				.setCaption("Usuario")
//				.setIcon(VaadinIcons.USER)
//				.setAlignment(Alignment.MIDDLE_RIGHT)
//				.setNavigateTo(UserForm.class)
//				.build(hybridMenu);

//		TopMenuButtonBuilder.get()
//				.setCaption("Usuario")
//				.setIcon(VaadinIcons.USER)
//				.setAlignment(Alignment.MIDDLE_RIGHT)
//				.setHideCaption(false)
//				.setNavigateTo(MemberPage.class)
//				.build(hybridMenu);

//		TopMenuButtonBuilder.get()
//				.setCaption("Member")
//				.setIcon(VaadinIcons.USER)
//				.setAlignment(Alignment.MIDDLE_RIGHT)
//				.setHideCaption(false)
//				.addStyleName(EMenuStyle.ICON_RIGHT)
//				.setNavigateTo(MemberPage.class)
//				.build(hybridMenu);

		TopMenuSubContent userAccountMenu = TopMenuSubContentBuilder.get()
				.setButtonCaption("Usuario")
				.setButtonIcon(new ThemeResource("images/profilDummy.jpg"))
				.addButtonStyleName(EMenuStyle.ICON_RIGHT)
				.addButtonStyleName(EMenuStyle.PROFILVIEW)
				.setAlignment(Alignment.MIDDLE_RIGHT)
				.build(hybridMenu);

//		userAccountMenu.addLabel("");
//		userAccountMenu.addHr();
		userAccountMenu.addButton("Cambiar password");


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

	private void buildLeftMenu(HybridMenu hybridMenu) {
		MenuButton importDataButton = LeftMenuButtonBuilder.get()
				.withCaption("Importar datos")
				.withIcon(VaadinIcons.STORAGE)
				.withNavigateTo(ImportDataForm.class)
				.build();

		hybridMenu.addLeftMenuButton(importDataButton);
		
//		MenuButton themeBuilderButton = LeftMenuButtonBuilder.get()
//				.withCaption("Theme Builder")
//				.withIcon(FontAwesome.WRENCH)
//				.withNavigateTo(ThemeBuilderPage.class)
//				.build();
//
//		hybridMenu.addLeftMenuButton(themeBuilderButton);

		MenuButton registerDataButton = LeftMenuButtonBuilder.get()
				.withCaption("Registrar nuevos datos")
				.withIcon(VaadinIcons.ADD_DOCK)
				.withNavigateTo(RegisterNewDataForm.class)
				.build();

		hybridMenu.addLeftMenuButton(registerDataButton);



		MenuSubMenu contractManageList = LeftMenuSubMenuBuilder.get()
				.setCaption("Contratos")
				.setIcon(VaadinIcons.BRIEFCASE)
//				.setConfig(hybridMenu.getConfig())
				.build(hybridMenu);

		contractManageList.addLeftMenuButton(LeftMenuButtonBuilder.get()
				.withCaption("Lista contratos")
				.withIcon(VaadinIcons.FILE_TEXT)
				.withNavigateTo(ListContractForm.class)
				.build());

		contractManageList.addLeftMenuButton(LeftMenuButtonBuilder.get()
				.withCaption("Generar contratos")
				.withIcon(VaadinIcons.FILE_PROCESS)
				.withNavigateTo(GenerateContractsForm.class)
				.build());

		MenuButton ManageBranchButton = LeftMenuButtonBuilder.get()
				.withCaption("Datos Sucursales")
				.withIcon(VaadinIcons.OFFICE)
				.withNavigateTo(ManageBranchOfficeForm.class)
				.build();
		hybridMenu.addLeftMenuButton(ManageBranchButton);

		MenuButton TempleateContractsButton = LeftMenuButtonBuilder.get()
				.withCaption("Plantillas de contratos")
				.withIcon(VaadinIcons.DIPLOMA)
				.withNavigateTo(TemplateContractsForm.class)
				.build();
		hybridMenu.addLeftMenuButton(TempleateContractsButton);

		MenuButton VariablesContractsButton = LeftMenuButtonBuilder.get()
				.withCaption("Variables de contratos")
				.withIcon(VaadinIcons.PALETE)
				.withNavigateTo(VariablesContractsForm.class)
				.build();
		hybridMenu.addLeftMenuButton(VariablesContractsButton);

		MenuButton UserButton = LeftMenuButtonBuilder.get()
				.withCaption("Usuarios")
				.withIcon(VaadinIcons.USERS)
				.withNavigateTo(UserForm.class)
				.build();
		hybridMenu.addLeftMenuButton(UserButton);

		MenuButton RolButton = LeftMenuButtonBuilder.get()
				.withCaption("Roles")
				.withIcon(VaadinIcons.FILE_CODE)
				.withNavigateTo(RolForm.class)
				.build();
		hybridMenu.addLeftMenuButton(RolButton);

		MenuButton ParameterButton = LeftMenuButtonBuilder.get()
				.withCaption("Parametros")
				.withIcon(VaadinIcons.PANEL)
				.withNavigateTo(ParametersForm.class)
				.build();
		hybridMenu.addLeftMenuButton(ParameterButton);


//		MenuSubMenu memberListTwo = LeftMenuSubMenuBuilder.get()
//				.setCaption("member")
//				.setIcon(VaadinIcons.USERS)
//				.setConfig(hybridMenu.getConfig())
//				.build(contractManageList);
//
//		memberListTwo.addLeftMenuButton(LeftMenuButtonBuilder.get()
//				.withCaption("Settings")
//				.withIcon(VaadinIcons.COGS)
//				.withNavigateTo(SettingsPage.class)
//				.build());
//
//		memberListTwo.addLeftMenuButton(LeftMenuButtonBuilder.get()
//				.withCaption("Member")
//				.withIcon(VaadinIcons.USER)
//				.withNavigateTo(MemberPage.class)
//				.build());



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
	
	public HybridMenu getHybridMenu() {
		return hybridMenu; 
	}
	
	@Override
	public void detach(DetachEvent event) {
		getUI().close();
	}
}
