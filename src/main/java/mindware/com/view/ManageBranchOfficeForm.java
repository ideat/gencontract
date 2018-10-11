package mindware.com.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.BranchOffice;
import mindware.com.model.Signatories;
import mindware.com.netbank.model.BranchOfficeNetbank;
import mindware.com.netbank.service.BranchOfficeNetbankService;
import mindware.com.service.BranchOfficeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ManageBranchOfficeForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private Grid<Signatories> gridSignatories;
    private Grid<BranchOffice> gridBranchOffice;
    private Button btnImport;
    private Button btnAddSignatories;
    private Button btnEditSignatorie;
    private Panel panelBranchOffice;
    private Panel panelSignatories;

    private String currentSignatories;
    private Signatories signatorieSelected;
    private Integer branchOfficeIdSelected;
    private BranchOfficeService branchOfficeService;
    private List<Signatories> signatoriesList = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    public ManageBranchOfficeForm(){

        setCompositionRoot(buildMainLayout());
        postBuild();
        branchOfficeService = new BranchOfficeService();
        loadBranchOffice(branchOfficeService.findAllBranchOffice());

    }

    private void postBuild(){

        btnImport.addClickListener(clickEvent -> {
            BranchOfficeNetbankService branchOfficeNetbankService = new BranchOfficeNetbankService();
            List<BranchOfficeNetbank> branchOfficeNetbanksList = branchOfficeNetbankService.findAllBranchOfficeNetbank();
            List<BranchOffice> branchOfficeList = new ArrayList<>();
            List<Signatories> signatories = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for(BranchOfficeNetbank branchOfficeNetbank:branchOfficeNetbanksList){
                BranchOffice branchOffice = new BranchOffice();
                branchOffice.setBranchOfficeId(branchOfficeNetbank.getGbofinofi());
                branchOffice.setBranchName(branchOfficeNetbank.getGbofidesc());
                branchOffice.setCityName(branchOfficeNetbank.getGbdptdesc());
                branchOffice.setProvinceName(branchOfficeNetbank.getGbprvdesc());
                branchOffice.setAddress(String.valueOf(branchOfficeNetbank.getGbofidire()));

                String jsonStr= null;
                try {
                    jsonStr = objectMapper.writeValueAsString(signatories);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                branchOffice.setSignatories(jsonStr);
                branchOfficeList.add(branchOffice);
            }

            List<BranchOffice> branchOfficeListLocal = new ArrayList<>();
            branchOfficeListLocal.addAll(branchOfficeService.findAllBranchOffice());
            if (branchOfficeListLocal.isEmpty())
                branchOfficeService.insertListBranchOffice(branchOfficeList);
            else {
                List<BranchOffice> branchOffices = new ArrayList<>();

                for(BranchOffice branchOffice:branchOfficeList){
                    boolean find = false;
                    for(BranchOffice branchOffice1:branchOfficeListLocal){
                        if (branchOffice.getBranchOfficeId()== branchOffice1.getBranchOfficeId() )
                            find =true;
                    }
                    if (find==false)
                        branchOffices.add(branchOffice);
                }
                if (branchOffices.size()>0)
                    branchOfficeService.insertListBranchOffice(branchOffices);
            }

            loadBranchOffice(branchOfficeService.findAllBranchOffice());

        });

        btnAddSignatories.addClickListener(clickEvent -> {
           SignatorieWindowForm signatorieWindowForm = new SignatorieWindowForm(branchOfficeIdSelected,currentSignatories,signatorieSelected,"INSERT");
           signatorieWindowForm.setModal(true);
           signatorieWindowForm.setWidth("350px");
           signatorieWindowForm.setHeight("700px");
           signatorieWindowForm.center();
           UI.getCurrent().addWindow(signatorieWindowForm);
           signatorieWindowForm.addCloseListener(closeEvent -> {
                loadSignatorie(branchOfficeIdSelected);
           });

        });

        gridBranchOffice.addItemClickListener(event -> {
            BranchOffice branchOffice =  event.getItem();
            branchOfficeIdSelected = branchOffice.getBranchOfficeId();
            loadSignatorie(branchOfficeIdSelected);
        });

        btnEditSignatorie.addClickListener(clickEvent -> {
            SignatorieWindowForm signatorieWindowForm = new SignatorieWindowForm(branchOfficeIdSelected,currentSignatories,signatorieSelected,"EDIT");
            signatorieWindowForm.setModal(true);
            signatorieWindowForm.setWidth("350px");
            signatorieWindowForm.setHeight("700px");
            signatorieWindowForm.center();
            UI.getCurrent().addWindow(signatorieWindowForm);
            signatorieWindowForm.addCloseListener(closeEvent -> {
                loadSignatorie(branchOfficeIdSelected);
            });

        });

        gridSignatories.addItemClickListener(event -> {
           signatorieSelected = event.getItem();

        });
    }

    private void loadSignatorie(int branchOfficeId){
//        BranchOfficeService branchOfficeService = new BranchOfficeService();
        BranchOffice branchOffice = branchOfficeService.findSignatorieByBranchOffice(branchOfficeId);
        currentSignatories = branchOffice.getSignatories();
        if (currentSignatories.equals("[]") || (currentSignatories.isEmpty())){
            gridSignatories.removeAllColumns();
            headerEmptyGridSignatorie();
            gridSignatories.setItems(emptySignatorie());
            gridSignatories.getEditor().setEnabled(true);
        }else {
            gridSignatories.removeAllColumns();
            List<Signatories> signatoriesList = new ArrayList<>();
            try {
                signatoriesList = Arrays.asList(mapper.readValue(currentSignatories,Signatories[].class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            fillGridSignatories(signatoriesList);

        }

    }


    private List<Signatories> emptySignatorie(){
        List<Signatories> signatoriesList = new ArrayList<>();
        Signatories signatories = new Signatories();
        signatories.setSignatorieId(0);
        signatories.setNameSignatorie("");
        signatories.setIdentifyCardSignatorie("");
        signatories.setPosition("");
        signatories.setStatus("");
        signatories.setNroPoder("");
        signatories.setFechaPoder("");
        signatories.setNroNotaria("");
        signatories.setNombreNotario("");
        signatories.setDistritoJudicial("");
        signatories.setNroTestimonio("");
        signatories.setFechaTestimonio("");

        signatoriesList.add(signatories);
        return signatoriesList;
    }

    private void headerEmptyGridSignatorie(){
        gridSignatories.addColumn(Signatories::getSignatorieId).setCaption("ID");
        gridSignatories.addColumn(Signatories::getNameSignatorie).setCaption("Nombre");
        gridSignatories.addColumn(Signatories::getIdentifyCardSignatorie).setCaption("Carnet");
        gridSignatories.addColumn(Signatories::getPosition).setCaption("Cargo");
        gridSignatories.addColumn(Signatories::getStatus).setCaption("Estado");
        gridSignatories.addColumn(Signatories::getNroPoder).setCaption("Nro Poder");
        gridSignatories.addColumn(Signatories::getFechaPoder).setCaption("Fecha Poder");
        gridSignatories.addColumn(Signatories::getNroNotaria).setCaption("Nro Notaria");
        gridSignatories.addColumn(Signatories::getNombreNotario).setCaption("Nombre Notario");
        gridSignatories.addColumn(Signatories::getDistritoJudicial).setCaption("Distrito Judicial");
        gridSignatories.addColumn(Signatories::getNroTestimonio).setCaption("Nro Testimonio");
        gridSignatories.addColumn(Signatories::getFechaTestimonio).setCaption("Fecha Testimonio");

    }

    private void fillGridSignatories(List<Signatories> signatoriesList){
       gridSignatories.setItems(signatoriesList);
       gridSignatories.addColumn(Signatories::getSignatorieId).setCaption("ID");
       gridSignatories.addColumn(Signatories::getNameSignatorie).setCaption("Nombre");
       gridSignatories.addColumn(Signatories::getIdentifyCardSignatorie).setCaption("Carnet");
       gridSignatories.addColumn(Signatories::getPosition).setCaption("Cargo");
       gridSignatories.addColumn(Signatories::getStatus).setCaption("Estado");
        gridSignatories.addColumn(Signatories::getNroPoder).setCaption("Nro Poder");
        gridSignatories.addColumn(Signatories::getFechaPoder).setCaption("Fecha Poder");
        gridSignatories.addColumn(Signatories::getNroNotaria).setCaption("Nro Notaria");
        gridSignatories.addColumn(Signatories::getNombreNotario).setCaption("Nombre Notario");
        gridSignatories.addColumn(Signatories::getDistritoJudicial).setCaption("Distrito Judicial");
        gridSignatories.addColumn(Signatories::getNroTestimonio).setCaption("Nro Testimonio");
        gridSignatories.addColumn(Signatories::getFechaTestimonio).setCaption("Fecha Testimonio");

    }


    private void loadBranchOffice(List<BranchOffice> branchOfficeList){
        gridBranchOffice.removeAllColumns();
        gridBranchOffice.setItems(branchOfficeList);
        gridBranchOffice.addColumn(BranchOffice::getBranchOfficeId).setCaption("Cod. Agencia");
        gridBranchOffice.addColumn(BranchOffice::getBranchName).setCaption("Nombre");
        gridBranchOffice.addColumn(BranchOffice::getCityName).setCaption("Departamento");
        gridBranchOffice.addColumn(BranchOffice::getProvinceName).setCaption("Provincia");
        gridBranchOffice.addColumn(BranchOffice::getAddress).setCaption("Direccion");
        gridBranchOffice.addColumn(BranchOffice::getSignatories).setHidden(true);
    }

    private GridLayout buildMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(6);
        gridMainLayout.setRows(4);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        gridMainLayout.addComponent(buildPanelBranchOffice(),0,0,4,1);
        btnImport = new Button("Importar");
        btnImport.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnImport.setIcon(VaadinIcons.AUTOMATION);
        btnImport.setWidth("120px");
        gridMainLayout.addComponent(btnImport,5,0);

        gridMainLayout.addComponent(buildPanelSiganotories(),0,2,4,3);
        btnAddSignatories = new Button("Insertar");
        btnAddSignatories.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnAddSignatories.setIcon(VaadinIcons.PLUS);
        btnAddSignatories.setWidth("120px");
        gridMainLayout.addComponent(btnAddSignatories,5,2);

        btnEditSignatorie = new Button("Editar");
        btnEditSignatorie.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnEditSignatorie.setIcon(VaadinIcons.PENCIL);
        btnEditSignatorie.setWidth("120px");
        gridMainLayout.addComponent(btnEditSignatorie,5,3);
        gridMainLayout.setComponentAlignment(btnEditSignatorie,Alignment.TOP_LEFT);


        return gridMainLayout;
    }



    private Panel buildPanelBranchOffice(){
        panelBranchOffice = new Panel("<font size=3 color=#163759> Datos Agencias <font>");
        panelBranchOffice.setCaptionAsHtml(true);
        panelBranchOffice.setStyleName(ValoTheme.PANEL_WELL);
        panelBranchOffice.setHeight("215px");

        gridBranchOffice = new Grid();
        gridBranchOffice.setStyleName(ValoTheme.TABLE_SMALL);
        gridBranchOffice.setSizeFull();

        panelBranchOffice.setContent(gridBranchOffice);


        return panelBranchOffice;
    }

    private Panel buildPanelSiganotories(){
        panelSignatories = new Panel("<font size=3 color=#163759> Firmantes por la Agencia <font>");
        panelSignatories.setCaptionAsHtml(true);
        panelSignatories.setStyleName(ValoTheme.PANEL_WELL);
        panelSignatories.setHeight("170px");

        gridSignatories = new Grid();
        gridSignatories.setStyleName(ValoTheme.TABLE_SMALL);
        gridSignatories.setSizeFull();
        gridSignatories.getEditor().setEnabled(true);
        gridSignatories.setSelectionMode(Grid.SelectionMode.SINGLE);


        panelSignatories.setContent(gridSignatories);

        return panelSignatories;
    }

}
