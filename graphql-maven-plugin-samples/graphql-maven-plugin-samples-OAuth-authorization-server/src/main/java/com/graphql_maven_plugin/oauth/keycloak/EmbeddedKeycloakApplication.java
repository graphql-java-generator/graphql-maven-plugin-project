package com.graphql_maven_plugin.oauth.keycloak;

import java.util.NoSuchElementException;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.graphql_maven_plugin.oauth.keycloak.KeycloakServerProperties.AdminUser;

/**
 * <PRE>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.jboss.resteasy</groupId>
			<artifactId>resteasy-jackson2-provider</artifactId>
			<version>3.12.1.Final</version>
		</dependency>
		<dependency>
			<groupId>org.keycloak</groupId>
			<artifactId>keycloak-dependencies-server-all</artifactId>
			<version>11.0.2</version>
			<type>pom</type>
		</dependency>
 * </PRE>
 * 
 * @See https://www.baeldung.com/keycloak-embedded-in-spring-boot-apps
 */
public class EmbeddedKeycloakApplication extends KeycloakApplication {
	private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);
	static KeycloakServerProperties keycloakServerProperties;

	@Override
	protected void loadConfig() {
		JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
		Config.init(factory.create().orElseThrow(() -> new NoSuchElementException("No value present")));
	}

	public EmbeddedKeycloakApplication() {
		createMasterRealmAdminUser();
		createBaeldungRealm();
	}

	private void createMasterRealmAdminUser() {
		KeycloakSession session = getSessionFactory().create();
		ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
		AdminUser admin = keycloakServerProperties.getAdminUser();
		try {
			session.getTransactionManager().begin();
			applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
			session.getTransactionManager().commit();
		} catch (Exception ex) {
			LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
			session.getTransactionManager().rollback();
		}
		session.close();
	}

	private void createBaeldungRealm() {
		KeycloakSession session = getSessionFactory().create();
		try {
			session.getTransactionManager().begin();
			RealmManager manager = new RealmManager(session);
			Resource lessonRealmImportFile = new ClassPathResource(keycloakServerProperties.getRealmImportFile());
			manager.importRealm(
					JsonSerialization.readValue(lessonRealmImportFile.getInputStream(), RealmRepresentation.class));
			session.getTransactionManager().commit();
		} catch (Exception ex) {
			LOG.warn("Failed to import Realm json file: {}", ex.getMessage());
			session.getTransactionManager().rollback();
		}
		session.close();
	}
}