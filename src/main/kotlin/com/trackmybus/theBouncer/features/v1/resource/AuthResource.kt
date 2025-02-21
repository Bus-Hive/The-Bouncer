@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.config.TheBouncer
import io.ktor.resources.Resource

@Resource("/public/auth")
class PublicAuthRoutes(
    val parent: TheBouncer.V1,
) {
    @Resource("/google")
    class Google(
        val parent: PublicAuthRoutes,
    ) : AuthAction()

    @Resource("/facebook")
    class Facebook(
        val parent: PublicAuthRoutes,
    ) : AuthAction()

    @Resource("/apple")
    class Apple(
        val parent: PublicAuthRoutes,
    ) : AuthAction()

    sealed class AuthAction {
        @Resource("/login")
        class Login(
            val idToken: String,
        )

        @Resource("/register")
        class Register(
            val idToken: String,
            val firstName: String,
            val lastName: String,
        )
    }
}
