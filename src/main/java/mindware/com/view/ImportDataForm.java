package mindware.com.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.renderers.TextRenderer;
import de.steinwedel.messagebox.MessageBox;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    private Grid<?> gridCoDebtor;
    private Grid<?> gridGuarantor;

    private GridLayout gridLayout;
    private Panel panelLoan;
    private Panel panelClient;
    private Panel panelWarranty;
    private Panel panelSupplentaryData;
    private Panel panelCoDebtor;
    private Panel panelGuarantor;
    private TabSheet tabSupplementaryData;
    private GridLayout gridLayoutSupplementaryData;

    private  Grid<?> gridWarranty;

    private ClientLoanNetbank clientLoanNetbank;
    private List<WarrantyNetbank> warrantyNetbankList;
    private List<CodebtorGuarantorNetbank> codebtorNetbankList;
    private List<CodebtorGuarantorNetbank> guarantorNetbankList;
    private Integer loanDataId;
    private boolean local;

    public ImportDataForm(){
        setCompositionRoot(buildGridLayout());
        postBuild();
    }

    private void postBuild(){

        btnSearch.addClickListener(clickEvent -> {
            LoanDataService loanDataService = new LoanDataService();
            if (!txtLoanNumberSearch.isEmpty()) {
                LoanData loanData = loanDataService.findLoanDataByLoanNumber(Integer.parseInt(txtLoanNumberSearch.getValue()));
                if (loanData != null){
                    MessageBox.createQuestion()
                            .withCaption("Advertencia")
                            .withMessage("Credito ya fue importado, desea volver a cargar desde el servidor? ")
                            .withYesButton(()->{
                                clearFieldData();
                                ClientLoanNetBankService clientLoanNetBankService = new ClientLoanNetBankService();
                                clientLoanNetbank = clientLoanNetBankService.findClientNetbankById(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
                                dataFromNetBank();
                                loanDataId = loanData.getLoanDataId();
                                btnImport.setEnabled(true);
                            })
                            .withNoButton( ()->{
                                loadLoanDataLocal(loanData);
                                btnImport.setEnabled(false);

                            })
                            .open();


                } else {
                    clearFieldData();
                    ClientLoanNetBankService clientLoanNetBankService = new ClientLoanNetBankService();
                    clientLoanNetbank = clientLoanNetBankService.findClientNetbankById(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
                    if (clientLoanNetbank != null) {
                        dataFromNetBank();
                        btnImport.setEnabled(true);
                    } else {
                        Notification.show("Datos credito",
                                "Credito no encontrado",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
            else {
                Notification.show("Buscar credito",
                        "Ingrese un numero de credito",
                        Notification.Type.ERROR_MESSAGE);
                txtLoanNumberSearch.focus();

            }

        });

        btnImport.addClickListener(clickEvent -> {
            LoanDataService loanDataService = new LoanDataService();
//            if (loanDataService.findLoanDataByLoanNumber(Integer.parseInt(txttxtLoanNumberSearchLoanNumberSearch.getValue()))==null ) {
                if (validateLoanData()) {
                    Util util = new Util();
                    LoanData loanData = new LoanData();
                    loanData.setDebtorName(txtDebtorName.getValue().toString().trim());
                    loanData.setIdentityCardDebtor(txtIdentifyCardDebtor.getValue());
                    loanData.setClientLoanId(Integer.parseInt(txtClientLoanId.getValue().toString().trim()));
                    loanData.setAddressDebtor(txtAddressDebtor.getValue().toString().trim());
                    loanData.setCivilStatusDebtor(txtCivilStatusDebtor.getValue().trim());
                    loanData.setGenderDebtor(txtGenderDebtor.getValue().trim());
                    loanData.setLoanNumber(Integer.parseInt(txtLoanNumber.getValue()));
                    loanData.setCurrency(txtCurrency.getValue().trim());
                    loanData.setLoanMount(Double.parseDouble(txtLoanMount.getValue().trim()));
                    loanData.setLoanTerm(Integer.parseInt(txtLoanTerm.getValue().trim()));
                    loanData.setInterestRate(Double.parseDouble(txtInterestRate.getValue().trim()));
                    loanData.setCreditLifeInsurance(Double.parseDouble(txtCreditLifeInsurance.getValue().trim()));
                    loanData.setFixedPaymentDay(Integer.parseInt(txtFixedPaymentDay.getValue().trim()));
                    loanData.setPaymentFrecuency(txtPaymentFrecuency.getValue().trim());
                    loanData.setBranchOfficeId(Integer.parseInt(txtAgency.getValue().trim()));
                    loanData.setOfficial(txtOfficial.getValue().trim());
                    loanData.setAgency(txtAgency.getValue().trim());

                    loanData.setTeacRate(Double.parseDouble(txtTeacRate.getValue().toString().trim()));
                    loanData.setTreRate(Double.parseDouble(txtTreRate.getValue().toString().trim()));
                    loanData.setFeePayment(Double.parseDouble(txtFeePayment.getValue().toString().trim()));
                    loanData.setLoanDestination(txtLoanDestination.getValue().toString().trim());
                    loanData.setLoanDate(util.stringToDate(dateLoanDate.getValue().toString(), "yyyy-MM-dd"));
                    loanData.setTotalPayment(Double.parseDouble(txtTotalPayment.getValue().toString().trim()));

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonCodebtor = null;

                    try {
                        jsonCodebtor = mapper.writeValueAsString(getCoDebtorsGuarantors("codebtor", codebtorNetbankList));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    String jsonGuarantor = null;
                    try {
                        jsonGuarantor = mapper.writeValueAsString(getCoDebtorsGuarantors("guarantor", guarantorNetbankList));
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

                    if (loanDataService.findLoanDataByLoanNumber(Integer.parseInt(txtLoanNumberSearch.getValue()))==null ) {
                        loanDataService.insertLoanData(loanData);
                        Notification.show("Importacion datos",
                                "Datos del credito importados!",
                                Notification.Type.HUMANIZED_MESSAGE);
                    }else {
                        loanData.setLoanDataId(loanDataId);
                        loanDataService.updateLoanData(loanData);
                        Notification.show("Importacion datos",
                                "Datos del credito actualizados!",
                                Notification.Type.HUMANIZED_MESSAGE);
                    }

                } else {
                    Notification.show("ERROR",
                            "Datos incompeltos, complete la informacion",
                            Notification.Type.WARNING_MESSAGE);
                }
//            } else {
//                Notification.show("Datos credito",
//                        "Credito ya importado",
//                        Notification.Type.ERROR_MESSAGE);
//                txtLoanNumberSearch.focus();
//            }

        });

    }

    private void dataFromNetBank() {
        WarrantyNetBankService warrantyNetBankService = new WarrantyNetBankService();
        warrantyNetbankList = warrantyNetBankService.findWarrantyNetbankByCreCod(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
        CodebtorGuarantorNetbankService codebtorGuarantorNetbankService = new CodebtorGuarantorNetbankService();
        codebtorNetbankList = codebtorGuarantorNetbankService.findCodeptorByNumberLoan(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));
        guarantorNetbankList = codebtorGuarantorNetbankService.findGuarantorByNumberLoan(Integer.parseInt(txtLoanNumberSearch.getValue().toString()));

        loadCodebtorGuarantorNetbank(codebtorNetbankList, (Grid<CodebtorGuarantorNetbank>) gridCoDebtor);
        loadCodebtorGuarantorNetbank(guarantorNetbankList, (Grid<CodebtorGuarantorNetbank>) gridGuarantor);

        loadNetBankData(clientLoanNetbank);
        loadWarrantyNetBank(warrantyNetbankList, (Grid<WarrantyNetbank>) gridWarranty);
    }


    private boolean validateLoanData(){
        if (dateLoanDate.isEmpty()) return false;
        if (txtTreRate.isEmpty()) return false;
        if (txtTeacRate.isEmpty()) return false;
        if (txtFeePayment.isEmpty()) return false;
        if (txtTotalPayment.isEmpty()) return false;
        if (txtTotalPayment.isEmpty()) return false;
        if (txtLoanDestination.isEmpty()) return false;
        return true;
    }

    private void loadLoanDataLocal(LoanData loanData){
        loanDataId = loanData.getLoanDataId();
        txtLoanNumber.setValue(txtLoanNumberSearch.getValue());
        txtClientLoanId.setValue(loanData.getClientLoanId().toString());
        txtIdentifyCardDebtor.setValue(loanData.getIdentityCardDebtor());
        txtDebtorName.setValue(loanData.getDebtorName());
        txtCivilStatusDebtor.setValue(loanData.getCivilStatusDebtor());
        txtGenderDebtor.setValue(loanData.getGenderDebtor());
        txtAddressDebtor.setValue(loanData.getAddressDebtor());
        txtCurrency.setValue(loanData.getCurrency());
        txtLoanMount.setValue(loanData.getLoanMount().toString());
        txtInterestRate.setValue(loanData.getInterestRate().toString());
        txtLoanTerm.setValue(loanData.getLoanTerm().toString());
        txtFixedPaymentDay.setValue(loanData.getFixedPaymentDay().toString());
        txtPaymentFrecuency.setValue(loanData.getPaymentFrecuency());
        txtCreditLifeInsurance.setValue(loanData.getCreditLifeInsurance().toString());
        txtOfficial.setValue(loanData.getOfficial());
        txtAgency.setValue(loanData.getAgency());

        txtTeacRate.setValue(loanData.getTeacRate().toString());
        txtTreRate.setValue(loanData.getTreRate().toString());
        txtFeePayment.setValue(loanData.getFeePayment().toString());
        txtLoanDestination.setValue(loanData.getLoanDestination());
        Date date =  loanData.getLoanDate();
        dateLoanDate.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        txtTotalPayment.setValue(loanData.getTotalPayment().toString());
        dateLoanDate.setValue(new Util().stringToLocalDate(loanData.getLoanDate().toString(),"dd-MM-yyyy"));

        gridCoDebtor.removeAllColumns();
        gridGuarantor.removeAllColumns();
        gridWarranty.removeAllColumns();
        loadDataCoDebtors(loanData.getCoDebtors(), (Grid<CoDebtorGuarantor>) gridCoDebtor);
        loadDataCoDebtors(loanData.getGuarantors(), (Grid<CoDebtorGuarantor>) gridGuarantor);
        loadWarranty(loanData.getWarranty(), (Grid<Warranty>) gridWarranty);
    }

    private void loadDataCoDebtors(String coDebtorGuarantor, Grid<CoDebtorGuarantor> gridCoDebtorGurantor) {
        if (!coDebtorGuarantor.equals("[]")){
            ObjectMapper mapper = new ObjectMapper();
            List<CoDebtorGuarantor> coDebtorLocalList = new ArrayList<>();

            try {
                coDebtorLocalList = Arrays.asList(mapper.readValue(coDebtorGuarantor,CoDebtorGuarantor[].class));

               fillGridCoDebtorGuarantor(coDebtorLocalList,gridCoDebtorGurantor);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadWarranty(String warranty, Grid<Warranty> gridWarranty){
        if (!warranty.equals("[]")){
            ObjectMapper mapper = new ObjectMapper();
            List<Warranty> warrantyList = new ArrayList<>();
            try {
                warrantyList = Arrays.asList(mapper.readValue(warranty,Warranty[].class));
                fillGridWarranty(warrantyList,gridWarranty);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillGridWarranty(List<Warranty> warrantyList, Grid<Warranty> gridWarranty){
        gridWarranty.removeAllColumns();
        gridWarranty.setItems(warrantyList);
        gridWarranty.addColumn(Warranty::getLoanNumber).setCaption("Nro prestamo");
        gridWarranty.addColumn(Warranty::getTypeGuarantee).setCaption("Tipo garantia");
        gridWarranty.addColumn(Warranty::getCurrency).setCaption("Moneda");
        gridWarranty.addColumn(Warranty::getAssessmentEntity).setCaption("Grav. Fav. Entidad");
        gridWarranty.addColumn(Warranty::getExpirationDate,new TextRenderer("-")).setCaption("Fecha venc");
        gridWarranty.addColumn(Warranty::getNumberRealRight,new TextRenderer("-")).setCaption("Nro. Part. DDRR");
        gridWarranty.addColumn(Warranty::getDateRealRight,new TextRenderer("-")).setCaption("Fecha partida DDRR");
        gridWarranty.addColumn(Warranty::getMortageNumber, new TextRenderer("-")).setCaption("Nro hip.");
        gridWarranty.addColumn(Warranty::getDateMortage, new TextRenderer("-")).setCaption("Fecha hip.");
        gridWarranty.addColumn(Warranty::getDescription, new TextRenderer("-")).setCaption("Descripcion");
        gridWarranty.addColumn(Warranty::getEnoughGuarante, new TextRenderer("-")).setCaption("Suf. liquida");


    }

    private void fillGridCoDebtorGuarantor(List<CoDebtorGuarantor> coDebtorGuarantorList, Grid<CoDebtorGuarantor> gridCoDebtorGuarantor){

        gridCoDebtorGuarantor.removeAllColumns();
        gridCoDebtorGuarantor.setItems(coDebtorGuarantorList);
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getNumberLoan).setCaption("Nro Prestamo");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getCodeMebership).setCaption("Nro agenda");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getName).setCaption("Nombre");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getIdentifyCard).setCaption("Carnet");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getAddressHome).setCaption("Dir. domicilio");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getAddressOffice).setCaption("Dir. oficina");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getCivilStatus).setCaption("Estado civil");
        gridCoDebtorGuarantor.addColumn(CoDebtorGuarantor::getGender).setCaption("Genero");

    }



    private List<Warranty> getWarranty(List<WarrantyNetbank> warrantyNetbanks){
        List<Warranty> warrantyList = new ArrayList<>();
        Util util = new Util();

        for(WarrantyNetbank warrantyNetbank:warrantyNetbanks){
            Warranty warranty = new Warranty();
            warranty.setId(warrantyNetbank.getPrgarcorr());
            warranty.setLoanNumber(warrantyNetbank.getPrgarnpre());
            warranty.setTypeGuarantee(warrantyNetbank.getGbtgadesc().trim());
            warranty.setCurrency(warrantyNetbank.getPrgarcmon().trim());
            warranty.setAssessmentEntity(warrantyNetbank.getPrgargfin());
            if (warrantyNetbank.getPrgarfvto()!=null)
                warranty.setExpirationDate(util.stringToDate(warrantyNetbank.getPrgarfvto(),"yyyy-MM-dd"));
            warranty.setNumberRealRight(warrantyNetbank.getPrgarnpar());
            if (warrantyNetbank.getPrgarfpar()!= null)
                warranty.setDateRealRight(util.stringToDate(warrantyNetbank.getPrgarfpar(),"yyyy-MM-dd"));
            warranty.setMortageNumber(warrantyNetbank.getPrgarnhip());
            if(warrantyNetbank.getPrgarfhip() != null)
                warranty.setDateMortage(util.stringToDate(warrantyNetbank.getPrgarfhip(),"yyyy-MM-dd"));
            if (warrantyNetbank.getPrgardesc()!= null)
                warranty.setDescription(warrantyNetbank.getPrgardesc().trim());
            if (warrantyNetbank.getPrgarsufl()!= null)
                warranty.setEnoughGuarante(warrantyNetbank.getPrgarsufl().trim());
            warrantyList.add(warranty);

        }

        return warrantyList;
    }

    private List<CoDebtorGuarantor> getCoDebtorsGuarantors(String type, List<CodebtorGuarantorNetbank> codebtorGuarantorNetbanks){
        List<CoDebtorGuarantor> coDebtorList = new ArrayList<>();
        int i = 1;
        for(CodebtorGuarantorNetbank codebtorGuarantorNetbank: codebtorGuarantorNetbanks){
            CoDebtorGuarantor coDebtorGuarantor = new CoDebtorGuarantor();
            coDebtorGuarantor.setCodeMebership(codebtorGuarantorNetbank.getPrdeucage());
            coDebtorGuarantor.setNumberLoan(codebtorGuarantorNetbank.getPrdeunpre());
            coDebtorGuarantor.setType(type.trim());
            coDebtorGuarantor.setName(codebtorGuarantorNetbank.getGbagenomb().trim());
            coDebtorGuarantor.setIdentifyCard(codebtorGuarantorNetbank.getGbagendid().trim());
            coDebtorGuarantor.setAddressOffice(codebtorGuarantorNetbank.getGbageddo().trim());
            coDebtorGuarantor.setAddressHome(codebtorGuarantorNetbank.getGbagedir().trim());
            coDebtorGuarantor.setCivilStatus(codebtorGuarantorNetbank.getGbageeciv().trim());
            coDebtorGuarantor.setGender(codebtorGuarantorNetbank.getGbagesexo().trim());
            coDebtorGuarantor.setInsured(type.equals("codebtor")?"asegurado":"noAsegurado");
            coDebtorGuarantor.setId(i);


            i++;

            coDebtorList.add(coDebtorGuarantor);

        }

        return coDebtorList;
    }

    private Grid loadWarrantyNetBank(List<WarrantyNetbank> warrantyNetbankList, Grid<WarrantyNetbank> gridWarranty){
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
        return gridWarranty;
    }



    private void loadCodebtorGuarantorNetbank(List<CodebtorGuarantorNetbank> coDebtorList, Grid<CodebtorGuarantorNetbank> gridCoDebtor){
//        if (type.equals("codebtor")) {
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
//        } else {
//            gridGuarantor.removeAllColumns();
//            gridGuarantor.setItems(coDebtorList);
//
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getPrdeunpre).setCaption("Nro prestamo");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getPrdeucage).setCaption("Nro agenda");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagenomb).setCaption("Nombre");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagendid).setCaption("Carnet");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagedir).setCaption("Dir. domicilio");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbageddo).setCaption("Dir. oficina");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbageeciv).setCaption("Estado civil");
//            gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagesexo).setCaption("Genero");
//        }

    }

