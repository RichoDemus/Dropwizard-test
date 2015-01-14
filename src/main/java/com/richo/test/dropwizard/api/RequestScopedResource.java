package com.richo.test.dropwizard.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class RequestScopedResource implements RequestScopedApi
{
	private static final AtomicLong initCounter = new AtomicLong();
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public RequestScopedResource()
	{
		logger.info("{} number {} created", getClass().getSimpleName(), initCounter.incrementAndGet());
	}

	@Override
	public String test()
	{
		logger.info("{}.test called", getClass().getSimpleName());
		return "OK";
	}
}
