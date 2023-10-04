package it.polimi.db2.marketing.services;

import it.polimi.db2.marketing.entities.*;
import it.polimi.db2.marketing.exception.QuestionnaireException;
import it.polimi.db2.marketing.exception.QuestionnaireNotFoundException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class QuestionnaireService {
    @PersistenceContext(unitName = "MarketingGameEJB")
    private EntityManager em;

    public Questionnaire createQuestionnaire(int productId, Date date) throws QuestionnaireException{
        Questionnaire newQuestionnaire = new Questionnaire();
        newQuestionnaire.setDate(date);
        Product product = em.find(Product.class, productId);
        newQuestionnaire.setProduct(product);
        try{
            em.persist(newQuestionnaire);
        }catch (PersistenceException ex){
            throw new QuestionnaireException("Error while creating questionnaire.");
        }
        return newQuestionnaire;
    }

    public void addQuestion(int questId, String text){
        Questionnaire questionnaire = em.find(Questionnaire.class, questId);
        Question question = new Question();
        questionnaire.addQuestion(question);
        question.setText(text);
        em.persist(question);
    }

    public Questionnaire getQuestionnaire(int questID) throws QuestionnaireNotFoundException, QuestionnaireException {
        Questionnaire q = em.find(Questionnaire.class, questID);
        if(q == null)
            throw new QuestionnaireNotFoundException();
        em.refresh(q);
        return q;
    }

    public Questionnaire getQuestionnaireOfTheDay() throws QuestionnaireNotFoundException {
        Questionnaire questionnaire;
        try{
            questionnaire = em.createNamedQuery("Questionnaire.getQuestionnaireByDay", Questionnaire.class)
                    .setParameter("date", new java.util.Date(System.currentTimeMillis()))
                    .setHint("javax.persistence.cache.storeMode", "REFRESH")
                    .getSingleResult();
        }catch (PersistenceException ex){
            throw new QuestionnaireNotFoundException();
        }
        return questionnaire;
    }

    public List<Questionnaire> getQuestionnaires() throws QuestionnaireException {
        List<Questionnaire> questionnaires;
        try{
            questionnaires = em.createNamedQuery("Questionnaire.getQuestionnaires", Questionnaire.class)
                    .setHint("javax.persistence.cache.storeMode", "REFRESH")
                    .getResultList();
        }catch (PersistenceException ex){
            throw new QuestionnaireException("Cannot find questionnaire of the day");
        }
        return questionnaires;
    }

    public List<User> getSubmittedUsersByQuestionnaire(int questID) throws QuestionnaireNotFoundException, QuestionnaireException {
        Questionnaire q = em.find(Questionnaire.class, questID);
        if(q == null)
            throw new QuestionnaireNotFoundException();
        List<User> users;
        try{
            users = em.createNamedQuery("Questionnaire.getSubmittedUsersByQuestionnaire", User.class)
                    .setParameter("questionnaire", q)
                    .getResultList();
        }catch (PersistenceException ex){
            throw new QuestionnaireException("Cannot find users");
        }

        return users;
    }

    public List<User> getCancelledUsersByQuestionnaire(int questId) throws QuestionnaireNotFoundException, QuestionnaireException {
        Questionnaire q = em.find(Questionnaire.class, questId);
        if(q == null)
            throw new QuestionnaireNotFoundException();
        List<User> users;
        try{
            Calendar c = Calendar.getInstance();
            c.setTime(q.getDate());
            c.add(Calendar.DATE, 1);
            users = em.createNamedQuery("Questionnaire.usersWhoCancelledQuestionnaire", User.class)
                .setParameter("questionnaire", q)
                    .setParameter("nextday", c, TemporalType.DATE)
                    .setHint("javax.persistence.cache.storeMode", "REFRESH")
                .getResultList();
        }catch (PersistenceException ex){
            throw new QuestionnaireException("Cannot find users");
        }
        return users;
    }

    public List<Answer> getQuestionnaireAnswerByUser(int questId, int userId) throws QuestionnaireNotFoundException, QuestionnaireException {
        Questionnaire q = em.find(Questionnaire.class, questId);
        User u = em.find(User.class, userId);
        if(q == null)
            throw new QuestionnaireNotFoundException();
        if(u == null)
            throw new QuestionnaireException("Unknown user"); //in this case the userId in the session has been changed by the client
        List<Answer> answers;
        try{
            answers = em.createNamedQuery("Questionnaire.answersByUserAndQuestionnaire", Answer.class)
                    .setParameter("questionnaire", q)
                    .setParameter("user", u)
                    .getResultList();
        }catch (PersistenceException ex){
            throw new QuestionnaireException("Cannot find users");
        }
        return answers;
    }

    public void deleteQuestionnaire(int questId) throws QuestionnaireNotFoundException {
        Questionnaire q = em.find(Questionnaire.class, questId);
        if(q == null)
            throw new QuestionnaireNotFoundException();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if(q.getDate().before(c.getTime()))
            em.remove(q);
    }

    public void deleteQuestion(int questId) throws QuestionnaireNotFoundException {
        Question q = em.find(Question.class, questId);
        if(q == null)
            throw new QuestionnaireNotFoundException();
        em.remove(q);
    }

}