//    private void loadGuarantorNetbank(List<CodebtorGuarantorNetbank> guarantorList){
//        gridGuarantor.removeAllColumns();
//        gridGuarantor.setItems(guarantorList);
//
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getPrdeunpre).setCaption("Nro prestamo");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getPrdeucage).setCaption("Nro agenda");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagenomb).setCaption("Nombre");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagendid).setCaption("Carnet");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagedir).setCaption("Dir. domicilio");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbageddo).setCaption("Dir. oficina");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbageeciv).setCaption("Estado civil");
//        gridGuarantor.addColumn(CodebtorGuarantorNetbank::getGbagesexo).setCaption("Genero");
//    }

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
        dateLoanDate.setValue(new Util().stringToLocalDate(clientLoanNetbank.getPrmprfreg().toString(),"dd-MM-yyyy"));
        txtCreditLifeInsurance.setValue("0");
        txtOfficial.setValue("");
        txtAgency.setValue(clientLoanNetbank.getPrmpragen().toString());
        fieldClientStatus(true);

    }

    private void clearFieldData(){
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

        txtTeacRate.clear();
        txtTreRate.clear();
        txtFeePayment.clear();
        txtLoanDestination.clear();
        dateLoanDate.clear();
        txtTotalPayment.clear();

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

        dateLoanDate = new DateField("Fecha registro:");
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

//        btnSaveSupplementaryData = new Button("Guardar");
//        btnSaveSupplementaryData.setStyleName(ValoTheme.BUTTON_PRIMARY);
//        btnSaveSupplementaryData.setIcon(VaadinIcons.SAFE);
//        gridLayoutSupplementaryData.addComponent(btnSaveSupplementaryData,0,2);

        panelSupplentaryData.setContent(gridLayoutSupplementaryData);

        return panelSupplentaryData;
    }

    private Panel buildPanelWarranty(){
        panelWarranty = new Panel();
        panelWarranty.setStyleName(ValoTheme.PANEL_WELL);
        panelWarranty.setWidth("100%");
        panelWarranty.setHeight("180px");

        gridWarranty = new Grid<>();
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
