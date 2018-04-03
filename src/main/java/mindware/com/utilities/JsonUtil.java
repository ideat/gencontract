package mindware.com.utilities;

import com.google.gson.Gson;
import mindware.com.netbank.model.WarrantyNetbank;

import java.util.List;

public class JsonUtil {
    public String listWarrantyNetBankToJsonFormat(List<WarrantyNetbank> listWarrantyNetbank){

        Gson gson = new Gson();
        return  gson.toJson(listWarrantyNetbank);

    }
}
