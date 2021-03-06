package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_id_seq")
    @SequenceGenerator(allocationSize = 1, sequenceName = "tags_id_seq", name="tags_id_seq")
    private Long id;

    @Column(name = "name", length = 30, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags", cascade = CascadeType.PERSIST)
    private Collection<Snippet> snippetsUsing = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "followedTags", cascade = CascadeType.PERSIST)
    private Collection<User> followingUsers = new HashSet<>();

    private Boolean snippetsUsingIsEmpty = null;

    public Tag(String name) {
        this.name = name;
    }

    /* Constructor for Service Tests */
    public Tag(long id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Tag(){
        // Hibernate constructor
    }

    public Long getId() { return this.id; }

    public String getName() { return this.name; }

    public Collection<Snippet> getSnippetsUsing(){ return this.snippetsUsing; }

    public Collection<User> getFollowingUsers() {
        return followingUsers;
    }

    public Boolean getSnippetsUsingIsEmpty() {
        return snippetsUsingIsEmpty;
    }

    public void setSnippetsUsingIsEmpty(Boolean snippetsUsingIsEmpty) {
        this.snippetsUsingIsEmpty = snippetsUsingIsEmpty;
    }

    public void setFollowingUsers(Collection<User> followingUsers) {
        this.followingUsers= followingUsers;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        return this.getId().equals(tag.getId());
    }

    @Override public int hashCode() {
        return this.getId().hashCode();
    }

    /**
     * Returns a brief description of this tag. The exact details
     * of the representation are unspecified and subject to change,
     * but the following may be regarded as typical:
     *
     * "[Tag #12: name=algorithm]"
     */
    @Override public String toString() {
        return String.format("Tag #%d: name=%s]", this.getId(), this.getName());
    }
}
