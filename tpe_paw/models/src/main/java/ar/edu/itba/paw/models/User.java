package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "users")
public class User {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").withLocale(Locale.UK).withZone(ZoneId.systemDefault());

    //TODO: Check correct sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(allocationSize = 1, sequenceName = "users_id_seq", name="users_id_seq")
    private Long id;

    @Column(length = 30, name="username")
    private String username;

    @Column(length=60, name="password")
    private String password;

    @Column(length = 60, name="email")
    private String email;

    @Column(length = 300, name="description")
    private String description;

    @Column(name="reputation")
    private int reputation;

    @Column(name = "date_joined")
    private Timestamp dateJoined;

    @Column(name="icon")
    private byte[] icon;

    @Column(name="verified")
    private int verified;

    @Column(length = 5, name="lang")
    private String lang = "en";     // Setting "en" as the default language value

    @Column(length = 5, name="region")
    private String region = "US";   // Setting "US" as the default location value

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
        name= "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<Vote> votes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name= "favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "snippet_id"))
    private Collection<Snippet> favorites;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Collection<Tag> followedTags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.PERSIST)
    private Collection<Snippet> createdSnippets;

    protected User() {
        // Hibernate constructor
    }

    public User(String username, String password, String email, Timestamp dateJoined, Locale locale, boolean verified) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = "";
        this.reputation = 0;
        this.dateJoined = dateJoined;
        this.lang = locale.getLanguage();
        this.region = locale.getCountry();
        this.verified = verified ? 1 : 0;
    }

    @Deprecated
    public User(long id, String username, String password, String email, String dateJoined, byte[] icon, Locale locale, boolean verified) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = "";
        this.reputation = 0;
//        this.dateJoined = dateJoined;
        this.icon = icon;
//        this.locale = locale;
        this.verified = verified ? 1 : 0;
    }

    @Deprecated
    public User(long id, String username, String password, String email, String description, int reputation, String dateJoined, byte[] icon, Locale locale, boolean verified) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = description;
        this.reputation = reputation;
//        this.dateJoined = dateJoined;
        this.icon = icon;
//        this.locale = locale;
        this.verified = verified ? 1 : 0;
    }

    public Long getId() { return id; }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    /**
     * Returns the string representation of the creation date
     * @return
     */
    public String getDateJoined(){
        return DATE.format(this.dateJoined.toInstant());
    }

    public Locale getLocale(){
        return new Locale(this.lang, this.region);
    }

    public byte[] getIcon() { return this.icon; }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public boolean isVerified() {
        return verified == 1;
    }

    public void setVerified(boolean verified) {
        this.verified = verified ? 1 : 0;
    }

    public Collection<Snippet> getCreatedSnippets() {
        return this.createdSnippets;
    }

    public Collection<Tag> getFollowedTags() {
        return this.followedTags;
    }

    public Collection<Snippet> getFavorites() {
        return this.favorites;
    }

    public Collection<Role> getRoles() {
        return this.roles;
    }

    public void addFavorite(Snippet snippet) {
        this.getFavorites().add(snippet);
        snippet.getUserFavorites().add(this);
    }

    public void removeFavorite(Snippet snippet) {
        this.getFavorites().remove(snippet);
        snippet.getUserFavorites().remove(this);
    }

    public void addRole(Role role) {
        this.getRoles().add(role);
        role.getUsersWithRole().add(this);
    }

    public void followTag(Tag tag) {
        this.getFollowedTags().add(tag);
        tag.getFollowingUsers().add(this);
    }

    public void unfollow(Tag tag) {
        this.getFollowedTags().remove(tag);
        tag.getFollowingUsers().remove(this);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return this.getId().equals(user.getId());
    }

    public Collection<Vote> getVotes() {
        return votes;
    }
}
