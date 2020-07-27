package com.hom.vcs.filter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ehcache.xml.model.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hom.vcs.cache.CachedItem;
import com.hom.vcs.config.RateLimitConfig;
import com.hom.vcs.config.RequestType;
import com.hom.vcs.config.ServiceConfig;
import com.hom.vcs.config.ServiceKey;

@Component
public class RateLimitFilter implements Filter {

	@Autowired
	private CachedItem cachedItem;

	@Autowired
	private RateLimitConfig rateLimitConfig;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException 
	{

		LinkedList<LocalDateTime> localDateTimes = new LinkedList<LocalDateTime>();
		boolean ignoreRequest = false;

		if(request instanceof HttpServletRequest) {	
			String uri = ((HttpServletRequest) request).getRequestURI();
			String method = ((HttpServletRequest) request).getMethod();
			String clientId = ((HttpServletRequest) request).getHeader("CLIENT_ID");

			ServiceKey serviceKey = new ServiceKey(uri, RequestType.valueOf(method), clientId);

			if(rateLimitConfig.getMapOfStaticService().containsKey(serviceKey)) 
			{
				List<ServiceConfig> serviceConfigList = rateLimitConfig.getMapOfStaticService().get(serviceKey);
				for(ServiceConfig serviceConfig : serviceConfigList) 
				{
					ignoreRequest = this.checkForRequestValidity(serviceConfig, localDateTimes, serviceKey);
				}
			} 
			else
			{
				for(Map.Entry<ServiceKey, List<ServiceConfig>> entry : rateLimitConfig.getMapOfDynamicService().entrySet()) 
				{
					boolean checked = false;
					if(uri.matches(entry.getKey().getUri()) && method.equals(entry.getKey().getRequestType().name())) 
					{
						List<ServiceConfig> serviceConfigList = entry.getValue();
						for(ServiceConfig serviceConfig : serviceConfigList) 
						{
							if(clientId == null || (clientId != null && serviceConfig.getClientId().equals(clientId)))
							{
								ignoreRequest = this.checkForRequestValidity(serviceConfig, localDateTimes, entry.getKey());
								checked = true;
							}
						}
						if(checked) break;
					}
				}
			}

		}				


		if(ignoreRequest) {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
		} else {
			chain.doFilter(request, response);
		}
	}


	public synchronized boolean checkForRequestValidity(ServiceConfig serviceConfig, LinkedList<LocalDateTime> localDateTimes, ServiceKey serviceKey) 
	{
		int numOfReq = serviceConfig.getNumberOfAllowedRequest();		

		Duration timeDuration = this.duration(serviceConfig.getUnit(), serviceConfig.getUnitValue());
		boolean ignoreRequest = false;

		localDateTimes = cachedItem.getUriTimestamps(serviceKey, localDateTimes);
		LocalDateTime currentLocalDateTime = LocalDateTime.now();

		if(!localDateTimes.isEmpty()) {
			int diff = 1;							
			while(diff > 0) {								
				LocalDateTime firstLocalDateTime = localDateTimes.peekFirst();								
				diff = Duration.between(firstLocalDateTime, currentLocalDateTime).compareTo(timeDuration);

				if(diff > 0) {
					localDateTimes.removeFirst();
				}
			}
			ignoreRequest = checkAndUpdateTimestamp(localDateTimes, numOfReq, currentLocalDateTime, serviceKey);
		} 
		else 
		{
			ignoreRequest = checkAndUpdateTimestamp(localDateTimes, numOfReq, currentLocalDateTime, serviceKey);
		}
		return ignoreRequest;
	}
	
	
	public boolean checkAndUpdateTimestamp(LinkedList<LocalDateTime> localDateTimes, int numOfReq,
			LocalDateTime currentLocalDateTime, ServiceKey serviceKey)
	{
		if(localDateTimes.size() >= numOfReq)
		{
			return true;
		} 
		else
		{
			localDateTimes.add(currentLocalDateTime);
			localDateTimes = cachedItem.updateUriTimestamps(serviceKey, localDateTimes);
		}
		return false;
	}
	

	public Duration duration(TimeUnit unit, int value) {
		switch(unit)
		{
		case SECONDS : return Duration.ofSeconds(value);

		case MINUTES : return Duration.ofMinutes(value);

		case HOURS : return Duration.ofHours(value);

		case DAYS : return Duration.ofDays(value);

		default : return null;
		}		
	}
}
