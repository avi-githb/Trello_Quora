package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteQuestionService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(final String authorization, final String questionId) throws InvalidQuestionException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization); //all user details

        //to delete a question either own a question or role==admin

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }


        QuestionEntity questionEntity = userDao.getQuestionFromUuid(questionId); //all attributes of question

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userAuthTokenEntity.getUuid());
        UserEntity userEntityFromQuestion = questionEntity.getUser();

        if (!(userEntity.getUuid().equals(userEntityFromQuestion.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        String deletedQuestionUuid = questionEntity.getUuid();

        userDao.deleteQuestionFromUuid(deletedQuestionUuid);
        return deletedQuestionUuid; //return String
    }
}
