package mindware.com.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class ManageBranchOfficeForm extends CustomComponent implements View {
    public ManageBranchOfficeForm(){
        Label label = new Label("Gestion de sucursales!!");
        setCompositionRoot(label);
    }
}
