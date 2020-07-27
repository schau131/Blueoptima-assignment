package com.hom.vcs.cache;

import java.time.LocalDateTime;
import java.util.LinkedList;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.hom.vcs.config.ServiceKey;

@Component
public class CachedItem {

	@Cacheable(value="uriTimestamps", key="#serviceKey")
	public LinkedList<LocalDateTime> getUriTimestamps(ServiceKey serviceKey ,LinkedList<LocalDateTime> localDateTimes) {		
		return localDateTimes;
	}
	
	
	@CachePut(value="uriTimestamps", key="#serviceKey")
	public LinkedList<LocalDateTime> updateUriTimestamps(ServiceKey serviceKey ,LinkedList<LocalDateTime> localDateTimes) {		
		return localDateTimes;
	}

}
