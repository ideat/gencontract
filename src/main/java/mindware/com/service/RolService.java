package mindware.com.service;

import mindware.com.mappers.RolMapper;
import mindware.com.model.Rol;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class RolService {
    public List<Rol> findAllRol(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            RolMapper rolMapper = sqlSession.getMapper(RolMapper.class);
            return rolMapper.findAllRol();
        }finally {
            sqlSession.close();
        }
    }

    public Rol findAllRolMenuOptionByRolId(int rolId){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            RolMapper rolMapper = sqlSession.getMapper(RolMapper.class);
            return rolMapper.findAllRolMenuOptionByRolId(rolId);
        }finally {
            sqlSession.close();
        }
    }

    public void insertRol(Rol rol){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            RolMapper rolMapper = sqlSession.getMapper(RolMapper.class);
            rolMapper.insertRol(rol);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public void updateRol(Rol rol){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            RolMapper rolMapper = sqlSession.getMapper(RolMapper.class);
            rolMapper.updateRol(rol);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }
}
