package com.richo.test.dropwizard.api;

import com.richo.test.dropwizard.authentication.AuthenticationManager;
import com.richo.test.dropwizard.authentication.exception.InvalidTokenException;
import com.richo.test.dropwizard.authentication.exception.InvalidUserNameOrPasswordException;
import com.richo.test.dropwizard.model.JWTToken;
import com.richo.test.dropwizard.model.Token;
import com.richo.test.dropwizard.persistence.SimpleSetBlacklist;
import com.richo.test.dropwizard.persistence.TokenBlacklist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;

/**
 * Created by richard.tjerngren on 2015-01-16.
 */
public class DumbAuthImplementation implements Authentication
{
	/**
	 * TODO inject instead, WARNING since this is not injected and resources are request-scope,
	 * the blacklist is cleared after each request
	 */
	private final AuthenticationManager manager;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Context
	HttpServletRequest request;

	/**
	 * This is used as a ghetto dependency injection....
	 * TODO remove
	 */
	public DumbAuthImplementation()
	{
		manager = new AuthenticationManager(SimpleSetBlacklist.getInstance());
	}

	public DumbAuthImplementation(TokenBlacklist blacklist)
	{
		manager = new AuthenticationManager(blacklist);
	}

	@Override
	public Token login(String username, String password)
	{
		logger.debug("User {} attempting to login", username);
		try
		{
			return manager.login(username, password);
		}
		catch (InvalidUserNameOrPasswordException e)
		{
			throw new BadRequestException("Invalid username or password");
		}
	}

	@Override
	public Token refreshToken()
	{
		final String token = request.getHeader("x-token-jwt");

		logger.debug("Asked to refresh token {}", token);
		try
		{
			return manager.getNewToken(new JWTToken(token));
		}
		catch (InvalidTokenException e)
		{
			throw new BadRequestException("Token invalid");
		}
	}

	@Override
	public void logout()
	{
		final String token = request.getHeader("x-token-jwt");
		logger.debug("Logging out {}", token);
		manager.logout(new JWTToken(token));
	}
}
