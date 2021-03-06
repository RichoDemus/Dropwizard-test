package com.richo.test.dropwizard;

import com.richo.test.dropwizard.api.DumbAuthImplementation;
import com.richo.test.dropwizard.api.HelloWorldApi;
import com.richo.test.dropwizard.api.HelloWorldResource;
import com.richo.test.dropwizard.api.RequestScopedResource;
import com.richo.test.dropwizard.authentication.AuthRequestFilter;
import com.richo.test.dropwizard.filter.MyFilter;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public class HelloWorldApplication extends Application<HelloWorldConfiguration>
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws Exception
	{
		new HelloWorldApplication().run(args);
	}

	@Override
	public String getName()
	{
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap)
	{
		AssetsBundle assetsBundle = new AssetsBundle("/assets/", "/", "index.html", "static");
		bootstrap.addBundle(assetsBundle);
	}

	@Override
	public void run(HelloWorldConfiguration configuration,
	                Environment environment)
	{
		enableWadl(environment);

		final HelloWorldApi resource = new HelloWorldResource(
				configuration.getTemplate(),
				configuration.getDefaultName()
		);

		final TemplateHealthCheck healthCheck =
				new TemplateHealthCheck(configuration.getTemplate());
		environment.healthChecks().register("template", healthCheck);

		environment.getApplicationContext().addFilter(MyFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

		environment.admin().addTask(new MyTestTask());

		environment.jersey().register(resource);

		environment.jersey().register(RequestScopedResource.class);
		environment.jersey().register(DumbAuthImplementation.class);

		environment.jersey().getResourceConfig().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(AuthRequestFilter.class);

	}

	private void enableWadl(Environment environment)
	{
		Map<String, Object> props = new HashMap<>();
		props.put("jersey.config.server.wadl.disableWadl", "false");
		environment.jersey().getResourceConfig().addProperties(props);
	}

}