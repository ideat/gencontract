package mindware.com.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.renderers.TextRenderer;
import mindware.com.model.CoDebtorGuarantor;

import mindware.com.model.LoanData;
import mindware.com.model.Warranty;
import mindware.com.netbank.model.ClientLoanNetbank;
import mindware.com.netbank.model.CodebtorGuarantorNetbank;
import mindware.com.netbank.model.WarrantyNetbank;
import mindware.com.netbank.service.ClientLoanNetBankService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.netbank.service.CodebtorGuarantorNetbankService;
import mindware.com.netbank.service.WarrantyNetBankService;
import mindware.com.service.LoanDataService;
import mindware.com.utilities.Util;


import java.util.ArrayList;
import java.util.List;

public class ImportDataForm extends CustomComponent implements View {
    private Button btnSearch;
    private Button btnImport;
    private TextField txtLoanNumberSearch;
    private TextField txtLoanNumber;
    private DateField dateLoanDate; //*
    private TextField txtCurrency;
    private TextField txtLoanMount;
    private TextField txtLoanTerm;
    private TextField txtInterestRate;
    private TextField txtTreRate; //*
    private TextField txtTeacRate; //*
    private TextField txtFeePayment; //*
    private TextField txtPaymentFrecuency;
    private TextField txtCreditLifeInsurance;
    private TextField txtTotalPayment; //*
    private TextField txtLoanDestination; //*
    private TextField txtAgency;
    private TextField txtFixedPaymentDay;
    private TextField txtOfficial;
    private TextField txtDebtorName;
    private TextField txtIdentifyCardDebtor;
    private TextField txtAddressDebtor;
    private TextField txtCivilStatusDebtor;
    private TextField txtGenderDebtor;
    private TextField txtClientLoanId;

    private Grid<CodebtorGuarantorNetbank> gridCoDebtor;
    private Grid<CodebtorGuarantorNetbank> gridGuarantor;

    private GridLayout gridLayout;
    private Panel panelLoan;
    private Panel panelClient;
    private Panel panelWarranty;
    private Panel panelSupplentaryData;
    private Panel panelCoDebtor;
    private Panel panelGuarantor;
    private TabSheet tabSupplementaryData;
    private GridLayout gridLayoutSupplementaryData;
    private Button btnSaveSupplementaryData;
//    private VerticalLayout verticalLayoutCoDebtor;
//    private VerticalLayout verticalLayoutWarranty;
//    private VerticalLayout verticalLayoutGuarantors;
    private  Grid<WarrantyNetbank> gridWarranty;

    private ClientLoanNetbank clientLoanNetbank;
    private List<WarrantyNetbank> warrantyNetbankList;
    private List<CodebtorGuarantorNetbank> codebtorNetbankList;
    private List<CodebtorGuarantorNetbank> guarantorNetbankList;

    public ImportDataForm(){
        setCompositionRoot(buildGridLayout());
        postBuild();
    }

