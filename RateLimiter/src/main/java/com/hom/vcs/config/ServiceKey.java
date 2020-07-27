package com.hom.vcs.config;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ServiceKey implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@NotBlank	
	private final String uri;
	
	@NotBlank
	private final RequestType requestType;
	
	@NotBlank
	private final String clientId;

}
