package com.example.springdemo.entities;

import jakarta.persistence.*;
import lombok.*;
import com.example.springdemo.entities.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;
    @Column(nullable = false , name = "email")
    private String email;
    @Column(nullable = false, name = "password")
    private String password;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public <T> User(String email, String password, List<T> ts) {
    }

    public void addAddress(Address address)
    {
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address)
    {
        addresses.remove(address);
        address.setUser(null);
    }

    public void addTag(String tagName ){
        var tag = new Tag(tagName);
        tags.add(tag);
        tag.getUsers().add(this);

    }

    @ManyToMany
    @JoinTable(
         name = "user_tags",
         joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    @Builder.Default
    private Set<Tag> tags= new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Profile profile;


}
