package mindware.com.netbank.mappers;

import mindware.com.model.LoanData;
import mindware.com.netbank.model.ClientLoanNetbank;
import org.apache.ibatis.annotations.Param;


public interface ClientLoanNetbankMapper {
    ClientLoanNetbank findClientLoanNetbankByCreCod(@Param("prmprnpre") int prmprnpre);


}
