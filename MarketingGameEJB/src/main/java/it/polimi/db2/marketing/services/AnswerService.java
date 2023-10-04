package it.polimi.db2.marketing.services;

import it.polimi.db2.marketing.entities.Answer;
import it.polimi.db2.marketing.entities.Product;
import it.polimi.db2.marketing.entities.Question;
import it.polimi.db2.marketing.entities.User;
import it.polimi.db2.marketing.exception.AnswerException;
import it.polimi.db2.marketing.exception.IncompleteQuestionnaireException;
import it.polimi.db2.marketing.exception.OffensiveWordException;
import it.polimi.db2.marketing.exception.QuestionnaireException;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceException;
import java.util.*;

@Stateful
public class AnswerService {
    @PersistenceContext(unitName = "MarketingGameEJB", type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService productService;
    Map<Integer, Answer> answers = new HashMap<>();
    Product product = null;
    User user = null;

    private Product getProductOfTheDay() throws QuestionnaireException {
        if(product != null)
            return product;
        product = productService.getProductOfTheDay(false);
        return product;

    }

    public void addNewAnswer(int questId, int userId, String text) throws AnswerException, QuestionnaireException {
        getProductOfTheDay();
        Question question;
        question = em.find(Question.class, questId);
        if(question == null)
            throw new AnswerException("Question not found");
        if(user == null){
            user = em.find(User.class, userId);
        }
        Answer answer;
        if(answers.get(questId) == null) {
            answer = new Answer();
        }
        else
            answer = answers.get(questId);
        answer.setText(text);
        answers.put(questId, answer);
    }

    public void confirmAnswers() throws IncompleteQuestionnaireException, OffensiveWordException, AnswerException {
        if(product.getQuestionnaire().getNumberOfQuestions() > answers.size())
            throw new IncompleteQuestionnaireException();
        User user = em.find(User.class, this.user.getId());
        em.refresh(user);
        if(!nonOffensiveAnswers(new ArrayList(answers.values()))){
            user.setOffensive(true);
            cancelAnswers();
            throw new OffensiveWordException();
        }
        try{
            for (Map.Entry<Integer, Answer> entry : answers.entrySet()) {
                Integer key = entry.getKey();
                Answer value = entry.getValue();
                Question newQuestion = em.find(Question.class, key);
                em.refresh(newQuestion);
                newQuestion.addAnswer(value);
                user.addAnswer(value);
                em.persist(value);
            }
            em.flush();
        }catch (PersistenceException ex){
            throw new AnswerException("Error while uploadind answers.");
        }

    }

    public void cancelAnswers(){
        answers = new HashMap<>();
    }

    public boolean nonOffensiveAnswers(List<Answer> answers){
        Collection<String> offensive_words = em.createNamedQuery("Offensive_words.getAll", String.class)
                .setHint("javax.persistence.cache.storeMode", "REFRESH")
                .getResultList();
        HashSet<String> words = new HashSet<>();
        for(String w: offensive_words){
            words.add(w.toLowerCase());
        }
        for(Answer a: answers){
            if(!nonOffensiveAnswer(a.getText(), words))
                return false;
        }
        return true;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean nonOffensiveAnswer(String s, HashSet<String> words){
        String lower = s.toLowerCase();
        String[] tokens = lower.split("[^a-z]");
        for (String token : tokens) {
            if (words.contains(token))
                return false;
        }
        return true;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean allQuestionsAnswered() throws QuestionnaireException {
        System.out.println(getProductOfTheDay().getQuestionnaire().getNumberOfQuestions());
        return answers.size() == getProductOfTheDay().getQuestionnaire().getNumberOfQuestions();
    }

    @Remove
    public void remove(){

    }

}
