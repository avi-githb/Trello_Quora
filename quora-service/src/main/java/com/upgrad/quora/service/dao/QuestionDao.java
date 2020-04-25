package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

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

    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }


}