    private void postBuild(){

        btnSearch.addClickListener(clickEvent -> {
            clearFieldNetbankData();
            ClientLoanNetBankService clientLoanNetBankService = new ClientLoanNetBankService();
            clientLoanNetbank = clientLoanNetBankService.findClientNetbankById(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
            WarrantyNetBankService warrantyNetBankService = new WarrantyNetBankService();
            warrantyNetbankList = warrantyNetBankService.findWarrantyNetbankByCreCod(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
            CodebtorGuarantorNetbankService codebtorGuarantorNetbankService = new CodebtorGuarantorNetbankService();
            codebtorNetbankList = codebtorGuarantorNetbankService.findCodeptorByNumberLoan(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
            guarantorNetbankList= codebtorGuarantorNetbankService.findGuarantorByNumberLoan(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
            loadCodebtorNetbank(codebtorNetbankList);
            loadGuarantorNetbank(guarantorNetbankList);
            loadNetBankData(clientLoanNetbank);
            loadWarrantyNetBank(warrantyNetbankList);

        });

        btnImport.addClickListener(clickEvent -> {
            LoanData loanData = new LoanData();
            loanData.setLoanNumber(Integer.parseInt(txtLoanNumber.getValue()));
            loanData.setCurrency(txtCurrency.getValue());
            loanData.setLoanMount(Double.parseDouble(txtLoanMount.getValue()));
            loanData.setLoanTerm(Integer.parseInt(txtLoanTerm.getValue()));
            loanData.setInterestRate(Double.parseDouble(txtInterestRate.getValue()));
            loanData.setCreditLifeInsurance(Double.parseDouble(txtCreditLifeInsurance.getValue()));
            loanData.setFixedPaymentDay(Integer.parseInt(txtFixedPaymentDay.getValue()));
            loanData.setPaymentFrecuency(txtPaymentFrecuency.getValue());
            loanData.setOfficial(txtOfficial.getValue());
            loanData.setAgency(txtAgency.getValue());

            ObjectMapper mapper = new ObjectMapper();
            String jsonCodebtor = null;
            try {
                jsonCodebtor = mapper.writeValueAsString(getCoDebtorsGuarantors("codebor",codebtorNetbankList));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            String jsonGuarantor = null;
            try {
                jsonGuarantor = mapper.writeValueAsString(getCoDebtorsGuarantors("guarantor",guarantorNetbankList));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            String jsonWarranty = null;
            try {
                jsonWarranty = mapper.writeValueAsString(getWarranty(warrantyNetbankList));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            loanData.setGuarantors(jsonGuarantor);
            loanData.setCoDebtors(jsonCodebtor);
            loanData.setWarranty(jsonWarranty);

            LoanDataService loanDataService = new LoanDataService();
            loanDataService.insertLoanData(loanData);

        });

        btnSaveSupplementaryData.addClickListener(clickEvent -> {

        });
    }

    private List<Warranty> getWarranty(List<WarrantyNetbank> warrantyNetbanks){
        List<Warranty> warrantyList = new ArrayList<>();
        Util util = new Util();
        int i=1;
        for(WarrantyNetbank warrantyNetbank:warrantyNetbanks){
            Warranty warranty = new Warranty();
            warranty.setId(i);
            warranty.setLoanNumber(warrantyNetbank.getPrgarnpre());
            warranty.setTypeGuarantee(warrantyNetbank.getGbtgadesc());
            warranty.setCurrency(warrantyNetbank.getPrgarcmon());
            warranty.setAssessmentEntity(warrantyNetbank.getPrgargfin());
            if (!warrantyNetbank.getPrgarfvto().equals("-"))
                warranty.setExpirationDate(util.stringToDate(warrantyNetbank.getPrgarfvto(),"yyyy-MM-dd"));
            warranty.setNumberRealRight(warrantyNetbank.getPrgarnpar());
            if (!warrantyNetbank.getPrgarfpar().equals("-"))
                warranty.setDateRealRight(util.stringToDate(warrantyNetbank.getPrgarfpar(),"yyyy-MM-dd"));
            warranty.setMortageNumber(warrantyNetbank.getPrgarnhip());
            if(!warrantyNetbank.getPrgarfhip().equals("-"))
                warranty.setDateMortage(util.stringToDate(warrantyNetbank.getPrgarfhip(),"yyyy-MM-dd"));
            warranty.setDescription(warrantyNetbank.getPrgardesc());
            warranty.setEnoughGuarante(warrantyNetbank.getPrgarsufl());
            warrantyList.add(warranty);
            i=+1;
        }

        return warrantyList;
    }

    private List<CoDebtorGuarantor> getCoDebtorsGuarantors(String type, List<CodebtorGuarantorNetbank> codebtorGuarantorNetbanks){
        List<CoDebtorGuarantor> coDebtorList = new ArrayList<>();
        for(CodebtorGuarantorNetbank codebtorGuarantorNetbank: codebtorGuarantorNetbanks){
            CoDebtorGuarantor coDebtorGuarantor = new CoDebtorGuarantor();
            coDebtorGuarantor.setCodeMebership(codebtorGuarantorNetbank.getPrdeucage());
            coDebtorGuarantor.setNumberLoan(codebtorGuarantorNetbank.getPrdeunpre());
            coDebtorGuarantor.setType(type);
            coDebtorGuarantor.setName(codebtorGuarantorNetbank.getGbagenomb());
            coDebtorGuarantor.setIdentifyCard(codebtorGuarantorNetbank.getGbagendid());
            coDebtorGuarantor.setAddressOffice(codebtorGuarantorNetbank.getGbageddo());
            coDebtorGuarantor.setAddressHome(codebtorGuarantorNetbank.getGbagedir());
            coDebtorGuarantor.setCivilStatus(codebtorGuarantorNetbank.getGbageeciv());
            coDebtorGuarantor.setGender(codebtorGuarantorNetbank.getGbagesexo());

            coDebtorList.add(coDebtorGuarantor);

        }

        return coDebtorList;
    }

    private void loadWarrantyNetBank(List<WarrantyNetbank> warrantyNetbankList){
        gridWarranty.removeAllColumns();
        gridWarranty.setItems(warrantyNetbankList);
        gridWarranty.addColumn(WarrantyNetbank::getPrgarnpre).setCaption("Nro prestamo");
        gridWarranty.addColumn(WarrantyNetbank::getGbtgadesc).setCaption("Tipo garantia");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarcmon).setCaption("Moneda");
        gridWarranty.addColumn(WarrantyNetbank::getPrgargfin).setCaption("Grav. Fav. Entidad");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarfvto,new TextRenderer("-")).setCaption("Fecha venc");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarnpar,new TextRenderer("-")).setCaption("Nro. Part. DDRR");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarfpar,new TextRenderer("-")).setCaption("Fecha partida DDRR");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarnhip, new TextRenderer("-")).setCaption("Nro hip.");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarfhip, new TextRenderer("-")).setCaption("Fecha hip.");
        gridWarranty.addColumn(WarrantyNetbank::getPrgardesc, new TextRenderer("-")).setCaption("Descripcion");
        gridWarranty.addColumn(WarrantyNetbank::getPrgarsufl, new TextRenderer("-")).setCaption("Suf. liquida");
    }



    private void loadCodebtorNetbank(List<CodebtorGuarantorNetbank> coDebtorList){
        gridCoDebtor.removeAllColumns();
        gridCoDebtor.setItems(coDebtorList);

        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getPrdeunpre).setCaption("Nro prestamo");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getPrdeucage).setCaption("Nro agenda");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagenomb).setCaption("Nombre");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagendid).setCaption("Carnet");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagedir).setCaption("Dir. domicilio");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbageddo).setCaption("Dir. oficina");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbageeciv).setCaption("Estado civil");
        gridCoDebtor.addColumn(CodebtorGuarantorNetbank::getGbagesexo).setCaption("Genero");

    }

    private void loadGuarantorNetbank(List<CodebtorGuarantorNetbank> guarantorList){
        gridGuarantor.removeAllColumns();
        gridGuarantor.setItems(guarantorList);
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getPrdeunpre).setCaption("Nro prestamo");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getPrdeucage).setCaption("Nro agenda");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagenomb).setCaption("Nombre");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagendid).setCaption("Carnet");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagedir).setCaption("Dir. domicilio");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbageddo).setCaption("Dir. oficina");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbageeciv).setCaption("Estado civil");
        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagesexo).setCaption("Genero");
    }

    private void loadNetBankData(ClientLoanNetbank clientLoanNetbank){
        fieldClientStatus(false);

        txtClientLoanId.setValue(clientLoanNetbank.getPrmprcage().toString());
        txtIdentifyCardDebtor.setValue(clientLoanNetbank.getGbagendid());
        txtDebtorName.setValue(clientLoanNetbank.getGbagenomb());
        String civilStatus = null;
        switch (Integer.parseInt(clientLoanNetbank.getGbageeciv().toString())) {
            case 1:
                civilStatus = "SOLTERO";
                break;
            case 2:
                civilStatus = "CASADO";
                break;
            case 3:
                civilStatus = "DIVORCIADO";
                break;
            case 4:
                civilStatus = "VIUDO";
                break;
        }

        txtCivilStatusDebtor.setValue(civilStatus);
        String gender = null;
        switch (Integer.parseInt(clientLoanNetbank.getGbagesexo().toString())){
            case 1:
                gender = "MASCULINO";
                break;
            case 2:
                gender = "FEMENINO";
                break;
        }
        txtGenderDebtor.setValue(gender);
        txtAddressDebtor.setValue(clientLoanNetbank.getGbagedir());

        //Load Loan Data
        txtLoanNumber.setValue(clientLoanNetbank.getPrmprnpre().toString());
        String currency=null;
        switch (Integer.parseInt(clientLoanNetbank.getPrmprcmon().toString())){
            case 1:
                currency = "BS";
                break;
            case 2:
                currency ="$us";
                break;
        }

        txtCurrency.setValue(currency);
        txtLoanMount.setValue(clientLoanNetbank.getPrmprmdes().toString());
        txtLoanTerm.setValue(clientLoanNetbank.getPrmprplzo().toString());
        txtInterestRate.setValue(clientLoanNetbank.getPrtsatbas().toString());
        txtFixedPaymentDay.setValue(clientLoanNetbank.getPrmprdiap().toString());
        txtPaymentFrecuency.setValue(clientLoanNetbank.getPrmprppgk().toString());
        fieldClientStatus(true);

    }

    private void clearFieldNetbankData(){
        fieldClientStatus(false);
        txtClientLoanId.clear();
        txtIdentifyCardDebtor.clear();
        txtDebtorName.clear();
        txtCivilStatusDebtor.clear();
        txtGenderDebtor.clear();
        txtAddressDebtor.clear();
        txtLoanNumber.clear();
        txtCurrency.clear();
        txtLoanMount.clear();
        txtLoanTerm.clear();
        txtInterestRate.clear();
        txtFixedPaymentDay.clear();
        txtPaymentFrecuency.clear();
        fieldClientStatus(true);
    }

    private void fieldClientStatus(boolean read) {
        txtClientLoanId.setReadOnly(read);
        txtIdentifyCardDebtor.setReadOnly(read);
        txtDebtorName.setReadOnly(read);
        txtCivilStatusDebtor.setReadOnly(read);
        txtGenderDebtor.setReadOnly(read);
        txtAddressDebtor.setReadOnly(read);

        txtLoanNumber.setReadOnly(read);
        txtCurrency.setReadOnly(read);
        txtLoanMount.setReadOnly(read);
        txtLoanTerm.setReadOnly(read);
        txtInterestRate.setReadOnly(read);
        txtFixedPaymentDay.setReadOnly(read);
        txtPaymentFrecuency.setReadOnly(read);

    }

    private GridLayout buildGridLayout(){
        gridLayout = new GridLayout();
        gridLayout.setColumns(10);
        gridLayout.setRows(9);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();

        txtLoanNumberSearch =new TextField("Numero credito:");
        txtLoanNumberSearch.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        txtLoanNumberSearch.setPlaceholder("Ingrese credito");
        gridLayout.addComponent(txtLoanNumberSearch,0,0);

        btnSearch =new Button("Buscar");
        btnSearch.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSearch.setIcon(VaadinIcons.SEARCH);
        gridLayout.addComponent(btnSearch,1,0);
        gridLayout.setComponentAlignment(btnSearch,Alignment.BOTTOM_LEFT);

        btnImport = new Button("Importar");
        btnImport.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnImport.setIcon(VaadinIcons.DATABASE);
        gridLayout.addComponent(btnImport,2,0);
        gridLayout.setComponentAlignment(btnImport,Alignment.BOTTOM_LEFT);

        gridLayout.addComponent(buildClientPanel(),0,1,9,3);
        gridLayout.addComponent(buildLoanPanel(),0,4,9,6);
        gridLayout.addComponent(buildTabSupplementaryData(),0,7,9,8);


        return gridLayout;
    }

    private Panel buildLoanPanel(){
        panelLoan = new Panel( );//"<font size=3 color=#163759> Datos generales del credito<font>");
        panelLoan.setHeight("145px");
        panelLoan.setCaptionAsHtml(true);
        panelLoan.setStyleName(ValoTheme.PANEL_WELL);
        GridLayout gridLayoutLoan = new GridLayout();
        gridLayoutLoan.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutLoan.setColumns(8);
        gridLayoutLoan.setRows(5);
        gridLayoutLoan.setHeight("110px");
        gridLayoutLoan.setSpacing(true);

        txtLoanNumber = new TextField("Nro. credito:");
        txtLoanNumber.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanNumber.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtLoanNumber,0,0);

        txtCurrency = new TextField("Moneda:");
        txtCurrency.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCurrency.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtCurrency,1,0);

        txtLoanMount = new TextField("Monto:");
        txtLoanMount.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanMount.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtLoanMount,2,0);

        txtLoanTerm = new TextField("Plazo:");
        txtLoanTerm.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanTerm.setReadOnly(true);;
        gridLayoutLoan.addComponent(txtLoanTerm,3,0);

        txtInterestRate = new TextField("Tasa:");
        txtInterestRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtInterestRate.setReadOnly(true);;

        gridLayoutLoan.addComponent(txtInterestRate,4,0);

        txtCreditLifeInsurance = new TextField("Seguro desgravamen:");
        txtCreditLifeInsurance.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCreditLifeInsurance.setReadOnly(true);
        gridLayoutLoan.addComponent(txtCreditLifeInsurance,5,0);

        txtFixedPaymentDay = new TextField("Dia fijo de pago:");
        txtFixedPaymentDay.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtFixedPaymentDay.setReadOnly(true);
        gridLayoutLoan.addComponent(txtFixedPaymentDay,0,1);

        txtPaymentFrecuency = new TextField("Frecuencia pago:");
        txtPaymentFrecuency.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtPaymentFrecuency.setReadOnly(true);
        gridLayoutLoan.addComponent(txtPaymentFrecuency,1,1);

        txtOfficial = new TextField("Oficial:");
        txtOfficial.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtOfficial.setReadOnly(true);
        gridLayoutLoan.addComponent(txtOfficial,2,1);

        txtAgency = new TextField("Agencia:");
        txtAgency.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtAgency.setReadOnly(true);
        gridLayoutLoan.addComponent(txtAgency,3,1);

        panelLoan.setContent(gridLayoutLoan);


        return panelLoan;
    }

    private Panel buildClientPanel(){
        panelClient = new Panel("<font size=3 color=#163759> Datos cliente y credito <font>");
        panelClient.setCaptionAsHtml(true);
        panelClient.setStyleName(ValoTheme.PANEL_WELL);
        panelClient.setHeight("105px");

        GridLayout gridLayoutClient = new GridLayout();
        gridLayoutClient.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutClient.setColumns(8);
        gridLayoutClient.setRows(5);
        gridLayoutClient.setSpacing(true);
        gridLayoutClient.setSizeFull();

        txtClientLoanId = new TextField("Codigo agenda:");
        txtClientLoanId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtClientLoanId.setReadOnly(true);
        gridLayoutClient.addComponent(txtClientLoanId,0,0);

        txtIdentifyCardDebtor = new TextField("Carnet:");
        txtIdentifyCardDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtIdentifyCardDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtIdentifyCardDebtor,1,0);

        txtDebtorName = new TextField("Nombre deudor:");
        txtDebtorName.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtDebtorName.setReadOnly(true);
        gridLayoutClient.addComponent(txtDebtorName,2,0);

        txtCivilStatusDebtor = new TextField("Estado civil:");
        txtCivilStatusDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtCivilStatusDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtCivilStatusDebtor,3,0);

        txtGenderDebtor = new TextField("Genero:");
        txtGenderDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtGenderDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtGenderDebtor,4,0);

        txtAddressDebtor = new TextField("Direccion:");
        txtAddressDebtor.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtAddressDebtor.setReadOnly(true);
        gridLayoutClient.addComponent(txtAddressDebtor,5,0);

        panelClient.setContent(gridLayoutClient);

        return panelClient;
    }

    private TabSheet buildTabSupplementaryData(){
        tabSupplementaryData = new TabSheet();
        tabSupplementaryData.setStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSupplementaryData.addTab(buildPanelSupplentaryData(),"Datos complementarios");
        tabSupplementaryData.addTab(buildPanelCodDebtor(),"Codeudores");
        tabSupplementaryData.addTab(buildPanelWarranty(),"Garantias");
        tabSupplementaryData.addTab(buildPanelGuarantor(),"Garantes");


        return tabSupplementaryData;
    }

    private Panel buildPanelSupplentaryData(){
        panelSupplentaryData = new Panel();
        panelSupplentaryData.setStyleName(ValoTheme.PANEL_WELL);
        panelSupplentaryData.setWidth("100%");

        gridLayoutSupplementaryData = new GridLayout();
        gridLayoutSupplementaryData.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLayoutSupplementaryData.setRows(4);
        gridLayoutSupplementaryData.setColumns(7);
        gridLayoutSupplementaryData.setSpacing(true);

        dateLoanDate = new DateField("Fecha desembolso:");
        dateLoanDate.setStyleName(ValoTheme.DATEFIELD_TINY);
        dateLoanDate.setDateFormat("dd-MM-yyyy");
        gridLayoutSupplementaryData.addComponent(dateLoanDate,0,0);

        txtTreRate = new TextField("Tasa TRE:");
        txtTreRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtTreRate,1,0);

        txtTeacRate = new TextField("Tasa TEAC:");
        txtTeacRate.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtTeacRate,2,0);

        txtFeePayment = new TextField("Cuota:");
        txtFeePayment.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtFeePayment,3,0);

        txtTotalPayment = new TextField("Total a cancelar:");
        txtTotalPayment.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridLayoutSupplementaryData.addComponent(txtTotalPayment,4,0);

        txtLoanDestination = new TextField("Destino del credito:");
        txtLoanDestination.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanDestination.setWidth("360px");
        gridLayoutSupplementaryData.addComponent(txtLoanDestination,0,1,3,1);

        btnSaveSupplementaryData = new Button("Guardar");
        btnSaveSupplementaryData.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSaveSupplementaryData.setIcon(VaadinIcons.SAFE);
        gridLayoutSupplementaryData.addComponent(btnSaveSupplementaryData,0,2);

        panelSupplentaryData.setContent(gridLayoutSupplementaryData);

        return panelSupplentaryData;
    }

    private Panel buildPanelWarranty(){
        panelWarranty = new Panel();
        panelWarranty.setStyleName(ValoTheme.PANEL_WELL);
        panelWarranty.setWidth("100%");
        panelWarranty.setHeight("180px");

        gridWarranty = new Grid<WarrantyNetbank>();
        gridWarranty.setSizeFull();
        gridWarranty.setStyleName(ValoTheme.TABLE_COMPACT);

        panelWarranty.setContent(gridWarranty);

        return panelWarranty;
    }

    private Panel buildPanelCodDebtor(){
        panelCoDebtor = new Panel();
        panelCoDebtor.setStyleName(ValoTheme.PANEL_WELL);
        panelCoDebtor.setWidth("100%");
        panelCoDebtor.setHeight("180px");

        gridCoDebtor = new Grid();
        gridCoDebtor.setStyleName(ValoTheme.TABLE_SMALL);
        gridCoDebtor.setSizeFull();

        panelCoDebtor.setContent(gridCoDebtor);

        return panelCoDebtor;
    }

    private Panel buildPanelGuarantor(){
        panelGuarantor = new Panel();
        panelGuarantor.setStyleName(ValoTheme.PANEL_WELL);
        panelGuarantor.setWidth("100%");
        panelGuarantor.setHeight("180px");

        gridGuarantor = new Grid();
        gridGuarantor.setStyleName(ValoTheme.TABLE_SMALL);
        gridGuarantor.setSizeFull();

        panelGuarantor.setContent(gridGuarantor);
        return panelGuarantor;
    }

}
