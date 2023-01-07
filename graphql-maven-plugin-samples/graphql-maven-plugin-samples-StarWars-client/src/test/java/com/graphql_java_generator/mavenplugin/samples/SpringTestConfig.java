package com.graphql_java_generator.mavenplugin.samples;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import com.generated.graphql.QueryTypeExecutor;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.samples.simple.client.Main;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@TestConfiguration
@ComponentScan(basePackageClasses = { Main.class, SpringTestConfig.class, GraphqlClientUtils.class,
		QueryTypeExecutor.class }, excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "graphql\\..*") })
public class SpringTestConfig {

	/** Logger for this class */
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * This sample uses in https, but with a self-signed certificate. So we need to avoid certificate controls. This
	 * {@link HttpClient} just removes control on the certificate. <BR/>
	 * This is ok for this integration test. But DON'T DO THAT IN PRODUCTION!
	 * 
	 * @return
	 * @throws SSLException
	 */
	@Bean
	HttpClient insecureHttpClient() throws SSLException {
		int method = 2;

		if (method == 1) {

			// logger.debug("Activating Proxy for the Spring WebClient");
			// tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host("127.0.0.1").port(3128));

			logger.debug("Activating INSECURE SSL for the Spring WebClient");
			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			TcpClient tcpClient = TcpClient.create()
					.secure(sslProviderBuilder -> sslProviderBuilder.sslContext(sslContext));

			return HttpClient.from(tcpClient);
		} else if (method == 2) {
			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
			return httpClient;
		} else {
			throw new RuntimeException("bad value");
		}
	}

}
