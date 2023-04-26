package com.scrumpokerpro.config

import com.nimbusds.jose.shaded.json.JSONArray
import com.nimbusds.jose.shaded.json.JSONObject
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

class GrantedAuthoritiesConverter : JwtAuthenticationConverter() {

    override fun extractAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
        return jwt.getClaim<JSONObject?>(REALM_ACCESS_KEY)?.let { realmAccess ->
            realmAccess[ROLES_KEY]?.let {
                val roles = it as JSONArray
                roles.map { role: Any -> SimpleGrantedAuthority(ROLE_PREFIX + role.toString()) }
            }
        } ?: emptyList()
    }

    companion object {
        const val REALM_ACCESS_KEY = "realm_access"
        const val ROLES_KEY = "roles"
        const val ROLE_PREFIX = "ROLE_"
    }
}
