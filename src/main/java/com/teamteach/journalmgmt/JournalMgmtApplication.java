package com.teamteach.journalmgmt;

import com.teamteach.commons.security.jwt.config.JwtConfig;
import com.teamteach.commons.security.jwt.config.KeyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
		"com.teamteach.commons.security",
		"com.teamteach.commons.connectors",
		"com.teamteach.journalmgmt"
})
@EnableConfigurationProperties({JwtConfig.class, KeyConfig.class})
public class JournalMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(JournalMgmtApplication.class, args);
	}

}
