package mindware.com.mappers;

import mindware.com.model.LoanData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoanDataMapper {
    void insertLoanData(LoanData loanData);
    LoanData findLoanDataByLoanNumber(@Param("loanNumber") int loanNumber);
    List<LoanData> findLoanDataByDebtorName(@Param("debtorName") String debtorName);
}
