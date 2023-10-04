package it.polimi.db2.marketing.entities;

import javax.persistence.*;

@Entity
@Table(name = "offensive_words", schema = "marketinggame")
@NamedQuery(name = "Offensive_words.getAll", query = "SELECT w.word FROM OffensiveWord w")
public class OffensiveWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public String getWord() {
        return word;
    }

    private String word;
}
