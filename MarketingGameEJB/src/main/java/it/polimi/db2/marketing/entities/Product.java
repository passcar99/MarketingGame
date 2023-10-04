package it.polimi.db2.marketing.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Base64;
import java.util.Collection;

@Entity
@Table(name = "product", schema = "marketinggame")
@NamedQueries({
        @NamedQuery(name = "Product.getProductByDay", query = "SELECT p FROM Product p, Questionnaire q WHERE q.product = p AND q.date = :date"),
        @NamedQuery(name = "Product.getReviewsByProduct", query = "SELECT r FROM Product p JOIN p.reviews r WHERE p.id = :product")
})

public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @OneToOne(mappedBy = "product")
    private Questionnaire questionnaire;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Collection<Review> reviews;

    private String name;


    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte[] getImage() {
        return image;
    }

    public String getImageData() {
        return Base64.getMimeEncoder().encodeToString(image);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void addReview(Review review){
        reviews.add(review);
        review.setProduct(this);
    }

    public void setQuestionnaire(Questionnaire q) {
        questionnaire = q;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }
}
