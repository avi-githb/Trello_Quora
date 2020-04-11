package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllQuestionByUserSerivce {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionByUser(final String userId){

        UserEntity userEntity = userDao.getUserFromUuid(userId); //userId = UUID

        List<QuestionEntity> allQuestionFromUserId = userDao.getQuestionFromId(userEntity); //getId = Internal UserID

        return allQuestionFromUserId;
    }

}
