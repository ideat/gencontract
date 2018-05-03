package mindware.com.service;

import mindware.com.mappers.UserMapper;
import mindware.com.model.User;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class UserService {
    public void insertUser(User user){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insertUser(user);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public void updateUser(User user){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.updateUser(user);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public void updateUserPassword(User user){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.updateUserPassword(user);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public List<User> findAllUser(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.findAllUser();
        }finally {
            sqlSession.close();
        }
    }

    public User findUserByUserId(Integer userId){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.findUserByUserId(userId);
        }finally {
            sqlSession.close();
        }
    }

    public User findUserByLogin(String login){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.findUserByLogin(login);
        }finally {
            sqlSession.close();
        }
    }
}
