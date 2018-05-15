package com.loxodon.crawler.facebook.groupmembers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 
 * @author Loay
 *
 */
@SpringBootApplication
public class GroupMembersCrawler {

	public static void main(String[] args) {
		SpringApplication.run(GroupMembersCrawler.class, args);
	}

	@Bean
	public CommandLineRunner init() {

		return new CommandLineRunner() {
			public void run(String... strings) throws Exception {
				String accessToken = "EAACEdEose0cBAM0NlXDazrE6QEHE0QcfjtvdZC70fFUarJoLlzINEZCY58iCRnbPZAjNmUSZCpWB08yPahr9CNMZAEglLC0KLdTenjysu4fGYPOtOpszNebzE2A9r7OjeI6MK7WpUx19iA1lOODiZAgDIib7AZArKwU7FAHJhXQfVVJ6rYCIkT7d3A0FYKruIUZD";
				String[] fields = { "id", "name", "first_name", "last_name", "middle_name", "link", "username",
						"birthday", "hometown", "location", "bio", "quotes", "education", "work", "gender",
						"relationship_status", "languages", "email", "religion" };
				;
				String[] groupIDs = { "234214686628695" };
				FacebookJsonParser crawlFB = new FacebookJsonParser(accessToken, fields, groupIDs);

				crawlFB.crawlJson(CrawlOptions.GROUP);
			}
		};

	}
}
