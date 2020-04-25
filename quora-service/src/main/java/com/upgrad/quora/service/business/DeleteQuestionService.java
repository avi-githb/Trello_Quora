package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
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

/**
 * THis class is used to delete a question that has been posted by a user. Note, only the question owner of the question or admin can delete a question.
 */

@Service
public class DeleteQuestionService {

    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * this method delete the question from the database and return 'uuid' of the deleted question and
     message -'QUESTION DELETED' in the JSON response with the corresponding HTTP status.
     */
    public String deleteQuestion(final String authorization, final String questionId) throws InvalidQuestionException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization); //all user details

        /**
         * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException'
         */

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        /**
         * If the user has signed out, throw 'AuthorizationFailedException'
         */

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionFromUuid(questionId); //all attributes of question

        /**
         * If the question with uuid which is to be deleted does not exist in the database, throw 'InvalidQuestionException'
         */
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userAuthTokenEntity.getUuid());
        UserEntity userEntityFromQuestion = questionEntity.getUser();

        /**
         * if the user who is not the owner of the question or the role of the user is ‘nonadmin’ and
         tries to delete the question, throw 'AuthorizationFailedException'
         */

        if (!(userEntity.getUuid().equals(userEntityFromQuestion.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        String deletedQuestionUuid = questionEntity.getUuid();

       questionDao.deleteQuestionFromUuid(deletedQuestionUuid);
        return deletedQuestionUuid; //return String
    }
}
