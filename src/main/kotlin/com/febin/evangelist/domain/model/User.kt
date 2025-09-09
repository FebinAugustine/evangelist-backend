package com.febin.evangelist.domain.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    var password: String? = null,

    @Enumerated(EnumType.STRING)
    var status: AccountStatus = AccountStatus.UNVERIFIED,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    var provider: UserProvider = UserProvider.LOCAL,

    @Column(name = "provider_id")
    var providerId: String? = null,

    var verificationCode: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf(),

    @Transient
    private var attributes: Map<String, Any> = emptyMap()

) : UserDetails, OAuth2User {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority(it.name) }
    }

    override fun getPassword(): String? = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = status != AccountStatus.DISABLED

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = status == AccountStatus.ACTIVE

    override fun getName(): String = email

    override fun getAttributes(): Map<String, Any> = attributes

    fun setAttributes(attributes: Map<String, Any>) {
        this.attributes = attributes
    }
}
