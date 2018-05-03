package mindware.com.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import mindware.com.model.Contract;
import mindware.com.model.ListContractLoan;
import mindware.com.service.ContractService;
import mindware.com.utilities.Util;
import org.apache.commons.io.FileUtils;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListContractsForm extends CustomComponent implements View {
    private GridLayout gridMainLayout;
    private Grid<ListContractLoan> gridListContract;
    private Panel panelListContract;
    private Button btnDownloadContract;
    private GridCellFilter<Contract> filter;
    private String fileContract;

    public ListContractsForm(){

        setCompositionRoot(buildGridMainLayout());

        gridListContract.setItems(getListContract());
        postBuild(gridListContract);
    }

    private void postBuild(final Grid grid){
        fillGridListContract(grid);
        btnDownloadContract.addClickListener(clickEvent -> {
            Resource res = new FileResource(new File(fileContract));
            FileDownloader fd = new FileDownloader(res);
            fd.extend(btnDownloadContract);
        });

        gridListContract.addItemClickListener( itemClick -> {
           fileContract = itemClick.getItem().getFileNameContract();
           File file = new File(fileContract);
           if (!file.exists()){
               Notification.show("Contrato",
                       "No existe el contrato generado",
                       Notification.Type.ERROR_MESSAGE);
               btnDownloadContract.setEnabled(false);
           }else {
               btnDownloadContract.setEnabled(true);
           }

        });

    }

    private void fillGridListContract(final Grid grid){

        grid.getColumn("description").setHidden(true);
        grid.getColumn("fileNameContract").setHidden(true);

        grid.setColumnOrder("contractId","loanNumber","debtorName","dateContract", "currency","loanMount","description");
        grid.getColumn("loanNumber").setCaption("Nro prestamo").setWidth(170);
        grid.getColumn("contractId").setCaption("Id").setWidth(50);

        grid.getColumn("dateContract").setCaption("Fecha")
                .setRenderer(new DateRenderer(("%1$td-%1$tm-%1$tY"))).setWidth(180);
        grid.getColumn("loanMount").setCaption("Monto").setWidth(120);
        grid.getColumn("debtorName").setCaption("Deudor").setWidth(300);
        grid.getColumn("currency").setCaption("Moneda").setWidth(90);

        this.filter = new GridCellFilter(grid);

        this.filter.setDateFilter("dateContract",  new SimpleDateFormat("dd-MMM-yyyy"),true);
        this.filter.setComboBoxFilter("currency", String.class , Arrays.asList("BS", "$US"));
        this.filter.setTextFilter("debtorName", true,false);
        this.filter.setNumberFilter("loanNumber",Integer.class,"Prestamo invalido","","");
        this.filter.setNumberFilter("loanMount", Double.class, "Monto invalido", "desde", "hasta");

    }

    private List<ListContractLoan> getListContract(){
        ContractService contractService = new ContractService();
        List<Contract> contractList = contractService.findAllContract();
        List<ListContractLoan> listContractLoans = new ArrayList<>();
        Util util = new Util();
        for (Contract contract:contractList){
            ListContractLoan listContractLoan = new ListContractLoan();
            listContractLoan.setContractId(contract.getContractId());
            listContractLoan.setCurrency(contract.getLoanData().getCurrency());

            listContractLoan.setDateContract(contract.getDateContract());
            listContractLoan.setDebtorName(contract.getLoanData().getDebtorName());
            listContractLoan.setLoanMount(contract.getLoanData().getLoanMount());
            listContractLoan.setLoanNumber(contract.getLoanData().getLoanNumber());
            listContractLoan.setDescription(contract.getDescription());
            listContractLoan.setFileNameContract(contract.getFileNameContract());
            listContractLoans.add(listContractLoan);

        }

        return listContractLoans;
    }

    private GridLayout buildGridMainLayout(){
        gridMainLayout = new GridLayout();
        gridMainLayout.setColumns(7);
        gridMainLayout.setRows(5);
        gridMainLayout.setSpacing(true);
        gridMainLayout.setSizeFull();

        btnDownloadContract = new Button("Descargar");
        btnDownloadContract.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnDownloadContract.setIcon(VaadinIcons.DOWNLOAD);
        btnDownloadContract.setEnabled(false);
        gridMainLayout.addComponent(btnDownloadContract,6,0);
        gridMainLayout.setComponentAlignment(btnDownloadContract,Alignment.BOTTOM_RIGHT);

        panelListContract = new Panel("<font size=3 color=#163759> Contratos generados <font>");
        panelListContract.setCaptionAsHtml(true);
        panelListContract.setStyleName(ValoTheme.PANEL_WELL);
        panelListContract.setSizeFull();

        gridListContract = new Grid<>(ListContractLoan.class);
        gridListContract.setStyleName(ValoTheme.TABLE_SMALL);
        gridListContract.setWidth("100%");
        panelListContract.setContent(gridListContract);

        gridMainLayout.addComponent(panelListContract,0,1,6,4);

        return gridMainLayout;
    }


}
