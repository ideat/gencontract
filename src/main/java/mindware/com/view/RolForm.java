package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.MessageBox;
import mindware.com.model.MenuOption;
import mindware.com.model.Option;
import mindware.com.model.Rol;
import mindware.com.service.MenuOptionService;
import mindware.com.service.OptionService;
import mindware.com.service.RolService;

import java.util.*;

public class RolForm extends CustomComponent implements View{
    private GridLayout gridMainLayout;
    private HorizontalLayout horizontalLayout;
    private Grid<Rol> gridRol;
    private Button btnSaveRol;
    private Button btnNewRol;
    private TextField textRol;
    private TextField textDescription;
    private TextField textRolId;
    private TwinColSelect optionMenu;
    private Panel panelGridRol;
    private List<String> listOptions;

    private MenuOptionService menuOptionService;
    private RolService rolService;

    public RolForm (){
        setCompositionRoot(buildGridMainLayout());
        postBuild();

    }

    private void postBuild(){
        menuOptionService = new MenuOptionService();
        rolService = new RolService();
        listOptions = new ArrayList<>();
        fillMenuOption();
        optionMenu.setItems(listOptions);

        fillGridRol();

        gridRol.addItemClickListener(itemClick -> {
            fillOptionRol(itemClick.getItem());
        });


        btnSaveRol.addClickListener(clickEvent -> {
           if (textRolId.isEmpty()){
               insertRolOptions();

           }else {
               updateRolOpciones();
           }
           fillGridRol();
        });

        btnNewRol.addClickListener(clickEvent -> {
            textRolId.setReadOnly(false);
            textRolId.clear();
            textRol.clear();
            textDescription.clear();
            textRolId.setReadOnly(true);
            optionMenu.clear();
            optionMenu.setItems(listOptions);

        });

        optionMenu.addSelectionListener(event -> {
           Object[] l =  event.getNewSelection().toArray();

        });


    }

    private void updateRolOpciones(){
        Rol rol = new Rol();
        rol.setRolId(Integer.parseInt(textRolId.getValue().toString()));
        rol.setRolName(textRol.getValue().toString());
        rol.setDescription(textDescription.getValue().toString());
        rolService.updateRol(rol);

        menuOptionService.deleteOptionMenu(Integer.parseInt(textRolId.getValue().toString()));
        List<MenuOption> opcionesMenuList = fillAssignedOptions(rol.getRolId());

        for (MenuOption om : opcionesMenuList) {
            menuOptionService.insertMenuOption(om);
        }
    }

    private void insertRolOptions(){

        Rol rol = new Rol();
        rol.setRolName(textRol.getValue());
        rol.setDescription(textDescription.getValue());

        rolService.insertRol(rol);

        List<MenuOption> opcionesMenuList = fillAssignedOptions(rol.getRolId());

        for (MenuOption om : opcionesMenuList) {
            menuOptionService.insertMenuOption(om);
        }

    }

    private int getCodOpcion(String opcion){
        String[] partes = opcion.split("-");
        return Integer.parseInt(partes[0].toString());
    }

    private List<MenuOption> fillAssignedOptions(int rolId) {
        Object selectedValues= optionMenu.getValue();
        List<MenuOption> optionsMenus = new ArrayList<>();
        List<String> s = new ArrayList<>();
        s.addAll((Collection<? extends String>) selectedValues);
        for(String op : s) {
            MenuOption menuOption = new MenuOption();
            menuOption.setRolId(rolId);
            menuOption.setOptionId(getCodOpcion(op.toString()));
            optionsMenus.add(menuOption);
        }

        return optionsMenus;
    }

    private void fillGridRol(){
        gridRol.removeAllColumns();
        List<Rol> rolList=  rolService.findAllRol();
        gridRol.setItems(rolList);
        gridRol.addColumn(Rol::getRolId).setCaption("ID");
        gridRol.addColumn(Rol::getRolName).setCaption("Rol");
        gridRol.addColumn(Rol::getDescription).setCaption("Descripcion");
    }

    private void fillOptionRol(Rol rol) {
        List<String> assignedOptions = new ArrayList<>();
        textRol.setValue(rol.getRolName());
//        textDescription.setValue(gridRol.getContainerDataSource().getItem(rol).getItemProperty("Descripcion").getValue()==null ? "": gridRol.getContainerDataSource().getItem(rol).getItemProperty("Descripcion").getValue().toString() );
        textDescription.setValue(rol.getDescription());
        textRolId.setReadOnly(false);
        textRolId.setValue(rol.getRolId().toString());
        textRolId.setReadOnly(true);

        List<MenuOption> menuOptionList = menuOptionService.findMenuOptionByRolId(rol.getRolId());
        for(MenuOption menuOption:menuOptionList){
            assignedOptions.add(menuOption.getOptionId().toString()+"-"+menuOption.getOption().getNameOption());
        }
        optionMenu.clear();

        Collections.sort(listOptions);
        Set<String> list1 = new HashSet<String>(listOptions);
        Set<String> list2 = new HashSet<String>(assignedOptions);
        optionMenu.setItems(list1);

        optionMenu.setValue(list2);
//        optionMenu.updateSelection(list1,list2);
    }


    private void fillMenuOption(){
        OptionService optionService = new OptionService();
        List<Option> optionList = optionService.findAllOptions();

        for(Option option:optionList){
            listOptions.add(option.getOptionId().toString()+"-"+option.getNameOption());
        }
    }

    private GridLayout buildGridMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setSizeFull();
        gridMainLayout.setColumns(7);
        gridMainLayout.setRows(9);
        gridMainLayout.setSpacing(true);

        gridMainLayout.addComponent(buildHorizontalLayout(),0,0,6,0);

        gridRol = new Grid();
        gridRol.setStyleName(ValoTheme.TABLE_COMPACT);
//        gridRol.setHeight("50.0%");
        gridRol.setWidth("100%");

        panelGridRol = new Panel();
        panelGridRol.setStyleName(ValoTheme.PANEL_WELL);
        panelGridRol.setWidth("100%");
        panelGridRol.setHeight("200px");
        panelGridRol.setContent(gridRol);
        gridMainLayout.addComponent(panelGridRol,0,1,6,2);

        optionMenu = new TwinColSelect();
        optionMenu.setLeftColumnCaption("Opciones disponibles");
        optionMenu.setRightColumnCaption("Opciones asignadas");
//        optionMenu.setHeight("50.0%");
        optionMenu.setWidth("100.0%");
        gridMainLayout.addComponent(optionMenu,0,3,5,4);

        return gridMainLayout;
    }

    private HorizontalLayout buildHorizontalLayout(){
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        textRolId = new TextField("ID:");
        textRolId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        textRolId.setReadOnly(true);
        horizontalLayout.addComponent(textRolId);

        textRol = new TextField("Rol:");
        textRol.setStyleName(ValoTheme.TEXTFIELD_TINY);
        horizontalLayout.addComponent(textRol);

        textDescription = new TextField("Descricion rol:");
        textDescription.setStyleName(ValoTheme.TEXTFIELD_TINY);
        textDescription.setWidth("220px");
        horizontalLayout.addComponent(textDescription);

        btnSaveRol = new Button("Guardar");
        btnSaveRol.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSaveRol.setIcon(VaadinIcons.DATABASE);
        horizontalLayout.addComponent(btnSaveRol);


        btnNewRol = new Button("Nuevo");
        btnNewRol.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnNewRol.setIcon(VaadinIcons.PLUS);
        horizontalLayout.addComponent(btnNewRol);


        return horizontalLayout;
    }

}
