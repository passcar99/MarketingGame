package it.polimi.db2.marketing.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "questionnaire", schema = "marketinggame")
@NamedQueries({
        @NamedQuery(name = "Questionnaire.getSubmittedUsersByQuestionnaire",
                query = "SELECT u FROM Questionnaire q JOIN q.questions v JOIN v.answers a JOIN a.user u WHERE q = :questionnaire GROUP BY u"),
        @NamedQuery(name = "Questionnaire.answersByUserAndQuestionnaire",
                query = "SELECT a FROM Questionnaire q JOIN q.questions v JOIN v.answers a WHERE a.user = :user and q = :questionnaire"),
        @NamedQuery(name = "Questionnaire.getQuestionnaireByDay", query = "SELECT q FROM Questionnaire q WHERE q.date = :date"),
        @NamedQuery(name = "Questionnaire.getQuestionnaires", query = "SELECT q FROM Questionnaire q"),
        @NamedQuery(name = "Questionnaire.usersWhoCancelledQuestionnaire",
                query = "SELECT l.user FROM Questionnaire q, Login l WHERE l.timestamp>= q.date and l.timestamp<= :nextday " +
                        "and q = :questionnaire and l.user NOT IN (SELECT a.user FROM Answer a WHERE a.question.questionnaire = q) GROUP BY l.user")
})
public class Questionnaire implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "questionnaire", cascade = CascadeType.ALL)
    private Collection<Question> questions;

    @OneToOne
    @JoinColumn(name = "product")
    private Product product;

    @Temporal(TemporalType.DATE)
    private Date date;

    public void addQuestion(Question question){
        questions.add(question);
        question.setQuestionnaire(this);
    }

    public Collection<Question> getQuestions() {
        return questions;
    }

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        product.setQuestionnaire(this);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }
}
