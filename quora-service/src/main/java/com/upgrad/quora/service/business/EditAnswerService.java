package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditAnswerService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(final String authorization, final AnswerEntity answerEntity, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization);

        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answerToBeEdited = userDao.getAnswerFromUuid(answerId);


        if(answerToBeEdited ==null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        long id1 = answerToBeEdited.getUser().getId();
        long id2 = userAuthTokenEntity.getUser().getId();

        if(id1!=id2){
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        }

        answerToBeEdited.setAnswer(null);
        answerToBeEdited.setAnswer(answerEntity.getAnswer());

        return  answerToBeEdited;
    }
}
