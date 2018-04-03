package mindware.com.service;

import mindware.com.mappers.LoanDataMapper;
import mindware.com.model.LoanData;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

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
}
