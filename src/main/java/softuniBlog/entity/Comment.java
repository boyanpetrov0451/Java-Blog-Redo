package softuniBlog.entity;

import org.hibernate.annotations.ValueGenerationType;

import javax.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    private Integer id;

    private String content;

    private User author;

    private Article article;

    public Comment() {

    }

    public Comment(String content, User author, Article article) {
        this.content = content;

        this.author = author;

        this.article = article;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    @Column(columnDefinition = "text", nullable = false)
    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() { return author; }

    public void setAuthor(User author) {
        this.author = author;
    }

    @ManyToOne()
    @JoinColumn(name = "articleId")
    public Article getArticle() { return article; }

    public void setArticle(Article article) { this.article = article; }


}
