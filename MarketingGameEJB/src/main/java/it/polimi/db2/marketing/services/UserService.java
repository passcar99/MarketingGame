package it.polimi.db2.marketing.services;

import it.polimi.db2.marketing.entities.ExpertiseLevel;
import it.polimi.db2.marketing.entities.Login;
import it.polimi.db2.marketing.entities.Sex;
import it.polimi.db2.marketing.entities.User;
import it.polimi.db2.marketing.exception.CredentialsException;
import it.polimi.db2.marketing.exception.RegistrationException;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Stateless
public class UserService {
    @PersistenceContext(unitName = "MarketingGameEJB")
    private EntityManager em;

    public UserService() {
    }

    public User checkCredentials(String usrn, String pwd) throws CredentialsException, NonUniqueResultException {
        List<User> userList = null;
        try {
            userList = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, usrn).setParameter(2, pwd)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new CredentialsException("Could not verify credentals");
        }
        if (userList.isEmpty())
            return null;
        else if (userList.size() == 1){
            Login login = new Login();
            login.setUser(userList.get(0));
            login.setTimestamp(new Date(System.currentTimeMillis()));
            em.persist(login);
            return userList.get(0);
        }

        throw new NonUniqueResultException("More than one user registered with same credentials");

    }

    public User createUser(String username, String password, String email) throws RegistrationException {
        User newUser;
        try {
            newUser = em.createNamedQuery("User.getUserByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException ex) {
            if (password != null && email != null) {
                newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);
                newUser.setPoints(0);
                em.persist(newUser);
                return newUser;
            }
        }catch (Exception e){
            throw new RegistrationException();
        }
    return null; //shouldn't get there
    }

    public List<User> getLeaderboard(){
        List<User> leaders = em.createNamedQuery("User.getLeaderBoard", User.class)
                .setHint("javax.persistence.cache.storeMode", "REFRESH")
                .getResultList();
        return leaders;
    }

    public void addStatisticalInfo(Integer age, Sex sex, ExpertiseLevel expertiseLevel, int userId){
        User user = em.find(User.class, userId);
        user.setExpertise_level(expertiseLevel);
        user.setAge(age);
        user.setSex(sex);
    }


}
