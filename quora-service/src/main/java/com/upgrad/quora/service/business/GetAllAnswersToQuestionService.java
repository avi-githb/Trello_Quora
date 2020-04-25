package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class is used to get all answers to a particular question. Any user can access this endpoint.
 */

@Service
public class GetAllAnswersToQuestionService {

    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * this method return "uuid" of the answer, "content" of the question and "content" of all the answers posted for
     that particular question from the database in the JSON response with the corresponding HTTP status.
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {


        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);

        /**
         * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException"
         */
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        /**
         * If the user has signed out, throw "AuthorizationFailedException"
         */
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        /**
         * If the question with uuid whose answers are to be retrieved from the database does not exist in the database, throw "InvalidQuestionException"
         */
        if (questionDao.getQuestionFromUuid(questionId) == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        QuestionEntity questionEntity = questionDao.getQuestionFromUuid(questionId);

        List<AnswerEntity> allAnswerFromQuestionId = answerDao.getAnswersToQuestion(questionEntity);

        return allAnswerFromQuestionId;
    }

}
