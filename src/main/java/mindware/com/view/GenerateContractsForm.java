package mindware.com.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.MessageBox;
import mindware.com.model.*;
import mindware.com.service.*;
import mindware.com.utilities.NumberToLiteral;

import mindware.com.utilities.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateContractsForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private HorizontalLayout horizontalLayout;
    private Panel panelGridLoanData;
    private Panel panelGridTemplateContract;
    private Panel panelSearch;
    private Panel panelGenerateContract;
    private Grid<LoanData> gridLoanData;
    private Grid<Parameter> gridContractTemplate;

    private TextField txtTextSearch;
    private TextField txtLoanDataId;
    private TextField txtFileNameContract;
    private TextField txtDescription;
    private TextField txtContractId;
    private ComboBox cmbSearchBy;
    private DateField dateContractGenerate;
    private Button btnGenerateContract;
    private Button btnDownloadContract;
    private Button btnSearch;
    private Button btnInsured;
    private Integer numberLoan;
    private String pathGenerate="";
    private URI uri;
    private String origin = "";
    private LoanData loanData;

    private final static char CR  = (char) 0x0D;
    private final static char LF  = (char) 0x0A;
    private final static String CRLF  = "" + CR + LF;
    public GenerateContractsForm(){

        setCompositionRoot(buildMainGridLayout());
        fillGridTemplateContract();
        postBuild();
        uri=  Page.getCurrent().getLocation();
    }

    private void postBuild(){
        btnSearch.addClickListener(clickEvent -> {

            if (validateSearchCriteria()){
                fillGridLoanData(cmbSearchBy.getValue().toString(), txtTextSearch.getValue() );
            }else{
                Notification.show("Buscar",
                        "Seleccione e ingrese criterios de busqueda",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        btnGenerateContract.addClickListener(clickEvent -> {
            pathGenerate = "";
            if (validateData()) {
                if (validateSignatoryEntity()) {
                    Path paths = Paths.get(System.getProperties().get("user.home").toString());
                    String path = paths.toString() + "/template/" + txtFileNameContract.getValue();
                    pathGenerate = paths.toString() + "/generated/" + numberLoan.toString() + ".docx";
                    if (verifyExistFileContract(path)) {
                        if (verifyExistFileContract(pathGenerate)) {

                            MessageBox
                                    .createQuestion()
                                    .withCaption("Contrato")
                                    .withMessage("Prestamo ya tiene contrato, desea reemplazarlo?")
                                    .withYesButton(() -> {
                                        createContract(path, pathGenerate);
                                        Notification.show("Contrato",
                                                "Contrato creado",
                                                Notification.Type.HUMANIZED_MESSAGE);
                                        insertUpdateContract(pathGenerate, "update");
                                    })
                                    .withNoButton(() -> {
                                    })
                                    .open();
                        } else {
                            if (txtContractId.isEmpty()) {
                                createContract(path, pathGenerate);
                                Notification.show("Contrato",
                                        "Contrato creado",
                                        Notification.Type.HUMANIZED_MESSAGE);
                                insertUpdateContract(pathGenerate, "insert");
                            } else {
                                createContract(path, pathGenerate);
                                Notification.show("Contrato",
                                        "Contrato regenerado",
                                        Notification.Type.HUMANIZED_MESSAGE);
                                insertUpdateContract(pathGenerate, "update");
                            }
                        }
                    } else {
                        Notification.show("Contrato",
                                "No existe la plantilla de contrato selecionada",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }else {
                    Notification.show("Contrato",
                            "No tiene registrado responsables legales en la agencia o sucursal",
                            Notification.Type.ERROR_MESSAGE);
                }
            }else {
                Notification.show("Contrato",
                        "Ingrese la fecha de elaboracion del contrato y/o seleccione el credito y modelo de contrato",
                        Notification.Type.ERROR_MESSAGE);
                dateContractGenerate.focus();
            }
        });

        gridLoanData.addItemClickListener(itemClick -> {
            txtLoanDataId.setValue(itemClick.getItem().getLoanDataId().toString());
            numberLoan = itemClick.getItem().getLoanNumber();
            loanData = itemClick.getItem();
            List<Contract> contractList = new ContractService().findCotractByLoanNumber(numberLoan);
            if (contractList.size()>0)
                txtContractId.setValue(contractList.get(0).getContractId().toString());
            else
                txtContractId.setValue("");

        });

        gridContractTemplate.addItemClickListener(itemClick -> {
           txtFileNameContract.setValue(itemClick.getItem().getValueParameter());
        });

        btnDownloadContract.addClickListener(clickEvent -> {
            if (!pathGenerate.equals("") ) {
                downloadContract(pathGenerate, numberLoan.toString()+".docx");
            } else {
                Notification.show("ERROR",
                        "No se genero un contrato, genere uno y pruebe nuevamente",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        btnInsured.addClickListener(clickEvent -> {
            ListInsuredWindowCodebtor listInsuredWindowCodebtor = new ListInsuredWindowCodebtor(loanData);
            listInsuredWindowCodebtor.setModal(true);
            listInsuredWindowCodebtor.setWidth("700.0px");
            listInsuredWindowCodebtor.setHeight("450.0px");
            listInsuredWindowCodebtor.center();
            UI.getCurrent().addWindow(listInsuredWindowCodebtor);
            listInsuredWindowCodebtor.addCloseListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
//                    if (!agenciaWindowForm.descripcionAgencia.isEmpty()) {
//                        txtAgenciaId.setReadOnly(false);
//                        txtAgenciaId.setValue(agenciaWindowForm.agencia.getAgenciaId().toString());
//                        txtAgenciaId.setDescription(agenciaWindowForm.descripcionAgencia);
//                        txtAgenciaId.setReadOnly(true);
//                        ciudad = "";
//                        ciudad = agenciaWindowForm.agencia.getCiudad();
//                    }
                }
            });
        });

    }



    private void downloadContract(String file, String fileName){

        FileResource res = new FileResource(new File(file));
        res.setCacheTime(0);
        res.getStream().setCacheTime(0);
        FileDownloader fd = new FileDownloader(res);
        fd.extend(btnDownloadContract);
        
//
//        try {
//            byte[] data = Files.readAllBytes(Paths.get(file));
//
//            downloadExportFile(data,fileName);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    public void downloadExportFile(byte[] toDownload, String fileName) {
        StreamResource.StreamSource source = new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return new ByteArrayInputStream(toDownload);
            }
        };
        // by default getStream always returns new DownloadStream. Which is weird because it makes setting stream parameters impossible.
        // It seems to be working before in earlier versions of Vaadin. We'll override it.
        StreamResource resource = new StreamResource(source, fileName) {
            DownloadStream downloadStream;
            @Override
            public DownloadStream getStream() {
                if (downloadStream==null)
                    downloadStream = super.getStream();
                return downloadStream;
            }
        };
        resource.getStream().setParameter("Content-Disposition","attachment"); // or else browser will try to open resource instead of download it
        resource.getStream().setParameter("Content-Type","application/octet-stream");
        resource.getStream().setCacheTime(0);
        ResourceReference ref = new ResourceReference(resource,this, "download");


        FileDownloader fd = new FileDownloader(resource);
        fd.extend(btnDownloadContract);

//        this.setResource("download", resource); // now it's available for download
//
//
//        Page.getCurrent().open(ref.getURL(), null);


    }


    private void createContract(String path, String pathGenerate) {
        OutputStream contract = null;
        try {

           contract = new FileOutputStream(pathGenerate);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            replace(path, getFieldValuesLoanData(numberLoan), contract);

        } catch (Exception e) {
            Notification.show("ERROR",
                    "Verifique lo siguiente:\n" +
                            "1. Que existan los representantes legales de la agencia\n" +
                            " 2. Si el contrato es con garantes que el credito, tenga registrado garantes\n" +
                            " 3. Tener registradas correctamente las direcciones de los Codeudores y Garantes\n" +
                            " 4. "+ e +"\n" ,
                    Notification.Type.ERROR_MESSAGE);

        }
    }

    private void insertUpdateContract(String nameContract, String type){
        Contract contract = new Contract();
        Util util = new Util();
        contract.setDateContract(util.stringToDate(dateContractGenerate.getValue().toString(),"yyyy-MM-dd"));
        contract.setFileNameContract(nameContract);
        contract.setLoanDataId(Integer.parseInt(txtLoanDataId.getValue()));
        contract.setDescription(txtDescription.getValue());

        ContractService contractService = new ContractService();
        if (type.equals("insert")) {
            contractService.insertContract(contract);
            txtContractId.setValue(contract.getContractId().toString());
        }
        else {
            if (txtContractId.getValue().isEmpty()){
                contractService.insertContract(contract);
                txtContractId.setValue(contract.getContractId().toString());
            }else {
                contract.setContractId(Integer.parseInt(txtContractId.getValue()));
                contractService.updateContract(contract);
            }
        }
    }

    private boolean verifyExistFileContract(String fileContract){

        return new File(fileContract).exists();

    }

    private void replace(String inFile, Map<String, String> data, OutputStream out) throws Exception {
        XWPFDocument doc = new XWPFDocument(OPCPackage.open(inFile));

        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc);
        //read header
        XWPFHeader header = policy.getDefaultHeader();
        for (XWPFParagraph w : header.getListParagraph()){
            replace2(w, data, "body",doc);
        }
//        replace2(header.getParagraphArray(0), data, "body",doc);
//        replace2(header.getParagraphArray(1), data, "body",doc);

        for (XWPFParagraph p : doc.getParagraphs()) {
            replace2(p, data, "body",doc);
        }

        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replace2(p, data,"signature_guarantors",doc);
                    }
                }
            }
        }
        doc.write(out);
    }

    private void replace2(XWPFParagraph p, Map<String, String> data, String partDocument, XWPFDocument doc) throws Exception{
        String pText = p.getText(); // complete paragraph as string
        if (pText.contains("${")) { // if paragraph does not include our pattern, ignore
            TreeMap<Integer, XWPFRun> posRuns = getPosToRuns(p);
            Pattern pat = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher m = pat.matcher(pText);
            while (m.find()) { // for all patterns in the paragraph
                String g = m.group(1);  // extract key start and end pos
                int s = m.start(1);
                int e = m.end(1);
                String key = g;
                String x = "";
                try {
                     if (key.equals("dateContract")){
                         x = data.get(key).trim();
                         SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", new Locale("bo", "BO"));
                         Date date = dt.parse(x);
                         x = new SimpleDateFormat("dd 'de' MMMM yyyy").format(date);
                     }else {
                         x = data.get(key).trim();
                     }
                } catch (Exception f){
                    x = "";
                }

                if (x == null)
                    x = "";

                SortedMap<Integer, XWPFRun> range = posRuns.subMap(s - 2, true, e + 1, true); // get runs which contain the pattern
                boolean found1 = false; // found $
                boolean found2 = false; // found {
                boolean found3 = false; // found }
                XWPFRun prevRun = null; // previous run handled in the loop
                XWPFRun found2Run = null; // run in which { was found
                int found2Pos = -1; // pos of { within above run
                for (XWPFRun r : range.values())
                {
                    if (r == prevRun)
                        continue; // this run has already been handled
                    if (found3)
                        break; // done working on current key pattern
                    prevRun = r;
                    for (int k = 0;; k++) { // iterate over texts of run r
                        if (found3)
                            break;
                        String txt = null;
                        try {
                            txt = r.getText(k); // note: should return null, but throws exception if the text does not exist
                        } catch (Exception ex) {

                        }
                        if (txt == null)
                            break; // no more texts in the run, exit loop
                        if (txt.contains("$") && !found1) {  // found $, replace it with value from data map
                            txt = txt.replaceFirst("\\$", String.valueOf(x));
                            found1 = true;
                        }
                        if (txt.contains("{") && !found2 && found1) {
                            found2Run = r; // found { replace it with empty string and remember location
                            found2Pos = txt.indexOf('{');
                            txt = txt.replaceFirst("\\{", "");
                            found2 = true;
                        }
                        if (found1 && found2 && !found3) { // find } and set all chars between { and } to blank
                            if (txt.contains("}"))
                            {
                                if (r == found2Run)
                                { // complete pattern was within a single run
                                    txt = txt.substring(0, found2Pos)+txt.substring(txt.indexOf('}'));
                                }
                                else // pattern spread across multiple runs
                                    txt = txt.substring(txt.indexOf('}'));
                            }
                            else if (r == found2Run) // same run as { but no }, remove all text starting at {
                            {
                                if (txt.length()> found2Pos)
                                    txt = txt.substring(0, found2Pos);

                            }
                            else
                                txt = ""; // run between { and }, set text to blank
                        }
                        if (txt.contains("}") && !found3) {
                            txt = txt.replaceFirst("\\}", "");
                            found3 = true;
                        }
                        if (partDocument.equals("body")) {
                            if (txt.contains("\n")) {
                                    String[] strings = txt.split("\n");
                                    int i = 0;
                                    switch (key){
//                                        case "#warranty_dpf_redaccion":
//                                            fillAnd(p, strings, i, r);
//                                            break;
                                        case "#codeudor_nombres" :
                                            fillAnd(p, strings, i, r);
                                            break;
                                        case "#garante_nombres" :
                                            fillAnd(p, strings, i, r);
                                            break;
                                            default:
                                                for (String string : strings) {
                                                r.setText(string, i);
                                                if (i != strings.length - 1) {
                                                    r.addCarriageReturn();

                                                }
                                                i++;
//
                                                p.insertNewRun(i);

                                            }
                                    }


                            } else {
                                r.setText(txt, k);
                            }
                        }else {
                            r.setText(txt, k);
                        }

                    }
                }
            }
            System.out.println(p.getText());
        }
    }

    private void fillAnd(XWPFParagraph p, String[] strings, int i, XWPFRun r) {
        for (String string : strings) {

            if (strings.length == 1) {
                r.setText(string.trim(), i);
            }
            if (strings.length == 2) {
                if (i != strings.length - 1) {
                    r.setText(string + " y ", i);
                } else {
                    r.setText(string, i);
                }
            } else {
                if (i != strings.length - 1) {
                    r.setText(string + ", ", i);
                } else if (i == strings.length - 1) {
                    r.setText(string.trim() + " y ", i);
                } else {
                    r.setText(string.trim(), i);
                }
            }
            i++;
            p.insertNewRun(i);
        }
    }

    private TreeMap<Integer, XWPFRun> getPosToRuns(XWPFParagraph paragraph) {
        int pos = 0;
        TreeMap<Integer, XWPFRun> map = new TreeMap<Integer, XWPFRun>();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.text();
            if (runText != null && runText.length() > 0) {
                for (int i = 0; i < runText.length(); i++) {
                    map.put(pos + i, run);
                }
                pos += runText.length();
            }

        }
        return map;
    }

    private Map<String, String> getFieldValuesLoanData(Integer loanNumber){
        Map<String,String> stringMapVariables = new HashMap<>();
        ParameterService parameterService = new ParameterService();
        List<Parameter> listVariableContract = parameterService.findParameterByType("variable_contract");
        LoanDataService loanDataService = new LoanDataService();
        LoanData loanData = loanDataService.findLoanDataByLoanNumber(loanNumber);

        for(Parameter parameter:listVariableContract){
            if (parameter.getValueParameter().equals("loanNumber"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanNumber().toString());
            if (parameter.getValueParameter().equals("loanDate"))
                stringMapVariables.put(parameter.getValueParameter(),dateContractGenerate.getValue().toString());
            if(parameter.getValueParameter().equals("currency"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCurrency());
            if(parameter.getValueParameter().equals("loanMount"))
                stringMapVariables.put(parameter.getValueParameter(),String.format("%,.2f",  loanData.getLoanMount()));
            if(parameter.getValueParameter().equals("loanTerm")) {
                Integer plazo = loanData.getLoanTerm();
                stringMapVariables.put(parameter.getValueParameter(), plazo.toString());
            }
            if(parameter.getValueParameter().equals("interestRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getInterestRate().toString());
            if(parameter.getValueParameter().equals("treRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getTreRate().toString());
            if(parameter.getValueParameter().equals("teacRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getTeacRate().toString());
            if(parameter.getValueParameter().equals("feePayment"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getFeePayment().toString());
            if(parameter.getValueParameter().equals("paymentFrecuency"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getPaymentFrecuency());
            if(parameter.getValueParameter().equals("creditLifeInsurance"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCreditLifeInsurance().toString());
            if(parameter.getValueParameter().equals("totalPayment"))
                stringMapVariables.put(parameter.getValueParameter(), String.format("%,.2f",loanData.getTotalPayment()));
            if(parameter.getValueParameter().equals("loanDestination"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanDestination());
            if(parameter.getValueParameter().equals("agency"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getAgency());
            if(parameter.getValueParameter().equals("official"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getOfficial());
            if(parameter.getValueParameter().equals("debtorName"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getDebtorName());
            if(parameter.getValueParameter().equals("identityCardDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getIdentityCardDebtor());
            if(parameter.getValueParameter().equals("addressDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getAddressDebtor());
            if(parameter.getValueParameter().equals("civilStatusDebtor"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCivilStatusDebtor());
            if(parameter.getValueParameter().equals("creditLifeInsurance"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getCreditLifeInsurance().toString());
            if(parameter.getValueParameter().equals("teacRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getTeacRate().toString());
            if (parameter.getValueParameter().equals("interestRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getInterestRate().toString());
            if(parameter.getValueParameter().equals("genderDebtor")) {
                if (loanData.getGenderDebtor().equals("FEMENINO"))
                    stringMapVariables.put(parameter.getValueParameter(), "LA DEUDORA");
                else
                    stringMapVariables.put(parameter.getValueParameter(), "EL DEUDOR");
            }
            if(parameter.getValueParameter().equals("fixedPaymentDay"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getFixedPaymentDay().toString());
            if(parameter.getValueParameter().equals("dateContract"))
                stringMapVariables.put(parameter.getValueParameter(),dateContractGenerate.getValue().toString());
            if(parameter.getValueParameter().equals("ciudadSucursal"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getBranchOffice().getCityName());
            if(parameter.getValueParameter().equals("direccionSucursal"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getBranchOffice().getAddress());

            if (parameter.getValueParameter().equals("paymentFrecuency")){
                String frecuencia ="";
                if (loanData.getPaymentFrecuency().equals("30"))
                    frecuencia= "MENSUALES";
                else
                    if (loanData.getPaymentFrecuency().equals("60"))
                    frecuencia = "BIMESTRALES";
                else
                    if (loanData.getPaymentFrecuency().equals("90"))
                    frecuencia = "TRIMESTRALES";
                else
                    if (loanData.getPaymentFrecuency().equals("120"))
                        frecuencia = "CUATRIMESTRALES";
                else
                    if (loanData.getPaymentFrecuency().equals("150"))
                        frecuencia = "QUINTIMESTRALES";
                else
                    if (loanData.getPaymentFrecuency().equals("180"))
                        frecuencia = "SEMESTRALES";
                else
                    if (loanData.getPaymentFrecuency().equals("360"))
                        frecuencia = "ANUALES";

                stringMapVariables.put(parameter.getValueParameter(),frecuencia);
            }
            if (parameter.getValueParameter().equals("paymentFrecuency2")){
                String frecuencia ="";
                if (loanData.getPaymentFrecuency().equals("30"))
                    frecuencia= "MENSUAL";
                else
                if (loanData.getPaymentFrecuency().equals("60"))
                    frecuencia = "BIMESTRAL";
                else
                if (loanData.getPaymentFrecuency().equals("90"))
                    frecuencia = "TRIMESTRAL";
                else
                if (loanData.getPaymentFrecuency().equals("120"))
                    frecuencia = "CUATRIMESTRAL";
                else
                if (loanData.getPaymentFrecuency().equals("150"))
                    frecuencia = "QUINTIMESTRAL";
                else
                if (loanData.getPaymentFrecuency().equals("180"))
                    frecuencia = "SEMESTRAL";
                else
                if (loanData.getPaymentFrecuency().equals("360"))
                    frecuencia = "ANUAL";

                stringMapVariables.put(parameter.getValueParameter(),frecuencia);
            }
            if (parameter.getValueParameter().equals("savingBox"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getSavingBox());
            if (parameter.getValueParameter().equals("spread"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getSpread().toString());
            if(parameter.getValueParameter().equals("loanLine"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLoanLine().toString());
            if(parameter.getValueParameter().equals("lineRate"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLineRate().toString());
            if(parameter.getValueParameter().equals("lineSpread"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLineSpread().toString());
            if(parameter.getValueParameter().equals("lineTerm"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLineTerm().toString());
            if(parameter.getValueParameter().equals("lineMount"))
                stringMapVariables.put(parameter.getValueParameter(),loanData.getLineMount().toString());

        }

        listVariableContract.removeAll(listVariableContract);
        listVariableContract = parameterService.findParameterByType("custom_variable_contract");

        for(Parameter parameter:listVariableContract){


            if (parameter.getValueParameter().equals("literal_monto_prestamo")){
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(String.format("%.2f",loanData.getLoanMount()),true,"N/A","float"));
            }else
            if (parameter.getValueParameter().equals("literal_total_pagar")){
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(String.format("%.2f",loanData.getTotalPayment()),true,"N/A","float"));
            }else
            if (parameter.getValueParameter().equals("literal_plazo")){
                Integer plazo = loanData.getLoanTerm();
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(plazo.toString(),true,"","integer"));

            } else
            if (parameter.getValueParameter().equals("literal_interes")){
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(loanData.getInterestRate().toString(),true,"","float"));
            }else
            if (parameter.getValueParameter().equals("literal_spread")){
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(loanData.getSpread().toString(),true,"","float"));
            }else
            if (parameter.getValueParameter().equals("literal_monto_linea")){
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(String.format("%.2f", loanData.getLineMount()),true,"N/A","float"));
            }else
            if (parameter.getValueParameter().equals("literal_plazo_linea")){
                Integer plazo = loanData.getLineTerm()/30;
                stringMapVariables.put(parameter.getValueParameter(),new NumberToLiteral()
                        .Convert(plazo.toString(),true,"","integer"));
            }else
            if (parameter.getValueParameter().equals("literal_tasa_base_tre")) {
                stringMapVariables.put(parameter.getValueParameter(), new NumberToLiteral()
                        .Convert(loanData.getTreRate().toString(), true, "", "float"));
            }
            else
                stringMapVariables.put(parameter.getValueParameter(),parameter.getDescriptionParameter());
        }
        if (!loanData.getGuarantors().equals("[]")) {
            stringMapVariables = replaceCicleVariables(stringMapVariables, loanData.getGuarantors(), "#garante%");
            stringMapVariables = replaceSignantGuarantorCodebtor(stringMapVariables,loanData.getGuarantors(),"garante");
        }

        if (!loanData.getWarranty().equals("[]")){
            stringMapVariables = replaceCicleVariablesWarranty(stringMapVariables,loanData.getWarranty(),"#warranty%");
        }
        stringMapVariables= replaceCicleVariables(stringMapVariables,loanData.getCoDebtors(),"#codeudor%");
        stringMapVariables= replaceCicleInsured(stringMapVariables,loanData.getCoDebtors());

        stringMapVariables = replaceSignantGuarantorCodebtor(stringMapVariables,loanData.getCoDebtors(),"codeudor");
        stringMapVariables = replaceSignatureEntity(stringMapVariables,loanData.getBranchOffice().getSignatories(),"responsable");

        return stringMapVariables;
    }


    private Map<String,String> replaceSignatureEntity(Map<String,String> data, String json, String type){
        ObjectMapper mapper = new ObjectMapper();
        List<Signatories> signatoriesList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        ParameterService parameterService = new ParameterService();
        List<Parameter> parameterList = new ArrayList<>();
        try {
            signatoriesList = Arrays.asList(mapper.readValue(json,Signatories[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i=1;
        signatoriesList.sort(Comparator.comparing(Signatories::getPriority));
        for(Signatories signatories:signatoriesList){
            if (signatories.getStatus().equals("ACTIVO")) {
                map.put("${nameSignatorie}", signatories.getNameSignatorie());
                map.put("${position}", signatories.getPosition());
                map.put("${identifyCardSignatorie}", signatories.getIdentifyCardSignatorie());
//                map.put("${nroPoder}", signatories.getNroPoder());
//                map.put("${fechaPoder}", signatories.getFechaPoder());
                map.put("${nroNotaria}", signatories.getNroNotaria());
                map.put("${nombreNotario}", signatories.getNombreNotario());
                map.put("${distritoJudicial}", signatories.getDistritoJudicial());
                map.put("${nroTestimonio}", signatories.getNroTestimonio());
                map.put("${fechaTestimonio}", signatories.getFechaTestimonio());

                parameterList = parameterService.findParameterByTypeAndValue("custom_variable_contract", type + i + "%");
                for (Parameter parameter : parameterList) {
                    origin = parameter.getDescriptionParameter();
                    map.forEach((k, v) -> {
                        origin = origin.replaceAll(Pattern.quote(k), v);
                    });
                    if (data.containsKey(parameter.getValueParameter()))
                        data.put(parameter.getValueParameter(), origin);
                    else
                        data.put(parameter.getValueParameter(), "");
                }
                i++;
            }
        }
        return data;
    }

    private Map<String,String> replaceSignantGuarantorCodebtor(Map<String,String> data, String json, String type) {
        ObjectMapper mapper = new ObjectMapper();
        List<CoDebtorGuarantor> coDebtorGuarantorList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        ParameterService parameterService = new ParameterService();

        try {
            coDebtorGuarantorList = Arrays.asList(mapper.readValue(json,CoDebtorGuarantor[].class));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (coDebtorGuarantorList.size()>0){
            List<Parameter> parameterList = new ArrayList<>();
            int i=1;
            coDebtorGuarantorList.sort(Comparator.comparing(CoDebtorGuarantor::getPrioridad));
            for(CoDebtorGuarantor coDebtorGuarantor : coDebtorGuarantorList) {
                map.put("${name}",coDebtorGuarantor.getName());
                map.put("${identifyCard}",coDebtorGuarantor.getIdentifyCard());
                map.put("${adyacentes}",coDebtorGuarantor.getAdyacentes());
                map.put("${zona}",coDebtorGuarantor.getZona());
                map.put("${ciudad}",coDebtorGuarantor.getCiudad());
                map.put("${provincia}",coDebtorGuarantor.getProvincia());
                map.put("${departamento}",coDebtorGuarantor.getDepartamento());
                map.put("${numeroCasa}", coDebtorGuarantor.getNumeroCasa());

                parameterList = parameterService.findParameterByTypeAndValue("custom_variable_contract",type+i+"%");

                for (Parameter parameter : parameterList) {
                    origin = parameter.getDescriptionParameter();

                    map.forEach((k, v) -> {
                        origin = origin.replaceAll(Pattern.quote(k), v);

                    });
                    if (data.containsKey(parameter.getValueParameter()))
                        data.put(parameter.getValueParameter(),origin);
                    else
                        data.put(parameter.getValueParameter(),"");
               }
               i++;
            }
            for(int j=i;j<5;j++){
                parameterList = parameterService.findParameterByTypeAndValue("custom_variable_contract",type+j+"%");
                for(Parameter parameter : parameterList) {
                    data.put(parameter.getValueParameter()," ");
                }

            }

        }

        return data;
    }

    private Map<String,String> replaceCicleInsured(Map<String,String> data, String json){
        ObjectMapper mapper = new ObjectMapper();
        List<CoDebtorGuarantor> coDebtorGuarantorList = new ArrayList<>();
        String insuredCodebtors="";
        ParameterService parameterService = new ParameterService();
        try {
            List<Parameter> parameterList = new ArrayList<>();
            coDebtorGuarantorList = Arrays.asList(mapper.readValue(json,CoDebtorGuarantor[].class));
//            parameterList = parameterService.findParameterByTypeAndValue("custom_variable_contract","#lista_asegurados");
            for(CoDebtorGuarantor coDebtorGuarantor:coDebtorGuarantorList){
                if (coDebtorGuarantor.getInsured().equals("asegurado")) {
                    if (insuredCodebtors.equals(""))
                        insuredCodebtors = insuredCodebtors + coDebtorGuarantor.getName();
                    else insuredCodebtors = insuredCodebtors+", "+ coDebtorGuarantor.getName();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        data.put("#lista_asegurados",insuredCodebtors);
        return data;

    }

    private Map<String, String> replaceCicleVariablesWarranty(Map<String,String> data, String json, String type){
        ObjectMapper mapper = new ObjectMapper();
        List<Warranty> warrantyList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();

        try{
            ParameterService parameterService = new ParameterService();
            warrantyList = Arrays.asList(mapper.readValue(json,Warranty[].class));
            List<Parameter> parameterList = new ArrayList<>();
            parameterList = parameterService.findParameterByTypeAndValue("custom_variable_contract",type);

            if(warrantyList.size()>0){
                for(Warranty warranty : warrantyList){
                    if (warranty.getCodeGuarantee().equals("BM2") || warranty.getCodeGuarantee().equals("BE2")){
                       map.put("${numeroDpf}",warranty.getMortageNumber());
                       map.put("${numeroPizarra}",warranty.getNumeroPizarra());
                       map.put("${numeroCui}",warranty.getNumeroCUI());
                       map.put("${entidadEmisora}",warranty.getEntidadEmisora());
                       map.put("${titular}",warranty.getTitular());
                       map.put("${currency}",warranty.getCurrency());
                       map.put("${literalMoneda}",warranty.getCurrency().equals("BS")?"BOLIVIANOS":"DOLARES");
                       map.put("${assessmentEntity}",String.format("%,.2f", warranty.getAssessmentEntity()));
                       map.put("${literalMontoDpf}", new NumberToLiteral()
                               .Convert(String.format("%.2f",warranty.getAssessmentEntity()),true,"N/A","float"));

                    }
                }
                try {
                    for (Parameter parameter : parameterList) {
                        origin = parameter.getDescriptionParameter();
//                        data.forEach((k, v) -> {
//                            String key = "${" + k + "}";
//                            origin = origin.replaceAll(Pattern.quote(key), v);
//                        });
                        map.forEach((k, v) -> {
                            origin = origin.replaceAll(Pattern.quote(k), v);
                        });
                        if (!data.containsKey(parameter.getValueParameter()))
                            data.put(parameter.getValueParameter(), origin);
                        else {
                            String value = data.get(parameter.getValueParameter());
                            if (value.contains("${")) {
                                data.replace(parameter.getValueParameter(), value, origin);
                            } else {
                                origin = origin + " y " + value;
                                data.replace(parameter.getValueParameter(), value, origin);
                            }
                        }

                    }
                }catch (Exception e){
                    System.out.println(e);
                }

            }

        }catch (Exception e){

        }

        return data;
    }

    private Map<String,String> replaceCicleVariables(Map<String,String> data, String json, String type){
        ObjectMapper mapper = new ObjectMapper();
        List<CoDebtorGuarantor> coDebtorGuarantorList = new ArrayList<>();

        Map<String,String> map = new HashMap<>();
        try {
            ParameterService parameterService = new ParameterService();
            coDebtorGuarantorList = Arrays.asList(mapper.readValue(json,CoDebtorGuarantor[].class));
            List<Parameter> parameterList = new ArrayList<>();
            parameterList = parameterService.findParameterByTypeAndValue("custom_variable_contract",type);
            if (coDebtorGuarantorList.size()>0){

                coDebtorGuarantorList.sort(Comparator.comparing(CoDebtorGuarantor::getPrioridad).reversed());
                for(CoDebtorGuarantor coDebtorGuarantor : coDebtorGuarantorList) {
                    map.put("${name}",coDebtorGuarantor.getName());
                    if (coDebtorGuarantor.getTipoDireccion().equals("URBANA")) {
                        map.put("${addressHome}", coDebtorGuarantor.getAddressHome() + " " + coDebtorGuarantor.getNumeroCasa() + ", " + coDebtorGuarantor.getAdyacentes()
                                + ", " + coDebtorGuarantor.getZona() + ", " + coDebtorGuarantor.getCiudad()
                                + ", DEL DEPARTAMENTO DE " + coDebtorGuarantor.getDepartamento()
                        );
                    }else{
                        map.put("${addressHome}", coDebtorGuarantor.getAddressHome() + " " + coDebtorGuarantor.getNumeroCasa()+ ", "  + coDebtorGuarantor.getAdyacentes()
                                + ", " + coDebtorGuarantor.getZona() + ", " + coDebtorGuarantor.getCiudad()
                                + ", " + coDebtorGuarantor.getProvincia() +  ", DEL DEPARTAMENTO DE " + coDebtorGuarantor.getDepartamento()
                        );
                    }
                    map.put("${addressHomeStreet}",coDebtorGuarantor.getAddressHome());
                    map.put("${addressOffice}",coDebtorGuarantor.getAddressOffice());
                    map.put("${identifyCard}",coDebtorGuarantor.getIdentifyCard());
                    map.put("${gender}",coDebtorGuarantor.getGender());
                    map.put("${civilStatus}",coDebtorGuarantor.getCivilStatus());
                    map.put("${codeMebership}",coDebtorGuarantor.getCodeMebership().toString());
                    map.put("${adyacentes}",coDebtorGuarantor.getAdyacentes());
                    map.put("${zona}",coDebtorGuarantor.getZona());
                    map.put("${ciudad}",coDebtorGuarantor.getCiudad());
                    map.put("${provincia}",coDebtorGuarantor.getProvincia());
                    map.put("${departamento}",coDebtorGuarantor.getDepartamento());


                    for (Parameter parameter : parameterList) {
                        origin = parameter.getDescriptionParameter();
                        data.forEach((k,v)-> {
                            String key = "${"+k+"}";
                            origin =  origin.replaceAll(Pattern.quote(key),v);
                        } );
                        map.forEach((k,v) ->{
                          origin =  origin.replaceAll(Pattern.quote(k),v);
                        });
                        if (!data.containsKey(parameter.getValueParameter()))
                            data.put(parameter.getValueParameter(),origin);
                        else {
                           String value =  data.get(parameter.getValueParameter());
                           if (value.contains("${")){
                               data.replace(parameter.getValueParameter(),value,origin);
                           }else{
                               origin = origin +"\n"  +value;
                               data.replace(parameter.getValueParameter(),value,origin);
                           }
                        }

                    }
                }

            }else{
                for (Parameter parameter : parameterList) {
//                    data.put(parameter)
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }



    private boolean validateSignatoryEntity(){
        BranchOfficeService branchOfficeService = new BranchOfficeService();
        BranchOffice branchOffice =  branchOfficeService.findSignatorieByBranchOffice(loanData.getBranchOfficeId());
        if (branchOffice.getSignatories().equals("[]"))
            return false;
        return true;
    }

    private boolean validateData(){
        if (dateContractGenerate.isEmpty()) return false;
        if (txtLoanDataId.isEmpty()) return false;
        if (txtFileNameContract.isEmpty()) return false;
        return true;
    }

    private boolean validateSearchCriteria(){
        if (cmbSearchBy.isEmpty()) return false;
        if (txtTextSearch.isEmpty()) return false;
        return true;
    }

    private void fillGridLoanData(String criteria, String textSearch){
        LoanDataService loanDataService = new LoanDataService();
        List<LoanData> loanDataList = new ArrayList<>();
        if (criteria.equals("Numero credito")){

            if (StringUtils.isNumeric(textSearch))
               loanDataList = java.util.Arrays.asList(loanDataService.findLoanDataByLoanNumber(Integer.parseInt(textSearch)));
            else {
                Notification.show("Contrato",
                        "Ingrese un numero de credito valido",
                        Notification.Type.ERROR_MESSAGE);
                txtTextSearch.focus();
            }
        } else if (criteria.equals("Nombre deudor")){
            textSearch = textSearch.toUpperCase();
            txtTextSearch.setValue(textSearch);
            loanDataList = loanDataService.findLoanDataByDebtorName('%'+textSearch+'%');
        }
        gridLoanData.removeAllColumns();
        if (loanDataList!= null) {
            gridLoanData.setItems(loanDataList);
            gridLoanData.addColumn(LoanData::getLoanDataId).setCaption("ID");
            gridLoanData.addColumn(LoanData::getLoanNumber).setCaption("Nro credito");
            gridLoanData.addColumn(LoanData::getDebtorName).setCaption("Deudor");
        }
    }

    private void fillGridTemplateContract(){
        ParameterService parameterService = new ParameterService();
        gridContractTemplate.removeAllColumns();
        gridContractTemplate.setItems(parameterService.findParameterByType("contract"));
        gridContractTemplate.addColumn(Parameter::getParameterId).setCaption("ID").setHidden(true);
        gridContractTemplate.addColumn(Parameter::getValueParameter).setCaption("Contrato").setHidden(true);
        gridContractTemplate.addColumn(Parameter::getDescriptionParameter).setCaption("Descripcion");
        gridContractTemplate.addComponentColumn(parameter -> {
            Button button = new Button();
            button.setIcon(VaadinIcons.DOWNLOAD);
            button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
            button.addClickListener(clickEvent -> {
                if (verifyExistFileContract(pathGenerate)) {
                    final FileResource res = new FileResource(new File(pathGenerate));
                    res.setCacheTime(0);
                    FileDownloader fd = new FileDownloader(res) {
                        @Override
                        public boolean handleConnectorRequest(VaadinRequest request,
                                                              VaadinResponse response, String path) throws IOException {

                            boolean result = super.handleConnectorRequest(request, response, path);

                            // Now the accept can be processed
                            // close the dialog here

                            return result;
                        }
                    };


                    fd.extend(button);
                } else {
                    Notification.show("Contrato",
                            "Contrato no generado",
                            Notification.Type.ERROR_MESSAGE);
                }
            });
            return button;
        });
    }


    private GridLayout buildMainGridLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(10);
        gridMainLayout.setRows(5);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        panelSearch = new Panel();
        panelSearch.setStyleName(ValoTheme.PANEL_WELL);
        panelSearch.setSizeFull();
        panelSearch.setContent(buildHorizontalLayout());
        gridMainLayout.addComponent(panelSearch,0,0,4,0);

        txtContractId = new TextField("Id:");
        txtContractId.setEnabled(false);
        txtContractId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        gridMainLayout.addComponent(txtContractId,9,0);
        gridMainLayout.setComponentAlignment(txtContractId,Alignment.BOTTOM_RIGHT);

        gridMainLayout.addComponent(buildPanelGridLoanData(),0,1,4,1);
        gridMainLayout.addComponent(buildPanelGridTemplateContract(),5,1,9,1);
        gridMainLayout.addComponent(buildGenerateContract(),0,2,9,2);

        return gridMainLayout;
    }

    private Panel buildPanelGridLoanData(){
        panelGridLoanData = new Panel();
        panelGridLoanData.setStyleName(ValoTheme.PANEL_WELL);
        panelGridLoanData.setWidth("100%");
        panelGridLoanData.setHeight("80%");

        gridLoanData = new Grid<>();
        gridLoanData.setStyleName(ValoTheme.TABLE_COMPACT);
        gridLoanData.setWidth("100%");
        gridLoanData.setHeight("350px");
        panelGridLoanData.setContent(gridLoanData);

        return panelGridLoanData;
    }

    private Panel buildPanelGridTemplateContract(){
        panelGridTemplateContract = new Panel("Lista modelos de contratos");
        panelGridTemplateContract.setStyleName(ValoTheme.PANEL_WELL);
        panelGridTemplateContract.setWidth("100%");
        panelGridTemplateContract.setHeight("80%");

        gridContractTemplate = new Grid<>();
        gridContractTemplate.setStyleName(ValoTheme.TABLE_COMPACT);
        gridContractTemplate.setWidth("100%");
        gridContractTemplate.setHeight("350px");
        panelGridTemplateContract.setContent(gridContractTemplate);

        return panelGridTemplateContract;
    }

    private Panel buildGenerateContract(){
        panelGenerateContract = new Panel();
        panelGenerateContract.setStyleName(ValoTheme.PANEL_WELL);
        panelGenerateContract.setWidth("100%");
        panelGenerateContract.setHeight("80%");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        txtLoanDataId = new TextField("ID credito:");
        txtLoanDataId.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtLoanDataId.setEnabled(false);
        txtLoanDataId.setRequiredIndicatorVisible(true);
        horizontalLayout.addComponent(txtLoanDataId);

        txtFileNameContract = new TextField("Archivo contrato:");
        txtFileNameContract.setStyleName(ValoTheme.TEXTFIELD_TINY);
        txtFileNameContract.setEnabled(false);
        txtFileNameContract.setRequiredIndicatorVisible(true);
        horizontalLayout.addComponent(txtFileNameContract);

        dateContractGenerate = new DateField("Fecha contrato:");
        dateContractGenerate.setStyleName(ValoTheme.DATEFIELD_TINY);
        dateContractGenerate.setDateFormat("yyyy-MM-dd");
        dateContractGenerate.setRequiredIndicatorVisible(true);
        horizontalLayout.addComponent(dateContractGenerate);

        txtDescription = new TextField("Descripcion");
        txtDescription.setStyleName(ValoTheme.TEXTFIELD_TINY);
        horizontalLayout.addComponent(txtDescription);

        btnInsured = new Button("Asegurados");
        btnInsured.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnInsured.setIcon(VaadinIcons.EYE);
        horizontalLayout.addComponent(btnInsured);
        horizontalLayout.setComponentAlignment(btnInsured,Alignment.BOTTOM_LEFT);

        btnGenerateContract = new Button("Generar");
        btnGenerateContract.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnGenerateContract.setIcon(VaadinIcons.DATABASE);
        horizontalLayout.addComponent(btnGenerateContract);
        horizontalLayout.setComponentAlignment(btnGenerateContract,Alignment.BOTTOM_LEFT);

        btnDownloadContract = new Button("Descargar");
        btnDownloadContract.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnDownloadContract.setIcon(VaadinIcons.DOWNLOAD);
        btnDownloadContract.setVisible(false);
        horizontalLayout.addComponent(btnDownloadContract);
        horizontalLayout.setComponentAlignment(btnDownloadContract,Alignment.BOTTOM_LEFT);

        panelGenerateContract.setContent(horizontalLayout);

        return panelGenerateContract;
    }

    private HorizontalLayout buildHorizontalLayout(){
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        cmbSearchBy = new ComboBox("Buscar por:");
        cmbSearchBy.setStyleName(ValoTheme.COMBOBOX_TINY);
        cmbSearchBy.setEmptySelectionAllowed(false);
        cmbSearchBy.setItems("Numero credito","Nombre deudor");
        horizontalLayout.addComponent(cmbSearchBy);

        txtTextSearch = new TextField();
        txtTextSearch.setStyleName(ValoTheme.TEXTFIELD_TINY);
        horizontalLayout.addComponent(txtTextSearch);
        horizontalLayout.setComponentAlignment(txtTextSearch,Alignment.BOTTOM_LEFT);

        btnSearch = new Button("Buscar");
        btnSearch.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnSearch.setIcon(VaadinIcons.SEARCH);
        horizontalLayout.addComponent(btnSearch);
        horizontalLayout.setComponentAlignment(btnSearch,Alignment.BOTTOM_LEFT);

        return horizontalLayout;
    }

}
