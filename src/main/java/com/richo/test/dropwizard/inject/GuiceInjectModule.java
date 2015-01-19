package com.richo.test.dropwizard.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.richo.test.dropwizard.HelloWorldConfiguration;
import com.richo.test.dropwizard.api.HelloWorldApi;
import com.richo.test.dropwizard.api.HelloWorldResource;

public class GuiceInjectModule extends AbstractModule
{
	private final HelloWorldConfiguration configuration;

	public GuiceInjectModule(HelloWorldConfiguration configuration)
	{

		this.configuration = configuration;
	}

	@Override
	protected void configure()
	{
		bind(HelloWorldApi.class).to(HelloWorldResource.class);
		bind(HelloWorldConfiguration.class).toInstance(configuration);

	}
}
