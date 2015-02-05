package com.richo.test.dropwizard.authentication;

import com.auth0.jwt.JWTVerifier;
import com.richo.test.dropwizard.authentication.exception.InvalidTokenException;
import com.richo.test.dropwizard.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class AuthRequestFilter implements ContainerRequestFilter
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
/*
	@Context
	HttpServletRequest webRequest;
*/
	public AuthRequestFilter()
	{
		logger.info("Constructor called");
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException
	{
		logger.trace("filter called");

		final Optional<String> encodedToken = getHeader(requestContext);
		logger.trace("token header is: {}", encodedToken.orElse("not present"));

		if(!encodedToken.isPresent())
		{
			requestContext.setSecurityContext(new MySecurityContext(Optional.<User>empty()));
			return;
		}


		final Optional<User> user;
        user = getUser(encodedToken.get());
        if(!user.isPresent())
        {
            final Response.Status unauthorized = Response.Status.UNAUTHORIZED;
            requestContext.abortWith(Response.status(unauthorized).build());
            return;
        }
        
		requestContext.setSecurityContext(new MySecurityContext(user));
	}

	private Optional<String> getHeader(ContainerRequestContext requestContext)
	{
		return Optional.ofNullable(requestContext.getHeaderString("x-token-jwt"));
	}

	private Optional<User> getUser(String encodedToken)
    {
		/**
		 * TODO use injected authentication manager instead
		 * and make sure this token is not blacklisted
		 */

		final Map<String, Object> claims;
		try
		{
			claims = new JWTVerifier(AuthenticationManager.SECRET).verify(encodedToken);

		}
		catch (Exception e)
		{
			logger.warn("Token invalid, wont add any user to request");
			return Optional.empty();
		}

		return Optional.of(new User((String)claims.get("user"), (String)claims.get("role")));
	}
}