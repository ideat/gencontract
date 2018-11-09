package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.BranchOffice;
import mindware.com.model.BranchUser;
import mindware.com.model.RolViewContract;
import mindware.com.service.BranchOfficeService;
import mindware.com.service.BranchUserService;
import mindware.com.service.RolViewContractService;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.*;

public class RolViewContractForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private HorizontalLayout horizontalLayout;
    private Grid<RolViewContract> gridRolViewContractGrid;
    private Button btnSaveRol;
    private Button btnNewRol;
    private TextField txtRol;
    private TextField txtDescription;
    private TextField txtRolViewContractId;
    private TwinColSelect branchUser;
    private Panel panelGridRolViewcontract;
    private ComboBox cmbListCity;
    private List<String> listBranch;
    private List<String> listCity;
    private RolViewContract rolViewContract;
    private GridCellFilter<RolViewContract> filterRolViewContract;

    private BranchOfficeService branchOfficeService;
    private RolViewContractService rolViewContractService;
    private  BranchUserService branchUserService;


    public RolViewContractForm(){
        setCompositionRoot(buildGridMainLayout());
        branchOfficeService = new BranchOfficeService();
        rolViewContractService = new RolViewContractService();
        branchUserService = new BranchUserService();
        rolViewContract = new RolViewContract();
        postBuild();
    }

    private void postBuild() {
        listCity = new ArrayList<>();
        fillCity();
        fillGridRolViewContract();


        cmbListCity.addValueChangeListener(valueChangeEvent ->{
            branchUser.clear();
            if (txtRolViewContractId.isEmpty())
                fillBranchByCity(cmbListCity.getValue().toString());
            else{
                fillBranchUserByCity(rolViewContract,cmbListCity.getValue().toString().trim());
            }
        });

        btnSaveRol.addClickListener(clickEvent -> {
            if (txtRolViewContractId.isEmpty()) {
                insertRolViewContract();
            } else {
                updateRolViewContract();
            }
            fillGridRolViewContract();
        });

        btnNewRol.addClickListener(clickEvent -> {
            txtRolViewContractId.setReadOnly(true);
            txtRolViewContractId.clear();
            txtRol.clear();
            txtDescription.clear();
            branchUser.clear();
            branchUser.setItems(listBranch);
        });

        gridRolViewContractGrid.addItemClickListener(itemClick ->{
            rolViewContract = itemClick.getItem();
           fillBranchUser(itemClick.getItem());
        });
    }

    private void fillBranchUser(RolViewContract rolViewContract){
        List<String> assignedBranchUser = new ArrayList<>();
        txtRolViewContractId.setReadOnly(false);
        txtRolViewContractId.setValue(rolViewContract.getRolViewContractId().toString());
        txtRolViewContractId.setReadOnly(true);
        txtDescription.setValue(rolViewContract.getDescription());
        txtRol.setValue(rolViewContract.getRolViewContractName());

        BranchUserService branchUserService = new BranchUserService();
        BranchUser branchUser1 = new BranchUser();
        if (branchUserService.findBranchUserByRolViewerId(rolViewContract.getRolViewContractId()).size()!=0) {
            branchUser1 = branchUserService.findBranchUserByRolViewerId(rolViewContract.getRolViewContractId()).get(0);

//            cmbListCity.setValue(branchUser1.getBranchOffice().getCityName().trim());
            cmbListCity.setValue(branchUser1.getCity().trim());
            List<BranchUser> branchUserList = branchUserService.findBranchUserByRolViewerId(rolViewContract.getRolViewContractId());
            for (BranchUser branchUser : branchUserList) {
                assignedBranchUser.add(branchUser.getBranchOfficeId().toString() + "-" + branchUser.getBranchOffice().getBranchName());
            }
            branchUser.clear();

            fillBranchByCity(cmbListCity.getValue().toString());

            Set<String> list1 = new HashSet<String>(listBranch);
            Set<String> list2 = new HashSet<String>(assignedBranchUser);
            branchUser.setItems(list1);
            branchUser.setValue(list2);
        }

    }

    private void fillBranchUserByCity(RolViewContract rolViewContract, String city){
        List<String> assignedBranchUser = new ArrayList<>();

        BranchUserService branchUserService = new BranchUserService();

        List<BranchUser> branchUserList = branchUserService.findBranchUserByRolViewerIdCity(rolViewContract.getRolViewContractId(),city);
        for(BranchUser branchUser:branchUserList){
            assignedBranchUser.add(branchUser.getBranchOfficeId().toString() +"-"+ branchUser.getBranchOffice().getBranchName());
        }
        branchUser.clear();

        fillBranchByCity(cmbListCity.getValue().toString());

        Set<String> list1 = new HashSet<String>(listBranch);
        Set<String> list2 = new HashSet<String>(assignedBranchUser);
        branchUser.setItems(list1);
        branchUser.setValue(list2);

    }


    private void fillGridRolViewContract(){
        if (gridRolViewContractGrid.getHeaderRowCount()>1)
            gridRolViewContractGrid.removeHeaderRow(1);
        gridRolViewContractGrid.removeAllColumns();
        List<RolViewContract> rolViewContractList = rolViewContractService.findAllRolViewContract();
        gridRolViewContractGrid.setItems(rolViewContractList);
        gridRolViewContractGrid.addColumn(RolViewContract::getRolViewContractId).setCaption("ID");
        gridRolViewContractGrid.addColumn(RolViewContract::getRolViewContractName).setCaption("Nombre rol").setId("rolName");
        gridRolViewContractGrid.addColumn(RolViewContract::getDescription).setCaption("Descripcion").setId("description");
        fillFilterGridRolViewContract(gridRolViewContractGrid);
    }

    private void fillFilterGridRolViewContract(final Grid grid){
        this.filterRolViewContract = new GridCellFilter<>(grid);

        this.filterRolViewContract.setTextFilter("rolName",true,false);
        this.filterRolViewContract.setTextFilter("description",true,false);
    }

    private void updateRolViewContract(){
        RolViewContract rolViewContract = new RolViewContract();
        rolViewContract.setRolViewContractId(Integer.parseInt(txtRolViewContractId.getValue()));
        rolViewContract.setRolViewContractName(txtRol.getValue());
        rolViewContract.setDescription(txtDescription.getValue());
        rolViewContractService.updateRolViewContract(rolViewContract);

        branchUserService.deleteBranchUser(Integer.parseInt(txtRolViewContractId.getValue()),cmbListCity.getValue().toString());
        List<BranchUser> branchUserList = fillAssignedBranch(rolViewContract.getRolViewContractId());

        for(BranchUser bs : branchUserList){
            branchUserService.insertBranchUser(bs);
        }
    }

    private void insertRolViewContract(){
        RolViewContract rolViewContract = new RolViewContract();
        rolViewContract.setRolViewContractName(txtRol.getValue());
        rolViewContract.setDescription(txtDescription.getValue());

        rolViewContractService.insertRolViewContract(rolViewContract);

        List<BranchUser> branchUserList = fillAssignedBranch(rolViewContract.getRolViewContractId());


        for(BranchUser bs : branchUserList){
            branchUserService.insertBranchUser(bs);
        }

    }

    private List<BranchUser> fillAssignedBranch(int rolViewContract){
        Object selectedValues = branchUser.getValue();
        List<BranchUser> branchUsers = new ArrayList<>();
        List<String> bs = new ArrayList<>();
        bs.addAll((Collection<? extends String>) selectedValues);
        for(String b:bs){
            BranchUser branchUser = new BranchUser();
            branchUser.setRolViewContractId(rolViewContract);
            branchUser.setBranchOfficeId(getCodBranch(b.toString()));
            branchUser.setCity(cmbListCity.getValue().toString());
            branchUsers.add(branchUser);
        }
        return branchUsers;
    }

    private int getCodBranch(String branch){
        String[] partes = branch.split("-");
        return Integer.parseInt(partes[0]);
    }

    private void fillCity(){
        List<BranchOffice> branchOfficeList = branchOfficeService.findAllCity();

        for(BranchOffice branchOffice:branchOfficeList){
            listCity.add(branchOffice.getCityName().trim());
        }

        cmbListCity.setItems(listCity);

    }

    private void fillBranchByCity(String city){
        List<BranchOffice> branchOfficeList = branchOfficeService.findBranchOfficeByCity(city);
        List<String> branchList = new ArrayList<>();
        for(BranchOffice branchOffice:branchOfficeList){
            branchList.add(Integer.toString(branchOffice.getBranchOfficeId())+"-"+branchOffice.getBranchName() );
        }
        listBranch = branchList;

        branchUser.setItems(branchList);
    }

    private GridLayout buildGridMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setSizeFull();
        gridMainLayout.setColumns(7);
        gridMainLayout.setRows(8);
        gridMainLayout.setSpacing(true);

        gridMainLayout.addComponent(buildHorizontalLayout(),0,0,6,0);

        gridRolViewContractGrid = new Grid<>(RolViewContract.class);
        gridRolViewContractGrid.setStyleName(ValoTheme.TABLE_COMPACT);
        gridRolViewContractGrid.setWidth("100%");


        panelGridRolViewcontract = new Panel();
        panelGridRolViewcontract.setStyleName(ValoTheme.PANEL_WELL);
        panelGridRolViewcontract.setWidth("100%");
        panelGridRolViewcontract.setHeight("200px");
        panelGridRolViewcontract.setContent(gridRolViewContractGrid);
        gridMainLayout.addComponent(panelGridRolViewcontract,0,1,6,2);

        cmbListCity = new ComboBox("Ciudad");
        cmbListCity.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbListCity.setEmptySelectionAllowed(false);
        gridMainLayout.addComponent(cmbListCity,0,3);

        branchUser = new TwinColSelect();
        branchUser.setLeftColumnCaption("Sucursal y agencia disponible");
        branchUser.setRightColumnCaption("Sucursal y agencia asignadas");
        branchUser.setWidth("100%");
        gridMainLayout.addComponent(branchUser,0,4,5,5);

        return gridMainLayout;
    }

    private HorizontalLayout buildHorizontalLayout(){
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        txtRolViewContractId = new TextField("ID:");
        txtRolViewContractId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtRolViewContractId.setReadOnly(true);
        horizontalLayout.addComponent(txtRolViewContractId);

        txtRol = new TextField("Nombre:");
        txtRol.setStyleName(ValoTheme.TEXTFIELD_TINY);
        horizontalLayout.addComponent(txtRol);

        txtDescription = new TextField("Descricion:");
        txtDescription.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtDescription.setWidth("220px");
        horizontalLayout.addComponent(txtDescription);

        btnSaveRol = new Button("Guardar");
        btnSaveRol.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSaveRol.setIcon(VaadinIcons.DATABASE);
        horizontalLayout.addComponent(btnSaveRol);
        horizontalLayout.setComponentAlignment(btnSaveRol,Alignment.BOTTOM_LEFT);


        btnNewRol = new Button("Nuevo");
        btnNewRol.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnNewRol.setIcon(VaadinIcons.PLUS);
        horizontalLayout.addComponent(btnNewRol);
        horizontalLayout.setComponentAlignment(btnNewRol,Alignment.BOTTOM_LEFT);

        return horizontalLayout;
    }

}
