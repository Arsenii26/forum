package com.arsenii.usermicroservice.model;


import com.arsenii.usermicroservice.json.CustomAuthorityDeserializer;
import com.arsenii.usermicroservice.model.security.Authority;
import com.arsenii.usermicroservice.validation.ValidEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    private String firstName;
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
//    @ValidEmail
    private String email;
    private String phone;

    private boolean enabled=true;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Collection<Role> roles;

    // https://stackoverflow.com/questions/36168010/spring-securitycan-not-construct-instance-of-org-springframework-security-core
    // https://stackoverflow.com/questions/18096589/json-jsonmappingexception-while-try-to-deserialize-object-with-null-values
    // https://stackoverflow.com/questions/45918309/jackson-get-a-null-reference-on-deserializing/45919445
//   https://stackoverflow.com/questions/5489532/jackson-json-library-how-to-instantiate-a-class-that-contains-abstract-fields
    @Override
//    @JsonIgnore
//    @JsonDeserialize(as=Cat.class)
//    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // https://stackoverflow.com/questions/12505141/only-using-jsonignore-during-serialization-but-not-deserialization
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(ur -> authorities.add(new Authority(ur.getName())));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
