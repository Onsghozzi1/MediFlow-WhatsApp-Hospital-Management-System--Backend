package com.example.MediFlow.config;
import com.example.MediFlow.entity.enums.Roles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public final class AuthenticationConstants {
    public static final Set<Roles> ADMINTATION_ROLES = new HashSet<>(
            Arrays.asList(
                    Roles.ADMIN,
                    Roles.DOCTOR
            ));
    public static final long EXPIRE_TOKEN_AFTER_MINUTES = 15;
}
