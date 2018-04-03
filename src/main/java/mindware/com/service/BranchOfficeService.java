package mindware.com.service;

import mindware.com.mappers.BranchOfficeMapper;
import mindware.com.model.BranchOffice;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class BranchOfficeService {
    public void insertListBranchOffice(List<BranchOffice> branchOfficeList){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            BranchOfficeMapper branchOfficeMapper = sqlSession.getMapper(BranchOfficeMapper.class);
            for (BranchOffice branchOffice:branchOfficeList) {
                branchOfficeMapper.insertBranchOffice(branchOffice);
            }
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public List<BranchOffice> findAllBranchOffice(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            BranchOfficeMapper branchOfficeMapper = sqlSession.getMapper(BranchOfficeMapper.class);
            return branchOfficeMapper.findAllBranchOffice();

        }finally {
            sqlSession.close();
        }
    }


}
