package mindware.com.service;

import mindware.com.mappers.LoanDataMapper;
import mindware.com.model.LoanData;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class LoanDataService {
    public void insertLoanData(LoanData loanData){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            LoanDataMapper loanDataMapper = sqlSession.getMapper(LoanDataMapper.class);
            loanDataMapper.insertLoanData(loanData);
            sqlSession.commit();
        }
        finally {
            sqlSession.close();
        }
    }

    public void updateLoanData(LoanData loanData){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            LoanDataMapper loanDataMapper = sqlSession.getMapper(LoanDataMapper.class);
            loanDataMapper.updateLoanData(loanData);
            sqlSession.commit();
        }
        finally {
            sqlSession.close();
        }
    }

    public void updateInputData(LoanData loanData){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            LoanDataMapper loanDataMapper = sqlSession.getMapper(LoanDataMapper.class);
            loanDataMapper.updateInputData(loanData);
            sqlSession.commit();
        }
        finally {
            sqlSession.close();
        }
    }

    public void updateCodebtor(LoanData loanData){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            LoanDataMapper loanDataMapper = sqlSession.getMapper(LoanDataMapper.class);
            loanDataMapper.updateCodebtor(loanData);
            sqlSession.commit();
        }
        finally {
            sqlSession.close();
        }
    }

    public LoanData findLoanDataByLoanNumber(int loanNumber){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            LoanDataMapper loanDataMapper = sqlSession.getMapper(LoanDataMapper.class);
            return loanDataMapper.findLoanDataByLoanNumber(loanNumber);
        }
        finally {
            sqlSession.close();
        }
    }

    public List<LoanData> findLoanDataByDebtorName(String debtorName){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            LoanDataMapper loanDataMapper = sqlSession.getMapper(LoanDataMapper.class);
            return loanDataMapper.findLoanDataByDebtorName(debtorName);
        }
        finally {
            sqlSession.close();
        }
    }
}
