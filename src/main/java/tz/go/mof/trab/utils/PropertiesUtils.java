package tz.go.mof.trab.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource({"classpath:log4j.properties"})
public class PropertiesUtils {
 @Bean
 public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
  return new PropertySourcesPlaceholderConfigurer();
 }
}