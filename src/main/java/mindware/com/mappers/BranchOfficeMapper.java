package mindware.com.mappers;

import mindware.com.model.BranchOffice;
import mindware.com.model.Signatories;

import java.util.List;

public interface BranchOfficeMapper {
    List<BranchOffice> findAllBranchOffice();
    void updateSignatoriesBranchOffice(BranchOffice branchOffice);
    void insertBranchOffice(BranchOffice branchOffice);
    BranchOffice findSignatorieByBranchOffice(int branchOfficeId);
}
