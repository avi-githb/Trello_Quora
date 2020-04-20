package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthTokenEntity getUserByAuthtoken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestions() {

        List<Object[]> objects = entityManager.createNamedQuery("getAllQuestions", Object[].class).getResultList();
        List<QuestionEntity> allQuestions = new ArrayList<QuestionEntity>();
        for (Object[] row : objects) {
            QuestionEntity temp = new QuestionEntity();
            temp.setUuid((String) row[0]);
            temp.setContent((String) row[1]);
            allQuestions.add(temp);
        }
        return allQuestions;
    }

    public QuestionEntity getQuestionFromUuid(final String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public void deleteAnswerFromUuid(final String answerId) {
        entityManager.createQuery("delete from AnswerEntity u where u.uuid =:answerId").setParameter("answerId", answerId).executeUpdate();
    }

    public void deleteQuestionFromUuid(final String questionId) {
        entityManager.createQuery("delete from QuestionEntity u where u.uuid =:questionId").setParameter("questionId", questionId).executeUpdate();
    }


    public List<QuestionEntity> getQuestionFromId(final UserEntity user) {
        try {
            List<QuestionEntity> allQuestionFromUserId = entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("user", user).getResultList();
            return allQuestionFromUserId;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserEntity getUserFromUuid(final String userId) {
        try {
            return entityManager.createNamedQuery("getUserByUuid", UserEntity.class).setParameter("userId", userId).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }


    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }


    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<AnswerEntity> getAnswersToQuestion(final QuestionEntity question) {
        try {
            List<AnswerEntity> allAnswersToQuestion = entityManager.createNamedQuery("getAnswersByQuestionId", AnswerEntity.class).setParameter("question", question).getResultList();
            return allAnswersToQuestion;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public String deleteUser(final String userId) {
        entityManager.createQuery("DELETE from UserEntity where uuid=:userId").setParameter("userId", userId).executeUpdate();
        return userId;
    }

    public AnswerEntity getAnswerFromUuid(final String answerUuId) {
        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class).setParameter("uuid", answerUuId).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

}
