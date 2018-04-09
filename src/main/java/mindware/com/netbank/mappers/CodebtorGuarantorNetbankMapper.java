package mindware.com.netbank.mappers;

import mindware.com.netbank.model.CodebtorGuarantorNetbank;

import java.util.List;

public interface CodebtorGuarantorNetbankMapper {
    List<CodebtorGuarantorNetbank> findCodeptorByNumberLoan(int numberLoan);
    List<CodebtorGuarantorNetbank> findGuarantorByNumberLoan(int numberLoan);
}
