package it.polimi.db2.marketing.services;

import it.polimi.db2.marketing.entities.Product;
import it.polimi.db2.marketing.entities.Review;
import it.polimi.db2.marketing.exception.ProductException;
import it.polimi.db2.marketing.exception.ProductNotFoundException;
import it.polimi.db2.marketing.exception.QuestionnaireException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

@Stateless
public class ProductService {
    @PersistenceContext(unitName = "MarketingGameEJB")
    private EntityManager em;

    public Product getProductOfTheDay(boolean withImage) throws QuestionnaireException {
        Product product;
        byte[] image;
        try{
            product = em.createNamedQuery("Product.getProductByDay", Product.class)
                    .setParameter("date", new Date(System.currentTimeMillis()))
                    .getSingleResult();
            if(withImage){
                product.getImage();
            }
        }catch (PersistenceException ex){
            throw new QuestionnaireException("Cannot find product of the day");
        }
        return product;
    }


    public Product createNewProduct(String name, byte[] image){
        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setImage(image);
        em.persist(newProduct);
        return newProduct;
    }

    public List<Review> getReviewsByProduct(int prodID) throws ProductNotFoundException, ProductException {
        Product prod = em.find(Product.class, prodID);
        if(prod == null)
            throw new ProductNotFoundException();
        List<Review> reviews;
        try{
            reviews = em.createNamedQuery("Product.getReviewsByProduct", Review.class)
                    .setParameter("product", prod.getId())
                    .getResultList();
        }catch (PersistenceException ex){
            ex.printStackTrace();
            throw new ProductException("Cannot retrieve reviews");
        }
        return reviews;
    }

}
