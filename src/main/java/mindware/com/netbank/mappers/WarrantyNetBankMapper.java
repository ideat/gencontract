package mindware.com.netbank.mappers;

import mindware.com.netbank.model.WarrantyNetbank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarrantyNetBankMapper {
    List<WarrantyNetbank> findWarrantyNetbankByCreCod(@Param("prgarnpre") int prgarnpre);
}
