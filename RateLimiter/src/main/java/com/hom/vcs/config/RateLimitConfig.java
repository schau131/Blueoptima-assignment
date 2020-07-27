package com.hom.vcs.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.ehcache.xml.model.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Data;

@Data
@ConstructorBinding
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitConfig {

	@NotBlank
	private final List<ServiceConfig> service;

	private final Map<ServiceKey, List<ServiceConfig>> mapOfStaticService;

	private final Map<ServiceKey, List<ServiceConfig>> mapOfDynamicService;

	private final int defaultNumberOfAllowedRequest;
			
	private final TimeUnit defaultUnit;
		
	private final int defaultUnitValue;
	
	public RateLimitConfig (List<ServiceConfig> service, int defaultNumberOfAllowedRequest, TimeUnit defaultUnit, int defaultUnitValue) 
	{
		this.service = service;
		this.defaultNumberOfAllowedRequest = defaultNumberOfAllowedRequest;
		this.defaultUnit = defaultUnit;
		this.defaultUnitValue = defaultUnitValue;
		
		mapOfStaticService = new HashMap<>();
		mapOfDynamicService = new HashMap<>();
		for(ServiceConfig serviceConfig : service) 
		{
			ServiceKey serviceKey = new ServiceKey(serviceConfig.getUri(), serviceConfig.getRequestType(), serviceConfig.getClientId());
			
			if(serviceConfig.getNumberOfAllowedRequest() == 0 )
				serviceConfig.setNumberOfAllowedRequest(defaultNumberOfAllowedRequest);
			if(serviceConfig.getUnit() == null )
				serviceConfig.setUnit(defaultUnit);
			if(serviceConfig.getUnitValue() == 0 )
				serviceConfig.setUnitValue(defaultUnitValue);
			
			if(serviceConfig.getUriType().equals(URIType.DYNAMIC.name())) 
			{
				if(!mapOfDynamicService.containsKey(serviceKey)) {
					List<ServiceConfig> serviceConfigList = new ArrayList<>();
					serviceConfigList.add(serviceConfig);
					mapOfDynamicService.put(serviceKey, serviceConfigList);
				} else {				
					mapOfDynamicService.get(serviceKey).add(serviceConfig);
				}
			}
			else
			{
				if(!mapOfStaticService.containsKey(serviceKey)) {
					List<ServiceConfig> serviceConfigList = new ArrayList<>();
					serviceConfigList.add(serviceConfig);
					mapOfStaticService.put(serviceKey, serviceConfigList);
				} else {				
					mapOfStaticService.get(serviceKey).add(serviceConfig);
				}
			}
		}
	}

}
