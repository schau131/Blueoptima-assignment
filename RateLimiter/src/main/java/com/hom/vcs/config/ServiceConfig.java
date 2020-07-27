package com.hom.vcs.config;

import javax.validation.constraints.NotBlank;

import org.ehcache.xml.model.TimeUnit;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@ConstructorBinding
public class ServiceConfig {
	
	@NotBlank	
	private final String uri;
	
	@NotBlank	
	private final String uriType;
	
	@NotBlank
	private final RequestType requestType;
	
	private int numberOfAllowedRequest;
		
	private TimeUnit unit;
		
	private int unitValue;
	
	private final String clientId;
	
}
