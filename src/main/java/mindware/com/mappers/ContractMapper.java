package mindware.com.mappers;

import mindware.com.model.Contract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractMapper {
    List<Contract> findContractByLoanNumber(@Param("loanNumber") Integer loanNumber);
    List<Contract> findContractByNameDebtor(@Param("debtorName") String debtorName);
    List<Contract> findAllContract();
    void insertContract(Contract contract);
    void updateContract(Contract contract);
    void updateStateContract(Contract contract);
}
