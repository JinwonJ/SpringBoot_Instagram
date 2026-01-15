package org.clonestudy.instagram.global.config;

import org.clonestudy.instagram.auth.jwt.JwtProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AppConfig {}