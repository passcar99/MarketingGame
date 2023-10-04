package it.polimi.db2.marketing.entities;

import javax.persistence.*;

@Entity
@Table(name = "review", schema = "marketinggame")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product")
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private User user;
    @Column(name = "text")
    private String review;

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }
    public String getReview() {
        return review;
    }
}
