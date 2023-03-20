package com.graphql_java_generator.mavenplugin.samples;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;

import com.generated.graphql.QueryTypeExecutor;
import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.samples.simple.client.Main;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@TestConfiguration
@ComponentScan(basePackageClasses = { Main.class, SpringTestConfig.class, GraphQLConfiguration.class,
		QueryTypeExecutor.class }, excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "graphql\\..*") })
public class SpringTestConfig {

	@Value("${trust.store}")
	private Resource trustStore;

	@Value("${trust.store.password}")
	private String trustStorePassword;

	/** Logger for this class */
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * This sample uses in https, but with a self-signed certificate. So we use the keystore that contains the
	 * certificate.
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 */
	@Bean
	HttpClient sslHttpClient() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
			UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		try (InputStream inputStream = trustStore.getInputStream()) {
			keyStore.load(inputStream, trustStorePassword.toCharArray());
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, trustStorePassword.toCharArray());
		SslContext sslContext = SslContextBuilder.forClient().keyManager(keyManagerFactory).build();

		return HttpClient.create().secure(t -> t.sslContext(sslContext));
	}

}
