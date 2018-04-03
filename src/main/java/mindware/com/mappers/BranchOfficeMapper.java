package mindware.com.mappers;

import mindware.com.model.BranchOffice;

import java.util.List;

public interface BranchOfficeMapper {
    List<BranchOffice> findAllBranchOffice();
    void updateSignatoriesBranchOffice(BranchOffice branchOffice);
    void insertBranchOffice(BranchOffice branchOffice);
}
