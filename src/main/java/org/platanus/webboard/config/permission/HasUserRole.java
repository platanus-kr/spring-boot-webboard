package org.platanus.webboard.config.permission;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(org.platanus.webboard.domain.UserRole).ROLE_USER.getKey())")
public @interface HasUserRole {

}
